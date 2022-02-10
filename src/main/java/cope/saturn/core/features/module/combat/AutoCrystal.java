/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.asm.mixins.network.packet.c2s.IPlayerInteractEntityC2SPacket;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.EntityUtil;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.entity.player.rotation.RotationUtil;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.world.BlockUtil;
import cope.saturn.util.world.combat.CrystalUtil;
import cope.saturn.util.world.combat.DamagesUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
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

    public static final Setting<Boolean> explode = new Setting<>("Explode", true);
    public static final Setting<Float> explodeSpeed = new Setting<>(explode, "ExplodeSpeed", 20.0f, 0.1, 20.0f);
    public static final Setting<Integer> existed = new Setting<>(explode, "Existed", 0, 0, 10);
    public static final Setting<Double> explodeRange = new Setting<>(explode, "ExplodeRange", 4.5, 1.0, 6.0);
    public static final Setting<Double> explodeWalls = new Setting<>(explode, "ExplodeWalls", 3.0, 1.0, 6.0);

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

    private PlayerEntity target = null;
    private final List<EndCrystalEntity> explodable = new CopyOnWriteArrayList<>();

    private BlockPos placePosition = null;
    private EndCrystalEntity attackCrystal = null;

    private final Stopwatch placeTimer = new Stopwatch();
    private final Stopwatch explodeTimer = new Stopwatch();

    @Override
    protected void onDisable() {
        super.onDisable();

        target = null;
        explodable.clear();

        placePosition = null;
        attackCrystal = null;
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {
            if (!sync.getValue().equals(Sync.INSTANT)) {
                return;
            }

            if (((IPlayerInteractEntityC2SPacket) packet).getType().equals(PlayerInteractEntityC2SPacket.InteractType.ATTACK)) {
                if (mc.world.getEntityById(((IPlayerInteractEntityC2SPacket) packet).getEntityId()) instanceof EndCrystalEntity crystalEntity) {
                    if (attackCrystal != null && attackCrystal.equals(crystalEntity)) {
                        crystalEntity.kill();
                        mc.world.removeEntity(attackCrystal.getId(), Entity.RemovalReason.KILLED);

                        attackCrystal = null;
                    }
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
                        entity.kill();
                        mc.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED);

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
                    entity.kill();
                    mc.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED);

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

            if (placePosition != null && placeTimer.getPassedMs() / 50.0f >= 20.0f - placeSpeed.getValue()) {
                placeTimer.reset();

                // TODO: limit rotations
                RotationUtil.rotation(new Vec3d(placePosition.getX(), placePosition.getY(), placePosition.getZ()))
                        .type(rotate.getValue().type)
                        .send(false);

                // place
                getSaturn().getInteractionManager().placeCrystal(
                        placePosition, Hand.MAIN_HAND, true, raycast.getValue(), strictDirection.getValue());
            }
        }

        if (explode.getValue()) {
            findBestCrystal();

            if (attackCrystal != null && explodeTimer.getPassedMs() / 50.0f >= 20.0f - explodeSpeed.getValue()) {
                explodeTimer.reset();

                RotationUtil.rotation(attackCrystal.getEyePos())
                        .type(rotate.getValue().type)
                        .send(false);

                // attack
                mc.interactionManager.attackEntity(mc.player, attackCrystal);

                mc.player.swingHand(Hand.MAIN_HAND);
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
            if (player == null || player.equals(mc.player)) {
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
