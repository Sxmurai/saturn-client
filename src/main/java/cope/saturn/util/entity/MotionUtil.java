/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.entity.effect.StatusEffects;

public class MotionUtil implements Wrapper {
    /**
     * Checks if the local player is moving
     * @return if the local player is moving
     */
    public static boolean isMoving() {
        return mc.player.input.movementForward != 0.0f || mc.player.input.movementSideways != 0.0f;
    }

    public static double getBaseNCPSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            baseSpeed *= 1.0 + 0.2 * (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static double getJumpHeight() {
        double y = 0.3995;
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            y += (mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
        }

        return y;
    }

    /**
     * Calculates the x and z velocity motion values
     * @param speed The speed multiplier
     * @return A two element array with the x and z values
     */
    public static double[] strafe(double speed) {
        float[] movements = getMovement();

        float forward = movements[0];
        float strafe = movements[1];

        double sin = -Math.sin(Math.toRadians(movements[2]));
        double cos = Math.cos(Math.toRadians(movements[2]));

        return new double[] {
                forward * speed * sin + strafe * speed * cos,
                forward * speed * cos - strafe * speed * sin
        };
    }

    /**
     * Get the needed movement values for calculating x and z motion values
     * @return A three element array containing the forward, strafe, and yaw values
     */
    public static float[] getMovement() {
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;
        float yaw = mc.player.getYaw();

        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += forward > 0.0f ? -45.0f : 45.0f;
            } else if (strafe < 0.0f) {
                yaw += forward > 0.0f ? 45.0f : -45.0f;
            }

            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }

        return new float[] { forward, strafe, yaw, };
    }
}
