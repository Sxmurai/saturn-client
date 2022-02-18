/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.hud.client;

import cope.saturn.core.features.hud.Category;
import cope.saturn.core.features.hud.HUDElement;
import net.minecraft.client.util.math.MatrixStack;

public class Ping extends HUDElement {
    public Ping() {
        super("Ping", Category.CLIENT);
    }

    @Override
    public void render(MatrixStack stack) {
        String text = "Ping: " + getSaturn().getServerManager().getLatency(mc.player.getUuid()) + "ms";
        mc.textRenderer.draw(stack, text, (float) x, (float) y, -1);

        setWidth(mc.textRenderer.getWidth(text));
        setHeight(mc.textRenderer.fontHeight);
    }
}
