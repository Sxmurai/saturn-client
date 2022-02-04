/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {
    private double oldGamma = -1.0;

    public Fullbright() {
        super("Fullbright", Category.VISUALS, "Makes the game brigher", GLFW.GLFW_KEY_M);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (oldGamma == -1.0) {
            oldGamma = mc.options.gamma;
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (oldGamma != -1.0) {
            mc.options.gamma = oldGamma;
            oldGamma = -1.0;
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        mc.options.gamma = 1000.0;
    }
}
