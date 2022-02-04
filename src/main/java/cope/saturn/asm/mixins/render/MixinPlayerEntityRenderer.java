/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.render;

import cope.saturn.core.Saturn;
import cope.saturn.util.entity.player.rotation.Rotation;
import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {
    private float prevYaw, prevPitch;

    @Inject(method = "render", at = @At("HEAD"))
    public void renderPre(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if (abstractClientPlayerEntity == Wrapper.mc.player) {
            Rotation rotation = Saturn.getInstance().getRotationManager().getRotation();
            if (!rotation.isValid()) {
                return;
            }

            prevYaw = abstractClientPlayerEntity.getYaw();
            prevPitch = abstractClientPlayerEntity.getPitch();

            abstractClientPlayerEntity.setYaw(rotation.yaw());
            abstractClientPlayerEntity.headYaw = rotation.yaw();
            abstractClientPlayerEntity.bodyYaw = rotation.yaw();
            abstractClientPlayerEntity.prevYaw = rotation.yaw();
            abstractClientPlayerEntity.prevBodyYaw = rotation.yaw();
            abstractClientPlayerEntity.prevHeadYaw = rotation.yaw();

            abstractClientPlayerEntity.setPitch(rotation.pitch());
            abstractClientPlayerEntity.prevPitch = rotation.pitch();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderPost(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if (abstractClientPlayerEntity == Wrapper.mc.player) {
            Rotation rotation = Saturn.getInstance().getRotationManager().getRotation();
            if (!rotation.isValid()) {
                return;
            }

            abstractClientPlayerEntity.setYaw(prevYaw);
            abstractClientPlayerEntity.headYaw = prevYaw;
            abstractClientPlayerEntity.bodyYaw = prevYaw;
            abstractClientPlayerEntity.prevYaw = prevYaw;
            abstractClientPlayerEntity.prevBodyYaw = prevYaw;
            abstractClientPlayerEntity.prevHeadYaw = prevYaw;

            abstractClientPlayerEntity.setPitch(prevPitch);
            abstractClientPlayerEntity.prevPitch = prevPitch;
        }
    }
}
