/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import com.mojang.authlib.GameProfile;
import cope.saturn.asm.duck.IVec3d;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.MotionUtil;
import cope.saturn.util.entity.player.FakeplayerUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class Freecam extends Module {
    public Freecam() {
        super("Freecam", Category.WORLD, "Allows you to move freely around");
    }

    public static final Setting<Double> speed = new Setting<>("Speed", 1.5, 0.1, 10.0);

    private OtherClientPlayerEntity cameraEntity;
    private BlockPos playerPos;

    @Override
    protected void onEnable() {
        super.onEnable();

        if (nullCheck()) {
            disable();
            return;
        }

        cameraEntity = FakeplayerUtil.spawn(-133742069, new GameProfile(UUID.randomUUID(), "You"));
        playerPos = mc.player.getBlockPos();
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!nullCheck()) {
            FakeplayerUtil.despawn(-133742069);
            cameraEntity = null;

            mc.player.noClip = false;

            mc.player.setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            playerPos = null;
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        mc.player.setVelocity(0.0, 0.0, 0.0);
        mc.player.noClip = true;

        double motionSpeed = speed.getValue() / 10.0;

        if (MotionUtil.isMoving()) {
            double[] motion = MotionUtil.strafe(motionSpeed);

            ((IVec3d) mc.player.getVelocity()).setX(motion[0]);
            ((IVec3d) mc.player.getVelocity()).setZ(motion[1]);
        }

        if (mc.options.keyJump.isPressed()) {
            ((IVec3d) mc.player.getVelocity()).setY(motionSpeed);
        } else if (mc.options.keySneak.isPressed()) {
            ((IVec3d) mc.player.getVelocity()).setY(-motionSpeed);
        }
    }
}
