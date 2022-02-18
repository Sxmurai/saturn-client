/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.miscellaneous;

import cope.saturn.core.events.MouseEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.input.InputUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class MiddleClick extends Module {
    public MiddleClick() {
        super("MiddleClick", Category.MISCELLANEOUS, "Does things upon a middle click");
    }

    public static final Setting<Action> entity = new Setting<>("Entity", Action.FRIEND);
    public static final Setting<Action> miss = new Setting<>("Miss", Action.PEARL);

    @EventListener
    public void onMouseEvent(MouseEvent event) {
        if (event.getButton() == InputUtil.MIDDLE_CLICK && event.getAction() == GLFW.GLFW_PRESS) {
            HitResult result = mc.crosshairTarget;
            if (result != null) {
                HitResult.Type type = result.getType();
                if (type.equals(HitResult.Type.BLOCK)) {
                    return;
                }

                if (type.equals(HitResult.Type.ENTITY)) {
                    handle(entity.getValue());
                } else if (type.equals(HitResult.Type.MISS)) {
                    handle(miss.getValue());
                }
            }
        }
    }

    private void handle(Action action) {
        if (action.equals(Action.PEARL)) {
            int pearlSlot = InventoryUtil.getSlot(Items.ENDER_PEARL, false);
            if (pearlSlot == -1) {
                return;
            }

            int oldSlot = mc.player.getInventory().selectedSlot;
            getSaturn().getInventoryManager().swap(pearlSlot, InventoryManager.Swap.CLIENT);

            ActionResult result = mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            if (result.isAccepted()) {
                if (result.shouldSwingHand()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }

                getSaturn().getInventoryManager().swap(oldSlot, InventoryManager.Swap.CLIENT);
            }
        } else if (action.equals(Action.FRIEND)) {
            if (!(mc.crosshairTarget instanceof EntityHitResult hitResult)) {
                return;
            }

            if (!(hitResult.getEntity() instanceof PlayerEntity player)) {
                return;
            }

            // TODO: relationship manager
        }
    }

    public enum Action {
        FRIEND,
        PEARL,
        NONE;
    }
}
