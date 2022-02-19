/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.managers.interaction.PlaceType;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.world.BlockUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AutoBedCrafter extends Module {
    public AutoBedCrafter() {
        super("AutoBedCrafter", Category.WORLD, "Automatically crafts beds");
    }

    public static final Setting<Double> speed = new Setting<>("Speed", 15.0, 0.1, 20.0);
    public static final Setting<Integer> threshold = new Setting<>("Threshold", 5, 0, 32);
    public static final Setting<Boolean> craftingPlace = new Setting<>("CraftingPlace", true);
    public static final Setting<RotationType> rotate = new Setting<>("Rotate", RotationType.PACKET);
    public static final Setting<Boolean> shiftClick = new Setting<>("ShiftClick", true);

    private final Stopwatch stopwatch = new Stopwatch();

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (!(mc.currentScreen instanceof CraftingScreen)) {
            BlockPos craftingTablePos = findCraftingTable();
            if (craftingTablePos == null) {
                if (!craftingPlace.getValue()) {
                    return;
                }

                int oldSlot = -1;
                Hand hand = Hand.MAIN_HAND;

                if (!InventoryUtil.isHolding(Blocks.CRAFTING_TABLE, true)) {
                    int slot = InventoryUtil.getHotBarSlot(Blocks.CRAFTING_TABLE, true);
                    if (slot == -1) {
                        return;
                    }

                    hand = slot == InventoryUtil.OFFHAND_SLOT ? Hand.OFF_HAND : Hand.MAIN_HAND;
                    if (hand.equals(Hand.MAIN_HAND)) {
                        oldSlot = mc.player.getInventory().selectedSlot;
                        getSaturn().getInventoryManager().swap(slot, InventoryManager.Swap.CLIENT);
                    }
                }

                for (BlockPos pos : BlockUtil.sphere(mc.player.getBlockPos(), 2)) {
                    if (BlockUtil.areBoxesEmpty(pos) && BlockUtil.getDirection(pos) != null) {
                        getSaturn().getInteractionManager().place(pos, PlaceType.PACKET, hand, !rotate.getValue().equals(RotationType.NONE));
                        if (oldSlot != -1) {
                            getSaturn().getInventoryManager().swap(oldSlot, InventoryManager.Swap.CLIENT);
                        }

                        return;
                    }
                }

                return;
            }

            getSaturn().getInteractionManager().rightClick(craftingTablePos, true, !rotate.getValue().equals(RotationType.NONE));

            return;
        }

        // TODO wtf
    }

    private BlockPos findCraftingTable() {
        BlockPos nearest = null;

        for (BlockPos pos : BlockUtil.sphere(mc.player.getBlockPos(), 4)) {
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.CRAFTING_TABLE)) {
                if (nearest == null) {
                    nearest = pos;
                } else {
                    if (mc.player.squaredDistanceTo(nearest.getX(), nearest.getY(), nearest.getZ()) >
                            mc.player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) {
                        nearest = pos;
                    }
                }
            }
        }

        return nearest;
    }
}
