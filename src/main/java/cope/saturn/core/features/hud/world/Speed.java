/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.hud.world;

import cope.saturn.core.features.hud.Category;
import cope.saturn.core.features.hud.HUDElement;
import net.minecraft.client.util.math.MatrixStack;

public class Speed extends HUDElement {
    public Speed() {
        super("Speed", Category.WORLD);
    }

    @Override
    public void render(MatrixStack stack) {
        double speed = getSaturn().getServerManager().getSpeed() * 71.2729367892;

        String text = Math.round(speed * 10.0) / 10.0 + "km/h";

        setWidth(mc.textRenderer.getWidth(text));
        setHeight(mc.textRenderer.fontHeight);

        mc.textRenderer.draw(stack, text, (float) x, (float) y, -1);
    }
}
