/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.managers.interaction.PlaceType;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.MotionUtil;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.network.NetworkUtil;
import cope.saturn.util.world.BlockUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", Category.WORLD, "Places blocks under you");
    }

    public static final Setting<PlaceType> type = new Setting<>("Type", PlaceType.CLIENT);
    public static final Setting<Boolean> rotate = new Setting<>("Rotate", true);

    public static final Setting<Boolean> tower = new Setting<>("Tower", true);
    public static final Setting<Double> multiplier = new Setting<>(tower, "Multiplier", 0.3, 0.0, 1.0);
    public static final Setting<Boolean> jump = new Setting<>(tower, "Jump", true);

    public static final Setting<Boolean> stopSprint = new Setting<>("StopSprint", true);
    public static final Setting<Boolean> slow = new Setting<>("Slow", false);

    public static final Setting<Switch> swap = new Setting<>("Swap", Switch.KEEP);

    private final Stopwatch towerStopwatch = new Stopwatch();
    private int oldSlot = -1;

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!nullCheck()) {
            resetSlot();
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket packet) {
            if (packet.getMode().equals(ClientCommandC2SPacket.Mode.START_SPRINTING) && stopSprint.getValue()) {
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        BlockPos below = new BlockPos(
                mc.player.getX() + mc.player.getVelocity().getX(),
                mc.player.getY() - 1.0, mc.player.getZ() + mc.player.getVelocity().getZ());

        if (BlockUtil.isReplaceable(below)) {
            Direction direction = BlockUtil.getDirection(below);
            if (direction == null) {
                return;
            }

            int slot = InventoryUtil.getSlot(BlockItem.class, false);
            if (slot == -1) {
                resetSlot();
                return;
            }

            Hand hand = slot == InventoryUtil.OFFHAND_SLOT ? Hand.OFF_HAND : Hand.MAIN_HAND;
            mc.player.setCurrentHand(hand);

            if (hand.equals(Hand.MAIN_HAND)) {
                oldSlot = mc.player.getInventory().selectedSlot;
                getSaturn().getInventoryManager().swap(slot, swap.getValue().swap);
            }

            if (slow.getValue() && MotionUtil.isMoving()) {
                Vec3d velocity = mc.player.getVelocity();
                mc.player.setVelocity(velocity.getX() * 0.67, velocity.getY(), velocity.getZ() * 0.67);
            }

            if (stopSprint.getValue() && mc.player.isSprinting()) {
                mc.player.setSprinting(false);
                NetworkUtil.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }

            getSaturn().getInteractionManager().place(below, type.getValue(), hand, rotate.getValue());

            if (tower.getValue() && mc.options.keyJump.isPressed() && direction.equals(Direction.DOWN)) {
                Vec3d velocity = mc.player.getVelocity();

                if (multiplier.getValue() != 0.0) {
                    mc.player.setVelocity(velocity.getX() * multiplier.getValue(), velocity.getY(), velocity.getZ() * multiplier.getValue());
                }

                if (jump.getValue()) {
                    mc.player.jump();
                }

                if (towerStopwatch.passedMs(1200L)) {
                    towerStopwatch.reset();
                    mc.player.setVelocity(0.0, -0.28, 0.0);
                }
            }

            if (hand.equals(Hand.MAIN_HAND) && !swap.getValue().equals(Switch.KEEP)) {
                resetSlot();
            }
        }
    }

    private void resetSlot() {
        if (oldSlot != -1) {
            getSaturn().getInventoryManager().swap(oldSlot, swap.getValue().swap);
            oldSlot = -1;
        }
    }

    public enum Switch {
        /**
         * Swaps client side
         */
        CLIENT(InventoryManager.Swap.CLIENT),

        /**
         * Swaps server side
         */
        PACKET(InventoryManager.Swap.PACKET),

        /**
         * Swaps client side and forces your hotbar slot on that block
         */
        KEEP(InventoryManager.Swap.CLIENT);

        private final InventoryManager.Swap swap;

        Switch(InventoryManager.Swap swap) {
            this.swap = swap;
        }
    }
}
