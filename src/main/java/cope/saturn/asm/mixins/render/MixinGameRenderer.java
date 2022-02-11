/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.render;

import cope.saturn.core.features.module.visuals.NoRender;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    private ItemStack floatingItem;

    @Shadow
    private float fovMultiplier;

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
