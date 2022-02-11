/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.managers.interaction.PlaceType;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.world.BlockUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FeetTrap extends Module {
    public FeetTrap() {
        super("FeetTrap", Category.COMBAT, "Surrounds your feet with obsidian");
    }

    public static final Setting<Integer> blocks = new Setting<>("Blocks", 4, 1, 8);
    public static final Setting<Integer> delay = new Setting<>("Delay", 1, 0, 10);

    public static final Setting<Boolean> floor = new Setting<>("Floor", true);

    public static final Setting<PlaceType> type = new Setting<>("Type", PlaceType.CLIENT);
    public static final Setting<Boolean> rotate = new Setting<>("Rotate", true);

    public static final Setting<InventoryManager.Swap> swap = new Setting<>("Swap", InventoryManager.Swap.CLIENT);

    public static final Setting<Boolean> center = new Setting<>("Center", false);
    public static final Setting<Boolean> instant = new Setting<>("Instant", true);

    public static final Setting<Disable> disable = new Setting<>("Disable", Disable.GROUND);

    private final Queue<BlockPos> positions = new ConcurrentLinkedQueue<>();
    private final Stopwatch stopwatch = new Stopwatch();

    private Hand hand = null;
    private int oldSlot = -1;

    @Override
    protected void onDisable() {
        super.onDisable();

        positions.clear();

        hand = null;
        oldSlot = -1;
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof BlockUpdateS2CPacket packet && instant.getValue()) {
            if (getTrapPositions().contains(packet.getPos()) && packet.getState().getMaterial().isReplaceable()) {
                positions.add(packet.getPos());
            }
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (disable.getValue().equals(Disable.GROUND) && !mc.player.isOnGround()) {
            disable();
            return;
        }

        if (positions.isEmpty()) {
            positions.addAll(getTrapPositions());
            stopwatch.reset();

            if (positions.isEmpty()) {
                swapBack();

                if (disable.getValue().equals(Disable.FINISHED)) {
                    disable();
                }
            }
        } else {
            if (!InventoryUtil.isHolding(Blocks.OBSIDIAN, false)) {
                int slot = InventoryUtil.getHotBarSlot(Blocks.OBSIDIAN, true);
                if (slot == -1) {
                    disable();
                    return;
                }

                hand = slot == InventoryUtil.OFFHAND_SLOT ? Hand.OFF_HAND : Hand.MAIN_HAND;
                if (hand.equals(Hand.MAIN_HAND)) {
                    oldSlot = mc.player.getInventory().selectedSlot;
                    getSaturn().getInventoryManager().swap(slot, swap.getValue());
                }
            }

            if (center.getValue()) {
                mc.player.setPosition(Math.floor(mc.player.getX()) + 0.5, mc.player.getY(), Math.floor(mc.player.getZ()) + 0.5);
                mc.player.setVelocity(0.0, mc.player.getVelocity().y, 0.0);

                return;
            }

            if (!stopwatch.passedTicks(delay.getValue())) {
                return;
            }

            stopwatch.reset();

            for (int i = 0; i < blocks.getValue(); ++i) {
                BlockPos pos = positions.poll();
                if (pos == null) {
                    break;
                }

                getSaturn().getInteractionManager().place(pos, type.getValue(), Hand.MAIN_HAND, rotate.getValue());
            }
        }
    }

    /**
     * Swaps back to the old slot before swapping to obsidian to place
     */
    private void swapBack() {
        if (oldSlot != -1) {
            getSaturn().getInventoryManager().swap(oldSlot, swap.getValue());
            oldSlot = -1;

            hand = null;
        }
    }

    /**
     * Gets all the surrounding positions
     */
    private Set<BlockPos> getTrapPositions() {
        BlockPos pos = mc.player.getBlockPos();

        Set<BlockPos> additions = new HashSet<>();
        Set<BlockPos> needsExtensions = new HashSet<>();

        for (Direction direction : Direction.values()) {
            if (direction.equals(Direction.UP) || (!floor.getValue() && direction.equals(Direction.DOWN))) {
                continue;
            }

            BlockPos neighbor = pos.offset(direction);

            if (!BlockUtil.areBoxesEmpty(neighbor)) {
                needsExtensions.add(neighbor);
            } else {
                if (!BlockUtil.isReplaceable(neighbor)) {
                    continue;
                }

                additions.add(neighbor);
            }
        }

        if (!needsExtensions.isEmpty()) {
            for (BlockPos extension : needsExtensions) {
                for (Direction direction : Direction.values()) {
                    if (direction.equals(Direction.UP) || (!floor.getValue() && direction.equals(Direction.DOWN))) {
                        continue;
                    }

                    BlockPos neighbor = extension.offset(direction);

                    if (BlockUtil.isReplaceable(neighbor) && BlockUtil.areBoxesEmpty(neighbor)) {
                        additions.add(neighbor);
                    }
                }
            }
        }

        return additions;
    }

    public enum Disable {
        /**
         * Disables when the player onGround state is false
         */
        GROUND,

        /**
         * Disables when it is done surrounding you
         */
        FINISHED,

        /**
         * Waits for the player to disable the module
         */
        MANUAL
    }
}
