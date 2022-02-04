/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.MOVEMENT, "Makes you automatically sprint", GLFW.GLFW_KEY_N);
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.STRICT);

    @Override
    protected void onDisable() {
        if (!nullCheck() && mc.player.isSprinting()) {
            mc.player.setSprinting(false);
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        switch (mode.getValue()) {
            case STRICT -> {
                if (mc.player.isSneaking() || mc.player.isUsingItem() || mc.player.horizontalCollision || !mc.options.keyForward.isPressed()) {
                    return;
                }
            }

            case RAGE -> {
                if (mc.player.input.movementForward < 0.0f) {
                    return;
                }
            }
        }

        if (!mc.player.isSprinting()) {
            mc.player.setSprinting(true);
        }
    }

    public enum Mode {
        /**
         * Makes sprinting as vanilla as possible
         */
        STRICT,

        /**
         * Sprints forward with no extra checks
         */
        RAGE,

        /**
         * Sprints when moving, all directions
         */
        OMNI
    }
}
