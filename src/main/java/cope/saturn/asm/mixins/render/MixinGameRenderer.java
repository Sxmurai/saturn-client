/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import cope.saturn.core.Saturn;
import cope.saturn.core.events.RenderWorldEvent;
import cope.saturn.core.features.module.visuals.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ItemStack floatingItem;

    @Shadow
    private float fovMultiplier;

    // Credit: https://github.com/MeteorDevelopment/meteor-client/blob/master/src/main/java/meteordevelopment/meteorclient/mixin/GameRendererMixin.java#L54-L72
    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = { "ldc=hand" }), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void render(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo info, boolean bl, Camera camera, MatrixStack matrixStack, double d, float f, Matrix4f matrix4f) {
        Saturn.EVENT_BUS.post(new RenderWorldEvent(matrices, camera.getPos()));
        RenderSystem.applyModelViewMatrix();
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void bobViewWhenHurt(MatrixStack matrices, float f, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.hurtcam.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderFloatingItem", at = @At("HEAD"), cancellable = true)
    private void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.totemOverlay.getValue() &&
                floatingItem != null && floatingItem.getItem().equals(Items.TOTEM_OF_UNDYING)) {
            info.cancel();
        }
    }

    @Inject(method = "renderNausea", at = @At("HEAD"), cancellable = true)
    private void renderNausea(float distortionStrength, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.nausea.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "updateFovMultiplier", at = @At("HEAD"), cancellable = true)
    private void updateFovMultiplier(CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.fov.getValue()) {
            info.cancel();

            fovMultiplier = 1.0f;
        }
    }
}
