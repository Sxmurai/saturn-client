/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.managers.interaction.PlaceType;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.network.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class SelfFill extends Module {
    /**
     * Vanilla packet offsets for a fake jump
     */
    private static final double[] FAKE_JUMP = { 0.41999998688698, 0.7531999805211997, 1.00133597911214, 1.16610926093821 };

    public SelfFill() {
        super("SelfFill", Category.COMBAT, "Flags NCP to put you inside a block");
    }

    public static final Setting<Type> type = new Setting<>("Block", Type.OBSIDIAN);
    public static final Setting<InventoryManager.Swap> swap = new Setting<>("Swap", InventoryManager.Swap.CLIENT);
    public static final Setting<Boolean> rotate = new Setting<>("Rotate", true);
    public static final Setting<Boolean> flag = new Setting<>("Flag", true);

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }

        BlockPos origin = new BlockPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        // swap to item
        int slot = InventoryUtil.getHotBarSlot(type.getValue().block, true);
        if (slot == -1) {
            disable();
            return;
        }

        Hand hand = slot == InventoryUtil.OFFHAND_SLOT ? Hand.OFF_HAND : Hand.MAIN_HAND;

        int oldSlot = -1;
        if (hand.equals(Hand.MAIN_HAND)) {
            oldSlot = mc.player.getInventory().selectedSlot;
            getSaturn().getInventoryManager().swap(slot, swap.getValue());
        }

        // send our fake jump offsets
        for (double offset : FAKE_JUMP) {
            NetworkUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.getX(), origin.getY() + offset, origin.getZ(), true));
        }

        // place block. this needs to be on packet place or else it will not place
        getSaturn().getInteractionManager().place(origin, PlaceType.PACKET, hand, rotate.getValue());

        // sometimes i like to burrow bait, so this is for just that
        if (flag.getValue()) {
            // flag NCP (this is the whole reason why burrow works)
            NetworkUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.getX(), origin.getY() + 3.0, origin.getZ(), true));
        }

        if (oldSlot != -1) {
            getSaturn().getInventoryManager().swap(oldSlot, swap.getValue());
        }

        disable();
    }

    public enum Type {
        OBSIDIAN(Blocks.OBSIDIAN),
        ECHEST(Blocks.ENDER_CHEST),
        ENDROD(Blocks.END_ROD);

        private final Block block;

        Type(Block block) {
            this.block = block;
        }
    }
}
