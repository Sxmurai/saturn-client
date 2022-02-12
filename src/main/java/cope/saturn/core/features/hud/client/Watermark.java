/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.hud.client;

import cope.saturn.core.Saturn;
import cope.saturn.core.features.hud.Category;
import cope.saturn.core.features.hud.HUDElement;
import net.minecraft.client.util.math.MatrixStack;

public class Watermark extends HUDElement {
    public Watermark() {
        super("Watermark", Category.CLIENT);

        // set default x, y coordinates
        setX(2.0);
        setY(2.0);
    }

    @Override
    public void render(MatrixStack stack) {
        String text = Saturn.NAME + " v" + Saturn.VERSION;

        setWidth(mc.textRenderer.getWidth(text));
        setHeight(mc.textRenderer.fontHeight);

        mc.textRenderer.draw(stack, text, (float) x, (float) y, -1);
    }
}
