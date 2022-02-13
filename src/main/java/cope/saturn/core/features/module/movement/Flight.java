/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.asm.duck.IVec3d;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.MotionUtil;
import me.bush.eventbus.annotation.EventListener;

public class Flight extends Module {
    public Flight() {
        super("Flight", Category.MOVEMENT, "Allows you to fly");
    }

    public static final Setting<Double> speed = new Setting<>("Speed", 2.5, 0.1, 10.0);
    public static final Setting<Boolean> antiKick = new Setting<>("AntiKick", true);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (!MotionUtil.isMoving()) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
        }

        if (mc.options.keyJump.isPressed()) {
            ((IVec3d) mc.player.getVelocity()).setY(speed.getValue() / 10.0);
        } else if (mc.options.keySneak.isPressed()) {
            ((IVec3d) mc.player.getVelocity()).setY(-(speed.getValue() / 10.0));
        } else {
            if (antiKick.getValue()) {
                ((IVec3d) mc.player.getVelocity()).setY(mc.player.age % 2 == 0 ? -0.08 : -0.04);
            }
        }

        double[] motion = MotionUtil.strafe(speed.getValue() / 10.0);

        ((IVec3d) mc.player.getVelocity()).setX(motion[0]);
        ((IVec3d) mc.player.getVelocity()).setZ(motion[1]);
    }
}
