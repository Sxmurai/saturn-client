/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity;

import cope.saturn.util.internal.Wrapper;

public class MotionUtil implements Wrapper {
    /**
     * Checks if the local player is moving
     * @return if the local player is moving
     */
    public static boolean isMoving() {
        return mc.player.input.movementForward != 0.0f || mc.player.input.movementSideways != 0.0f;
    }
}
