/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.input;

import cope.saturn.util.internal.Wrapper;
import org.lwjgl.glfw.GLFW;

public class MouseUtil implements Wrapper {
    public static final int LEFT_CLICK = GLFW.GLFW_MOUSE_BUTTON_1;
    public static final int RIGHT_CLICK = GLFW.GLFW_MOUSE_BUTTON_2;
    public static final int MIDDLE_CLICK = GLFW.GLFW_MOUSE_BUTTON_3;

    /**
     * Check if a mouse button is pressed down
     * @param button The button id
     * @return if the returned value from glfwGetMouseButton is 1 (GLFW.GLFW_PRESS)
     */
    public static boolean isButtonDown(int button) {
        return GLFW.glfwGetMouseButton(getHandle(), button) == GLFW.GLFW_PRESS;
    }

    /**
     * Gets the window handle
     * @return the window handle
     */
    public static long getHandle() {
        return mc.getWindow().getHandle();
    }
}
