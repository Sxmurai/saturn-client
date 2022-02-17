/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.asm.duck.IVec3d;
import cope.saturn.core.events.MotionEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.MotionUtil;
import cope.saturn.util.entity.player.rotation.Rotation;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ElytraItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ElytraFlight extends Module {
    public ElytraFlight() {
        super("ElytraFlight", Category.MOVEMENT, "Makes flying with elytras a little better");
    }

    public static final Setting<Double> speed = new Setting<>("Speed", 16.8, 1.0, 200.0);

    public static final Setting<Object> vertical = new Setting<>("Vertical", null);
    public static final Setting<Double> verticalSpeed = new Setting<>(vertical, "VerticalSpeed", 1.0, 0.1, 5.0);
    public static final Setting<Boolean> suspend = new Setting<>(vertical, "Suspend", true);
    public static final Setting<Boolean> rotate = new Setting<>(vertical, "Rotate", true);

    public static final Setting<Boolean> down = new Setting<>("Down", false);
    public static final Setting<Boolean> takeOff = new Setting<>("TakeOff", true);

    private int progress = 0;

    @Override
    protected void onDisable() {
        super.onDisable();

        progress = 0;
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (!(mc.player.getInventory().getArmorStack(2).getItem() instanceof ElytraItem)) {
            return;
        }

        if (!mc.player.isOnGround() && !mc.player.isFallFlying() && takeOff.getValue()) {
            NetworkUtil.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }

        if (!mc.player.isFallFlying()) {
            return;
        }

        if (MotionUtil.isMoving()) {
            double[] motion = MotionUtil.strafe(speed.getValue() / 10.0);

            ((IVec3d) event.getVec()).setX(motion[0]);
            ((IVec3d) event.getVec()).setZ(motion[1]);
        }

        if (rotate.getValue() && progress == 0) {
            // we only want to keep our 0 pitch rotation if we were to not be looking up or down
            // oh and we dont care if we're suspended too, cause we'll just say mid air while looking down anyway
            if (Math.abs(mc.player.getPitch()) < 45.0f && !suspend.getValue()) {
                new Rotation(RotationType.PACKET, mc.player.getYaw(), 0.0f).send(false);
            }
        }

        double vSpeed = verticalSpeed.getValue() / 10.0;

        if (mc.options.keyJump.isPressed()) {
            if (rotate.getValue()) {
                if (progress <= 45) {
                    ++progress;
                    new Rotation(RotationType.PACKET, mc.player.getYaw(), -progress).send(false);
                } else {
                    new Rotation(RotationType.PACKET, mc.player.getYaw(), -45.0f).send(false);
                    ((IVec3d) event.getVec()).setY(vSpeed);
                }
            }
        } else if (mc.options.keySneak.isPressed()) {
            if (rotate.getValue()) {
                if (progress <= 45) {
                    ++progress;
                    new Rotation(RotationType.PACKET, mc.player.getYaw(), progress).send(false);
                } else {
                    new Rotation(RotationType.PACKET, mc.player.getYaw(), 45.0f).send(false);
                    ((IVec3d) event.getVec()).setY(-vSpeed);
                }
            }
        } else {
            --progress;
            if (progress >= 0) {
                progress = 0;
            }

            // this helps on ec.me
            if (down.getValue()) {
                new Rotation(RotationType.PACKET, mc.player.getYaw(), 90.0f).send(false);
            }

            if (suspend.getValue()) {
                ((IVec3d) event.getVec()).setY(-1.01E-4);
            }
        }
    }
}
