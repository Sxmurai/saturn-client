/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.asm.duck.IVec3d;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.MotionUtil;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.entity.player.rotation.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Hand;

public class EntitySpeed extends Module {
    public EntitySpeed() {
        super("EntitySpeed", Category.MOVEMENT, "Makes ridden entities faster");
    }

    public static final Setting<Double> speed = new Setting<>("Speed", 10.0, 0.1, 50.0);
    public static final Setting<Boolean> forceYaw = new Setting<>("ForceYaw", true);
    public static final Setting<Boolean> remount = new Setting<>("Remount", true);

    private Entity ridden;
    private int rubberbandTicks = -1;

    @Override
    protected void onDisable() {
        super.onDisable();

        ridden = null;
        rubberbandTicks = -1;
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (!mc.player.hasVehicle()) {
            ridden = null;
            rubberbandTicks = -1;
            return;
        }

        if (rubberbandTicks != -1) {
            --rubberbandTicks;
            if (rubberbandTicks == 0) {
                rubberbandTicks = -1;
            } else {
                ridden.setVelocity(0.0, 0.0, 0.0);
                return;
            }
        }

        if (ridden == null) {
            ridden = mc.player.getVehicle();
        }

        if (forceYaw.getValue()) {
            ridden.setYaw(mc.player.getYaw());
        }

        double[] motion = MotionUtil.strafe(speed.getValue() / 10.0);

        ((IVec3d) ridden.getVelocity()).setX(motion[0]);
        ((IVec3d) ridden.getVelocity()).setZ(motion[1]);
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet && ridden != null) {
            if (packet.shouldDismount()) {
                if (remount.getValue()) {
                    mc.player.dismountVehicle();

                    RotationUtil.rotation(ridden.getEyePos()).type(RotationType.PACKET).send(true);
                    mc.interactionManager.interactEntity(mc.player, ridden, Hand.MAIN_HAND);
                }
            } else {
                ridden.setVelocity(0.0, 0.0, 0.0);
                rubberbandTicks = 10;
            }
        }
    }
}
