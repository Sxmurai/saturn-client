/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.managers.interaction.PlaceType;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.EntityUtil;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.internal.ChatUtil;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.world.BlockUtil;
import cope.saturn.util.world.combat.DamagesUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoBed extends Module {
    public AutoBed() {
        super("AutoBed", Category.COMBAT, "Automatically places and sleeps in beds");
    }

    public static final Setting<Boolean> place = new Setting<>("Place", true);
    public static final Setting<Float> placeSpeed = new Setting<>(place, "PlaceSpeed", 20.0f, 0.1f, 20.0f);
    public static final Setting<Double> placeRange = new Setting<>(place, "PlaceRange", 4.5, 1.0, 6.0);
    public static final Setting<Double> placeWalls = new Setting<>(place, "PlaceWalls", 3.0, 1.0, 6.0);
    public static final Setting<Boolean> raycast = new Setting<>(place, "Raycast", false);
    public static final Setting<Boolean> strictDirection = new Setting<>(place, "StrictDirection", false);

    public static final Setting<Boolean> sleep = new Setting<>("Sleep", true);
    public static final Setting<Float> sleepSpeed = new Setting<>(sleep, "SleepSpeed", 20.0f, 0.1, 20.0f);
    public static final Setting<Double> sleepRange = new Setting<>(sleep, "SleepRange", 4.5, 1.0, 6.0);
    public static final Setting<Double> sleepWalls = new Setting<>(sleep, "SleepWalls", 3.0, 1.0, 6.0);

    public static final Setting<Object> damage = new Setting<>("Damage", null);
    public static final Setting<Float> minDamage = new Setting<>(damage, "MinDamage", 4.0f, 1.0f, 20.0f);
    public static final Setting<Float> maxLocal = new Setting<>(damage, "MaxLocal", 10.0f, 1.0f, 20.0f);
    public static final Setting<Float> localBias = new Setting<>(damage, "LocalBias", 0.5f, 0.0f, 3.0f);

    public static final Setting<RotationType> rotate = new Setting<>("Rotate", RotationType.PACKET);

    public static final Setting<AutoCrystal.Targeting> targeting = new Setting<>("Targeting", AutoCrystal.Targeting.DAMAGE);
    public static final Setting<Double> targetRange = new Setting<>(targeting, "TargetRange", 10.0, 1.0, 20.0);
    public static final Setting<Boolean> naked = new Setting<>(targeting, "Naked", false);

    public static final Setting<InventoryManager.Swap> swap = new Setting<>("Swap", InventoryManager.Swap.CLIENT);

    private PlayerEntity target;
    private BlockPos position = null;

    private final Stopwatch placeTimer = new Stopwatch();
    private final Stopwatch sleepTimer = new Stopwatch();

    private int oldSlot = -1;
    private Hand hand;

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (mc.world.getDimension().isBedWorking()) {
            ChatUtil.send("You are able to sleep in beds in this dimension, toggling...");
            disable();
            return;
        }

        findTarget();
        if (target == null) {
            return;
        }

        if (place.getValue()) {
            findPlacePosition();

            if (!InventoryUtil.isHolding(BedItem.class, true) && !swap.getValue().equals(InventoryManager.Swap.NONE)) {
                int slot = InventoryUtil.getSlot(BedItem.class, true);
                if (slot == -1) {
                    return;
                }

                hand = slot == InventoryUtil.OFFHAND_SLOT ? Hand.OFF_HAND : Hand.MAIN_HAND;
                if (hand.equals(Hand.MAIN_HAND)) {
                    oldSlot = mc.player.getInventory().selectedSlot;
                    getSaturn().getInventoryManager().swap(slot, swap.getValue());
                }
            }

            if (position != null && placeTimer.getPassedMs() / 50.0f >= 20.0f - placeSpeed.getValue()) {
                placeTimer.reset();

                getSaturn().getInteractionManager().place(position, PlaceType.PACKET, hand, !rotate.getValue().equals(RotationType.NONE));

                position = null;
            }
        }
    }

    private void findPlacePosition() {
        if (target == null && targeting.getValue().equals(AutoCrystal.Targeting.DISTANCE)) {
            return;
        }

        BlockPos placePos = null;
        float damage = 0.5f;

        for (BlockPos pos : BlockUtil.sphere(mc.player.getBlockPos(), placeRange.getValue().intValue())) {
            if (BlockUtil.areBoxesEmpty(pos)) {
                continue;
            }

            float localDamage = DamagesUtil.bedDamage(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), target) + localBias.getValue();
            if (localDamage > EntityUtil.getHealth(mc.player) || localDamage > maxLocal.getValue()) {
                continue;
            }

            float targetDamage = 0.5f;
            if (target != null) {
                targetDamage = DamagesUtil.bedDamage(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), target);
                if (localDamage > targetDamage || targetDamage < minDamage.getValue()) {
                    continue;
                }
            } else {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    float playerDamage = DamagesUtil.bedDamage(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), player);
                    if (playerDamage > targetDamage) {
                        targetDamage = playerDamage;
                        target = player;
                    }
                }
            }

            if (targetDamage > damage) {
                placePos = pos;
                damage = targetDamage;
            }
        }

        position = placePos;
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
                if (targeting.getValue().equals(AutoCrystal.Targeting.DISTANCE)) {
                    if (distance < mc.player.squaredDistanceTo(possibleTarget)) {
                        possibleTarget = player;
                    }
                }
            }
        }

        target = possibleTarget;
    }
}
