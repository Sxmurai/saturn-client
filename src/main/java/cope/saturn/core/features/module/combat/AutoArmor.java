/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.task.Task;
import cope.saturn.util.entity.player.inventory.task.TaskHandler;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

// This AutoArmor is from cosmos, But guess who wrote that AutoArmor? me. so fuck you, its my code
public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", Category.COMBAT, "Automatically equips the best armor piece");
    }

    public static final Setting<Integer> delay = new Setting<>("Delay", 1, 0, 10);
    public static final Setting<Boolean> noBinding = new Setting<>("NoBinding", true);

    // best armor slots
    private final int[] bestSlots = { -1, -1, -1, -1 };

    // handles all our inventory interactions
    private final TaskHandler handler = new TaskHandler();

    @Override
    protected void onDisable() {
        super.onDisable();

        for (int i = 0; i < 4; ++i) {
            bestSlots[i] = -1;
        }

        handler.clear();
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (handler.isEmpty()) {
            for (int i = 0; i < 36; ++i) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem)) {
                    continue;
                }

                if (noBinding.getValue() && EnchantmentHelper.hasBindingCurse(stack)) {
                    continue;
                }

                int index = armorItem.getSlotType().getEntitySlotId();

                int damageReduction = getTotalArmorValue(mc.player.getInventory().armor.get(index).getItem());
                if (damageReduction < getTotalArmorValue(armorItem)) {
                    bestSlots[index] = i;
                }
            }

            for (int i = 0; i < 4; ++i) {
                int slot = bestSlots[i];
                if (slot == -1) {
                    continue;
                }

                int correctedSlot = slot < 9 ? slot + 36 : slot;

                handler.add(new Task(SlotActionType.PICKUP, correctedSlot, false));
                handler.add(new Task(SlotActionType.PICKUP, 8 - i, false));

                // place back
                if (!mc.player.getInventory().armor.get(i).isEmpty()) {
                    handler.add(new Task(SlotActionType.PICKUP, correctedSlot, false));
                }

                bestSlots[i] = -1;
            }
        } else {
            if (mc.currentScreen == null && handler.getStopwatch().passedTicks(delay.getValue()) && !handler.isEmpty()) {
                handler.getStopwatch().reset();
                handler.run(1);
            }
        }
    }

    private int getTotalArmorValue(Item item) {
        if (!(item instanceof ArmorItem armorItem)) {
            return -1;
        }

        // this doesnt take into account for enchantments
        // ill have to fix that, but rn i dont feel like it
        return armorItem.getMaterial().getProtectionAmount(armorItem.getSlotType()) + (int) armorItem.getToughness();
    }
}
