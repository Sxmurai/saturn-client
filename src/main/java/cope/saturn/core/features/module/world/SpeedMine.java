/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.AttackBlockEvent;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;

public class SpeedMine extends Module {
    public SpeedMine() {
        super("SpeedMine", Category.WORLD, "Mines things faster");
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.PACKET);
    public static final Setting<Double> range = new Setting<>("Range", 5.0, 1.0, 6.0);

    public static final Setting<InventoryManager.Swap> swap = new Setting<>("Swap", InventoryManager.Swap.CLIENT);

    private BlockPos position;
    private int oldSlot = -1;

    @Override
    protected void onDisable() {
        super.onDisable();

        position = null;

        if (!nullCheck()) {
            swapBack();
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (position != null &&
                (mc.world.isAir(position)
                        || mc.player.squaredDistanceTo(position.getX(), position.getY(), position.getZ()) > range.getValue() * range.getValue())) {
            position = null;

            swapBack();
        }
    }

    @EventListener
    public void onAttackBlock(AttackBlockEvent event) {
        BlockState state = mc.world.getBlockState(event.getPos());

        // if this is an unbreakable block by hand, dont bother TODO fix
        if (state.getBlock().getHardness() == -1) {
            return;
        }

        position = event.getPos();

        if (!swap.getValue().equals(InventoryManager.Swap.NONE)) {
            int bestSlot = AutoTool.getBestToolSlot(state);
            if (bestSlot != -1) {
                oldSlot = mc.player.getInventory().selectedSlot;
                getSaturn().getInventoryManager().swap(bestSlot, swap.getValue());
            }
        }

        switch (mode.getValue()) {
            case PACKET -> {
                NetworkUtil.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, event.getPos(), event.getDirection()));
                NetworkUtil.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getDirection()));
            }

            case INSTANT -> {
                NetworkUtil.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, event.getPos(), event.getDirection()));
                NetworkUtil.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getDirection()));

                mc.world.removeBlock(event.getPos(), false);
            }
        }
    }

    private void swapBack() {
        if (oldSlot != -1) {
            getSaturn().getInventoryManager().swap(oldSlot, swap.getValue());
            oldSlot = -1;
        }
    }

    public enum Mode {
        /**
         * Uses packets to mine
         */
        PACKET,

        /**
         * Sets the block to air
         */
        INSTANT;
    }
}
