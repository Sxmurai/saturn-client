package cope.saturn.util.entity.player;

import cope.saturn.util.internal.Wrapper;
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
     * Gets the slot number for an item
     * @param clazz The class type for this item (eg ItemSword.class, ItemSpade.class)
     * @param includeOffhand If to consider the offhand as a slot
     * @return the slot, or -1 if none found for the type
     */
    public static int getSlot(Class<? extends Item> clazz, boolean includeOffhand) {
        if (includeOffhand && clazz.isInstance(mc.player.getOffHandStack().getItem())) {
            return OFFHAND_SLOT;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && clazz.isInstance(stack.getItem())) {
                return i;
            }
        }

        return -1;
    }
}
