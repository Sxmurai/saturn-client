/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.client;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.ui.click.ClickGUIScreen;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {
    public static ClickGUI INSTANCE;

    public ClickGUI() {
        super("ClickGUI", Category.CLIENT, "Opens a ClickGUI screen for the client", GLFW.GLFW_KEY_RIGHT_ALT);
        INSTANCE = this;
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }

        mc.setScreen(ClickGUIScreen.getInstance());
    }

    @Override
    protected void onDisable() {
        if (!nullCheck()) {
            // set the screen to nothing
            mc.setScreen(null);
        }
    }

    public static ClickGUI getInstance() {
        return INSTANCE;
    }
}
