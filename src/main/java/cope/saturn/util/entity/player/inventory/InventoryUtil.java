/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player.inventory;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil implements Wrapper {
    /**
     * Represents the offhand slot
     */
    public static final int OFFHAND_SLOT = 45;

    /**
     * Checks if we are holding an item
     * @param clazz The class type for this item (eg ItemSword.class, ItemSpade.class)
     * @param includeOffhand If to consider the offhand as a slot
     * @return if we are holding this item
     */
    public static boolean isHolding(Class<? extends Item> clazz, boolean includeOffhand) {
        if (includeOffhand && clazz.isInstance(mc.player.getOffHandStack().getItem())) {
            return true;
        }

        return clazz.isInstance(mc.player.getMainHandStack().getItem());
    }

    /**
     * Checks if we are holding an item
     * @param item The item type
     * @param includeOffhand If to consider the offhand as a slot
     * @return if we are holding this item
     */
    public static boolean isHolding(Item item, boolean includeOffhand) {
        if (includeOffhand && mc.player.getOffHandStack().getItem().equals(item)) {
            return true;
        }

        return mc.player.getMainHandStack().getItem().equals(item);
    }

    /**
     * Checks if we are holding a block
     * @param block The block
     * @param includeOffhand If to consider the offhand as a slot
     * @return if we are holding this block
     */
    public static boolean isHolding(Block block, boolean includeOffhand) {
        if (includeOffhand && mc.player.getOffHandStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock().equals(block)) {
            return true;
        }

        return mc.player.getMainHandStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock().equals(block);
    }

    /**
     * Gets the slot number for an item
     * @param clazz The class type for this item (eg ItemSword.class, ItemSpade.class)
     * @param includeOffhand If to consider the offhand as a slot
     * @return the slot, or -1 if none found for the type
     */
    public static int getSlot(Class<? extends Item> clazz, boolean includeOffhand) {
        if (includeOffhand && clazz.isInstance(mc.player.getOffHandStack().getItem())) {
            return OFFHAND_SLOT;
        }

        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && clazz.isInstance(stack.getItem())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the slot number for an item
     * @param item The item
     * @param includeOffhand If to consider the offhand as a slot
     * @return the slot, or -1 if none found for the type
     */
    public static int getSlot(Item item, boolean includeOffhand) {
        if (includeOffhand && mc.player.getOffHandStack().getItem().equals(item)) {
            return OFFHAND_SLOT;
        }

        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem().equals(item)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets a slot in the hotbar
     * @param block The block
     * @param includeOffhand If to check the offhand
     * @return the slot, or -1 if none found for the type
     */
    public static int getHotBarSlot(Block block, boolean includeOffhand) {
        if (includeOffhand && mc.player.getOffHandStack().getItem() instanceof BlockItem blockItem &&
                blockItem.getBlock().equals(block)) {
            return OFFHAND_SLOT;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().equals(block)) {
                return i;
            }
        }

        return -1;
    }
}
