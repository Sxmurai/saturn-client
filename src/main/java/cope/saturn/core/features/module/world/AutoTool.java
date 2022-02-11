/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.AttackBlockEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", Category.WORLD, "Automatically switches to the best tool");
    }

    @EventListener
    public void onAttackBlock(AttackBlockEvent event) {
        int slot = getBestToolSlot(mc.world.getBlockState(event.getPos()));
        if (slot != -1) {
            getSaturn().getInventoryManager().swap(slot, InventoryManager.Swap.CLIENT);
        }
    }

    /**
     * Gets the best tool slot for this block state
     * @param state the block state
     * @return the hotbar slot, or -1 if none found.
     */
    public static int getBestToolSlot(BlockState state) {
        float lastSpeed = 1.0f;
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ToolItem)) {
                continue;
            }

            // get the mining speed multiplier
            float speed = stack.getMiningSpeedMultiplier(state);

            // take into account efficiency amount
            int efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
            if (efficiency > 0) {
                speed += Math.pow(efficiency, 2.0) + 1.0;
            }

            // if the speed for this item is better than the original one
            if (speed > lastSpeed) {
                lastSpeed = speed;
                slot = i;
            }
        }

        return slot;
    }
}
