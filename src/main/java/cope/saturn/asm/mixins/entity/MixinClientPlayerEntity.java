/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.entity;

import com.mojang.authlib.GameProfile;
import cope.saturn.core.Saturn;
import cope.saturn.core.events.ItemSlowdownEvent;
import cope.saturn.core.events.SendMovementPacketsEvent;
import cope.saturn.util.network.NetworkUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.*;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    private double lastX;

    @Shadow
    private double lastBaseY;

    @Shadow
    private double lastZ;

    @Shadow
    private float lastYaw;

    @Shadow
    private float lastPitch;

    @Shadow
    private boolean lastSprinting;

    @Shadow
    private boolean lastSneaking;

    @Shadow
    private boolean lastOnGround;

    @Shadow
    private int ticksSinceLastPositionPacketSent;

    @Shadow
    private boolean autoJumpEnabled;

    @Shadow
    public Input input;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract boolean isCamera();

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    public void sendMovementPacketsPre(CallbackInfo info) {
        SendMovementPacketsEvent.Pre event = new SendMovementPacketsEvent.Pre(getX(), getY(), getZ(), getYaw(), getPitch(), isOnGround());
        Saturn.EVENT_BUS.post(event);

        if (event.isOnGround()) {
            info.cancel();
            sendModifiedMovementPackets(event.getX(), event.getY(), event.getZ(),
                    event.getYaw(), event.getPitch(), event.isOnGround());
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    public void sendMovementPacketsPost(CallbackInfo info) {
        Saturn.EVENT_BUS.post(new SendMovementPacketsEvent());
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;ticksLeftToDoubleTapSprint:I",
                    shift = At.Shift.AFTER))
    public void afterSprintReset(CallbackInfo info) {
        Saturn.EVENT_BUS.post(new ItemSlowdownEvent(input));
    }

    private void sendModifiedMovementPackets(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        if (isSprinting() != lastSprinting) {
            NetworkUtil.sendPacket(new ClientCommandC2SPacket(this, isSprinting() ? START_SPRINTING : STOP_SPRINTING));
            lastSprinting = isSprinting();
        }

        if (isSneaking() != lastSneaking) {
            NetworkUtil.sendPacket(new ClientCommandC2SPacket(this, isSprinting() ? PRESS_SHIFT_KEY : RELEASE_SHIFT_KEY));
            lastSneaking = isSneaking();
        }

        if (isCamera()) {
            ++ticksSinceLastPositionPacketSent;

            double xDiff = x - lastX;
            double yDiff = y - lastBaseY;
            double zDiff = z - lastZ;

            boolean moved = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > 9.0E-4 || ticksSinceLastPositionPacketSent >= 20;

            float yawDiff = yaw - lastYaw;
            float pitchDiff = pitch - lastPitch;

            boolean rotated = yawDiff != 0.0f || pitchDiff != 0.0f;

            if (hasVehicle()) {
                Vec3d velocity = getVelocity();
                NetworkUtil.sendPacket(new PlayerMoveC2SPacket.Full(velocity.x, -999.0, velocity.z, yaw, pitch, onGround));
            } else if (moved && rotated) {
                NetworkUtil.sendPacket(new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround));
            } else if (moved) {
                NetworkUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
            } else if (rotated) {
                NetworkUtil.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround));
            } else if (lastOnGround != onGround) {
                NetworkUtil.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(onGround));
            }

            if (moved) {
                lastX = x;
                lastBaseY = y;
                lastZ = z;
                ticksSinceLastPositionPacketSent = 0;
            }

            if (rotated) {
                lastYaw = yaw;
                lastPitch = pitch;
            }

            lastOnGround = onGround;
            autoJumpEnabled = client.options.autoJump;
        }
    }
}
