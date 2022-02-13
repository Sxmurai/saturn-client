/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.hud.player;

import cope.saturn.core.features.hud.Category;
import cope.saturn.core.features.hud.HUDElement;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class Armor extends HUDElement {
    public Armor() {
        super("Armor", Category.PLAYER);
    }

    @Override
    public void render(MatrixStack stack) {
        double x = getX();

        for (int i = 3; i >= 0; --i) {
            ItemStack itemStack = mc.player.getInventory().armor.get(i);

            mc.getItemRenderer().renderInGui(itemStack, (int) x, (int) getY());
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, (int) x, (int) getY());

            x += 16.0;
        }

        setWidth(x - getX());
        setHeight(16.0);
    }
}
