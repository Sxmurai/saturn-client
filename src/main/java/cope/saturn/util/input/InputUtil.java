/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.input;

import cope.saturn.util.internal.Wrapper;
import org.lwjgl.glfw.GLFW;

public class InputUtil implements Wrapper {
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
     * Gets a key name based off of a keycode
     * @param keyCode the keycode
     * @return the string representation of this key, or null
     */
    public static String getKeyName(int keyCode) {
        // if we do the thing in the default switch case and its not like a number/letter, it'll return null
        // so i have to do one of these

        // also, alot of these cases are copy-pasted from meteor cause i didnt feel like writing these out
        // https://github.com/MeteorDevelopment/meteor-client/blob/6fe78a10de08d89ca34b162f6be9a5c2b2f7867c/src/main/java/meteordevelopment/meteorclient/utils/Utils.java#L286-L344
        return switch (keyCode) {
            case GLFW.GLFW_KEY_UNKNOWN -> "None";
            case GLFW.GLFW_KEY_ESCAPE -> "Esc";
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> "Grave Accent";
            case GLFW.GLFW_KEY_WORLD_1 -> "World 1";
            case GLFW.GLFW_KEY_WORLD_2 -> "World 2";
            case GLFW.GLFW_KEY_PRINT_SCREEN -> "Print Screen";
            case GLFW.GLFW_KEY_PAUSE -> "Pause";
            case GLFW.GLFW_KEY_INSERT -> "Insert";
            case GLFW.GLFW_KEY_DELETE -> "Delete";
            case GLFW.GLFW_KEY_HOME -> "Home";
            case GLFW.GLFW_KEY_PAGE_UP -> "Page Up";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "Page Down";
            case GLFW.GLFW_KEY_END -> "End";
            case GLFW.GLFW_KEY_TAB -> "Tab";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Control";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Control";
            case GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW.GLFW_KEY_UP -> "Arrow Up";
            case GLFW.GLFW_KEY_DOWN -> "Arrow Down";
            case GLFW.GLFW_KEY_LEFT -> "Arrow Left";
            case GLFW.GLFW_KEY_RIGHT -> "Arrow Right";
            case GLFW.GLFW_KEY_APOSTROPHE -> "Apostrophe";
            case GLFW.GLFW_KEY_BACKSPACE -> "Backspace";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "Caps Lock";
            case GLFW.GLFW_KEY_MENU -> "Menu";
            case GLFW.GLFW_KEY_LEFT_SUPER -> "Left Super";
            case GLFW.GLFW_KEY_RIGHT_SUPER -> "Right Super";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_KP_ENTER -> "Numpad Enter";
            case GLFW.GLFW_KEY_NUM_LOCK -> "Num Lock";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_F13 -> "F13";
            case GLFW.GLFW_KEY_F14 -> "F14";
            case GLFW.GLFW_KEY_F15 -> "F15";
            case GLFW.GLFW_KEY_F16 -> "F16";
            case GLFW.GLFW_KEY_F17 -> "F17";
            case GLFW.GLFW_KEY_F18 -> "F18";
            case GLFW.GLFW_KEY_F19 -> "F19";
            case GLFW.GLFW_KEY_F20 -> "F20";
            case GLFW.GLFW_KEY_F21 -> "F21";
            case GLFW.GLFW_KEY_F22 -> "F22";
            case GLFW.GLFW_KEY_F23 -> "F23";
            case GLFW.GLFW_KEY_F24 -> "F24";
            case GLFW.GLFW_KEY_F25 -> "F25";

            default -> GLFW.glfwGetKeyName(keyCode, GLFW.glfwGetKeyScancode(keyCode));
        };
    }

    /**
     * Gets the window handle
     * @return the window handle
     */
    public static long getHandle() {
        return mc.getWindow().getHandle();
    }
}
