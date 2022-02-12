/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.client;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.ui.hud.HUDEditorScreen;
import org.lwjgl.glfw.GLFW;

public class HUDEditor extends Module {
    public static HUDEditor INSTANCE;

    public HUDEditor() {
        super("HUDEditor", Category.CLIENT, "Shows the HUD editor GUI", GLFW.GLFW_KEY_GRAVE_ACCENT);
        INSTANCE = this;
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }

        mc.setScreen(HUDEditorScreen.getInstance());
    }

    @Override
    protected void onDisable() {
        if (!nullCheck()) {
            // set the screen to nothing
            mc.setScreen(null);
        }
    }
}
