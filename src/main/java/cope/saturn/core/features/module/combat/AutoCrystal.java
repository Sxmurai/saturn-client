/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.asm.duck.IPlayerInteractEntityC2SPacket;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.EntityUtil;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.entity.player.rotation.RotationUtil;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.network.NetworkUtil;
import cope.saturn.util.world.BlockUtil;
import cope.saturn.util.world.combat.CrystalUtil;
import cope.saturn.util.world.combat.DamagesUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT, "Automatically places and explode end crystals");
    }

    public static final Setting<Boolean> place = new Setting<>("Place", true);
    public static final Setting<Float> placeSpeed = new Setting<>(place, "PlaceSpeed", 20.0f, 0.1f, 20.0f);
    public static final Setting<Double> placeRange = new Setting<>(place, "PlaceRange", 4.5, 1.0, 6.0);
    public static final Setting<Double> placeWalls = new Setting<>(place, "PlaceWalls", 3.0, 1.0, 6.0);
    public static final Setting<Boolean> raycast = new Setting<>(place, "Raycast", false);
    public static final Setting<Boolean> strictDirection = new Setting<>(place, "StrictDirection", false);
    public static final Setting<CrystalUtil.Placement> type = new Setting<>(place, "Type", CrystalUtil.Placement.UPDATED);
    public static final Setting<Boolean> faceplace = new Setting<>(place, "Faceplace", true);
    public static final Setting<Float> faceplaceHealth = new Setting<>(faceplace, "FaceplaceHealth", 14.0f, 1.0f, 20.0f);
    public static final Setting<Float> faceplaceDamage = new Setting<>(faceplace, "FaceplaceDamage", 2.0f, 1.0f, 4.0f);

    public static final Setting<Boolean> explode = new Setting<>("Explode", true);
    public static final Setting<Float> explodeSpeed = new Setting<>(explode, "ExplodeSpeed", 20.0f, 0.1, 20.0f);
    public static final Setting<Integer> existed = new Setting<>(explode, "Existed", 0, 0, 10);
    public static final Setting<Double> explodeRange = new Setting<>(explode, "ExplodeRange", 4.5, 1.0, 6.0);
    public static final Setting<Double> explodeWalls = new Setting<>(explode, "ExplodeWalls", 3.0, 1.0, 6.0);
    public static final Setting<Weakness> weakness = new Setting<>(explode, "Weakness", Weakness.PACKET);

    public static final Setting<Object> damage = new Setting<>("Damage", null);
    public static final Setting<Float> minDamage = new Setting<>(damage, "MinDamage", 4.0f, 1.0f, 20.0f);
    public static final Setting<Float> maxLocal = new Setting<>(damage, "MaxLocal", 10.0f, 1.0f, 20.0f);
    public static final Setting<Float> localBias = new Setting<>(damage, "LocalBias", 0.5f, 0.0f, 3.0f);

    public static final Setting<Rotate> rotate = new Setting<>("Rotate", Rotate.PACKET);
    public static final Setting<Boolean> limitPause = new Setting<>(rotate, "LimitPause", true);
    public static final Setting<Float> threshold = new Setting<>(rotate, "Threshold", 55.0f, 1.0f, 180.0f);

    public static final Setting<Targeting> targeting = new Setting<>("Targeting", Targeting.DAMAGE);
    public static final Setting<Double> targetRange = new Setting<>(targeting, "TargetRange", 10.0, 1.0, 20.0);
    public static final Setting<Boolean> naked = new Setting<>(targeting, "Naked", false);

    public static final Setting<Sync> sync = new Setting<>("Sync", Sync.SOUND);

    public static final Setting<InventoryManager.Swap> swap = new Setting<>("Swap", InventoryManager.Swap.CLIENT);

    private PlayerEntity target = null;
    private final List<EndCrystalEntity> explodable = new CopyOnWriteArrayList<>();

    private BlockPos placePosition = null;
    private EndCrystalEntity attackCrystal = null;

    private final Stopwatch placeTimer = new Stopwatch();
    private final Stopwatch explodeTimer = new Stopwatch();

    private int oldSlot = -1;
    private Hand hand;

    @Override
    protected void onDisable() {
        super.onDisable();

        target = null;
        explodable.clear();

        placePosition = null;
        attackCrystal = null;

        if (!nullCheck() && oldSlot != -1) {
            swapBack();
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof IPlayerInteractEntityC2SPacket packet) {
            if (!sync.getValue().equals(Sync.INSTANT)) {
                return;
            }

            if (packet.getType().equals(PlayerInteractEntityC2SPacket.InteractType.ATTACK) &&
                    packet.getEntity() instanceof EndCrystalEntity crystalEntity) {

                if (attackCrystal != null && attackCrystal.equals(crystalEntity)) {
                    // crystalEntity.kill();
                    attackCrystal = null;
                }
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            if (!sync.getValue().equals(Sync.SOUND)) {
                return;
            }

            if (packet.getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE)) {
                mc.world.getEntities().forEach((entity) -> {
                    if (!(entity instanceof EndCrystalEntity crystal)) {
                        return;
                    }

                    if (entity.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < 12.0) {
                        // entity.kill();
                        if (attackCrystal != null && attackCrystal.equals(crystal)) {
                            attackCrystal = null;
                        }
                    }
                });
            }
        } else if (event.getPacket() instanceof ExplosionS2CPacket packet) {
            mc.world.getEntities().forEach((entity) -> {
                if (!(entity instanceof EndCrystalEntity crystal)) {
                    return;
                }

                if (entity.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < packet.getRadius() * packet.getRadius()) {
                    // entity.kill();
                    if (attackCrystal != null && attackCrystal.equals(crystal)) {
                        attackCrystal = null;
                    }
                }
            });
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        // map crystals
        mapExplodableCrystals();

        // find our target
        findTarget();

        // calculate place positions
        if (place.getValue()) {
            findPlacePosition();

            if (placePosition != null) {
                // we only want to swap when theres a valid crystal
                swapToCrystal();
                if (hand == null) {
                    return;
                }

                if (placeTimer.getPassedMs() / 50.0f >= 20.0f - placeSpeed.getValue()) {
                    placeTimer.reset();

                    // TODO: limit rotations
                    RotationUtil.rotation(new Vec3d(placePosition.getX(), placePosition.getY(), placePosition.getZ()))
                            .type(rotate.getValue().type)
                            .send(false);

                    // place
                    getSaturn().getInteractionManager().placeCrystal(
                            placePosition, hand, true, raycast.getValue(), strictDirection.getValue());

                    // reset place position
                    placePosition = null;

                    // we need to swap back for silent swap
                    if (swap.getValue().equals(InventoryManager.Swap.PACKET) && hand.equals(Hand.MAIN_HAND)) {
                        swapBack();
                        hand = Hand.MAIN_HAND;
                    }
                }
            }
        }

        if (explode.getValue()) {
            findBestCrystal();

            if (attackCrystal != null) {
                int oSlot = -1;
                if (placePosition == null && mc.player.hasStatusEffect(StatusEffects.WEAKNESS) && !weakness.getValue().equals(Weakness.NONE)) {
                    int slot = InventoryUtil.getSlot(SwordItem.class, false);
                    if (slot != -1) {
                        oSlot = getSaturn().getInventoryManager().getServerSlot();
                        getSaturn().getInventoryManager().swap(slot, weakness.getValue().swap);
                    }
                }

                if (explodeTimer.getPassedMs() / 50.0f >= 20.0f - explodeSpeed.getValue()) {
                    explodeTimer.reset();

                    RotationUtil.rotation(attackCrystal.getEyePos())
                            .type(rotate.getValue().type)
                            .send(false);

                    // attack
                    NetworkUtil.sendPacket(PlayerInteractEntityC2SPacket.attack(attackCrystal, mc.player.isSneaking()));

                    mc.player.swingHand(Hand.MAIN_HAND);

                    if (oSlot != -1) {
                        getSaturn().getInventoryManager().swap(oSlot, weakness.getValue().swap);
                    }
                }
            }
        }
    }

    private void findBestCrystal() {
        EndCrystalEntity crystal = null;

        for (EndCrystalEntity c : explodable) {
            if (c == null || !c.isAlive() || c.age < existed.getValue()) {
                continue;
            }

            double dist = mc.player.canSee(c) ? explodeWalls.getValue() : explodeRange.getValue();
            if (mc.player.squaredDistanceTo(c) > dist * dist) {
                continue;
            }

            if (crystal == null) {
                crystal = c;
            } else {
                if (dist < mc.player.squaredDistanceTo(crystal)) {
                    crystal = c;
                }
            }
        }

        attackCrystal = crystal;
    }

    /**
     * Finds the best place position for an end crystal
     */
    private void findPlacePosition() {
        if (target == null && targeting.getValue().equals(Targeting.DISTANCE)) {
            return;
        }

        BlockPos placementPos = null;
        float damage = 0.5f;

        for (BlockPos pos : BlockUtil.sphere(mc.player.getBlockPos(), placeRange.getValue().intValue())) {
            if (!CrystalUtil.canPlace(pos, type.getValue())) {
                continue;
            }

            Vec3d explosionOffset = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);

            float localDamage = DamagesUtil.crystalDamage(explosionOffset, mc.player) + localBias.getValue();
            if (localDamage > EntityUtil.getHealth(mc.player) || localDamage > maxLocal.getValue()) {
                continue;
            }

            float targetDamage = 0.5f;
            if (target != null) {
                targetDamage = DamagesUtil.crystalDamage(explosionOffset, target);
                if (localDamage > targetDamage || targetDamage < minDamage.getValue()) {
                    continue;
                }
            } else {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    float playerDamage = DamagesUtil.crystalDamage(explosionOffset, player);
                    if (playerDamage > targetDamage) {
                        targetDamage = playerDamage;
                        target = player;
                    }
                }
            }

            if (targetDamage > damage) {
                damage = targetDamage;
                placementPos = pos;
            }
        }

        if (placementPos == null && faceplace.getValue() &&
                target != null && EntityUtil.getHealth(target) <= faceplaceHealth.getValue()) {

            for (Direction direction : Direction.values()) {
                if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
                    continue;
                }

                BlockPos pos = target.getBlockPos().offset(direction);
                if (!CrystalUtil.canPlace(pos, type.getValue())) {
                    continue;
                }

                Vec3d explosionOffset = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);

                float localDamage = DamagesUtil.crystalDamage(explosionOffset, mc.player) + localBias.getValue();
                if (localDamage > EntityUtil.getHealth(mc.player) || localDamage > maxLocal.getValue()) {
                    continue;
                }

                float targetDamage = DamagesUtil.crystalDamage(explosionOffset, target);
                if (localDamage > targetDamage || targetDamage < faceplaceDamage.getValue()) {
                    continue;
                }

                if (targetDamage > damage) {
                    damage = targetDamage;
                    placementPos = pos;
                }
            }
        }

        placePosition = placementPos;
    }

    /**
     * Maps all explodable crystals by us into a list
     */
    private void mapExplodableCrystals() {
        explodable.clear();

        mc.world.getEntities().forEach((entity) -> {
            if (!(entity instanceof EndCrystalEntity crystalEntity)) {
                return;
            }

            if (!crystalEntity.isAlive()) {
                return;
            }

            if (crystalEntity.age < existed.getValue()) {
                return;
            }

            double range = mc.player.canSee(crystalEntity) ? explodeWalls.getValue() : explodeRange.getValue();
            if (mc.player.squaredDistanceTo(crystalEntity) > range * range) {
                return;
            }

            // add crystal
            explodable.add(crystalEntity);
        });
    }

    /**
     * Finds a valid target
     */
    private void findTarget() {
        PlayerEntity possibleTarget = target;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player.equals(mc.player) || getSaturn().getFriendManager().isFriend(player.getUuid())) {
                continue;
            }

            double distance = mc.player.squaredDistanceTo(player);

            if (mc.player.squaredDistanceTo(player) > targetRange.getValue() * targetRange.getValue()) {
                continue;
            }

            if (!naked.getValue() && player.getArmor() == 0) {
                continue;
            }

            if (possibleTarget == null) {
                possibleTarget = player;
            } else {
                if (targeting.getValue().equals(Targeting.DISTANCE)) {
                    if (distance < mc.player.squaredDistanceTo(possibleTarget)) {
                        possibleTarget = player;
                    }
                }
            }
        }

        target = possibleTarget;
    }

    private void swapToCrystal() {
        if (!swap.getValue().equals(InventoryManager.Swap.NONE)) {
            int slot = InventoryUtil.getSlot(Items.END_CRYSTAL, true);
            if (slot == -1) {
                hand = null;
                return;
            }

            hand = slot == InventoryUtil.OFFHAND_SLOT ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (hand.equals(Hand.MAIN_HAND)) {
                oldSlot = mc.player.getInventory().selectedSlot;
                getSaturn().getInventoryManager().swap(slot, swap.getValue());
            }
        } else {
            if (!InventoryUtil.isHolding(Items.END_CRYSTAL, true)) {
                hand = null;
                return;
            }

            if (mc.player.getOffHandStack().getItem().equals(Items.END_CRYSTAL)) {
                hand = Hand.OFF_HAND;
            } else {
                hand = Hand.MAIN_HAND;
            }
        }
    }

    private void swapBack() {
        if (oldSlot != -1) {
            getSaturn().getInventoryManager().swap(oldSlot, swap.getValue());
        }

        oldSlot = -1;
        hand = null;
    }

    public enum Weakness {
        NONE(InventoryManager.Swap.NONE),
        PACKET(InventoryManager.Swap.PACKET),
        CLIENT(InventoryManager.Swap.CLIENT);

        private final InventoryManager.Swap swap;

        Weakness(InventoryManager.Swap swap) {
            this.swap = swap;
        }
    }

    public enum Rotate {
        NONE(RotationType.NONE),
        CLIENT(RotationType.CLIENT),
        PACKET(RotationType.PACKET),
        LIMIT(RotationType.PACKET);

        private final RotationType type;

        Rotate(RotationType type) {
            this.type = type;
        }
    }

    public enum Targeting {
        DISTANCE,
        DAMAGE
    }

    public enum Sync {
        NONE,
        SOUND,
        INSTANT
    }
}
