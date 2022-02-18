/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.EntityUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

// i have 0 clue what the fuck im doing
public class Nametags extends Module {
    public static Nametags INSTANCE;

    public Nametags() {
        super("Nametags", Category.VISUALS, "Renders nametags");
        INSTANCE = this;
    }

    public static final Setting<Boolean> health = new Setting<>("Health", true);
    public static final Setting<Boolean> colors = new Setting<>(health, "Colors", true);
    public static final Setting<Boolean> ping = new Setting<>("Ping", true);
    public static final Setting<Boolean> pops = new Setting<>("Pops", true);

    public static final Setting<Object> items = new Setting<>("Items", null);
    public static final Setting<Boolean> offhand = new Setting<>(items, "Offhand", true);
    public static final Setting<Boolean> mainhand = new Setting<>(items, "Mainhand", true);
    public static final Setting<Boolean> armor = new Setting<>(items, "Armor", true);
    public static final Setting<Boolean> enchantments = new Setting<>(armor, "Enchantments", true);
    public static final Setting<Boolean> percentage = new Setting<>(armor, "Percentage", true);

    public static void render(AbstractClientPlayerEntity player, MatrixStack stack, VertexConsumerProvider consumerProvider, int light) {
        stack.push();

        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(1.0f, -1500000.0f);

        stack.translate(0.0, player.getHeight() + (player.isSneaking() ? 0.3f : 0.5f), 0.0);
        stack.multiply(mc.getEntityRenderDispatcher().getRotation());
        stack.scale(-0.025f, -0.025f, 0.025f);

        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        String text = player.getName().asString();
        if (health.getValue()) {
            float totalHealth = EntityUtil.getHealth(player);

            String color = "";

            if (colors.getValue()) {
                if (totalHealth >= 20.0f) {
                    color = "\u00A7a";
                } else if (totalHealth >= 17.0f) {
                    color = "\u00A7e";
                } else if (totalHealth >= 10.0f) {
                    color = " \u00A7c";
                } else if (totalHealth <= 10.0f) {
                    color = " \u00A74";
                }
            }

            text += " " + color + String.format("%.1f", EntityUtil.getHealth(player)) + " \u00A7r";
        }

        if (ping.getValue()) {
            text += " " + INSTANCE.getSaturn().getServerManager().getLatency(player.getUuid()) + "ms";
        }

        if (pops.getValue()) {
            int totems = INSTANCE.getSaturn().getTotemPopManager().get(player);
            if (totems != -1) {
                text += " -" + totems;
            }
        }

        mc.textRenderer.draw(text, -mc.textRenderer.getWidth(text) / 2.0f, 0.0f, -1, false, matrix4f, consumerProvider, true, 1056964608, light);

        int x = (-24 / 2 * player.getInventory().armor.size()) + 8;

        if (offhand.getValue() && !player.getOffHandStack().isEmpty()) {
            renderItem(player.getOffHandStack(), stack, x);
            x += 16;
        }

        if (armor.getValue()) {
            for (int i = 3; i >= 0; --i) {
                ItemStack itemStack = player.getInventory().getArmorStack(i);
                if (!itemStack.isEmpty()) {
                    renderItem(itemStack, stack, x);
                    x += 16;
                }
            }
        }

        if (mainhand.getValue() && !player.getMainHandStack().isEmpty()) {
            renderItem(player.getMainHandStack(), stack, x);
            x += 16;
        }

        RenderSystem.polygonOffset(1.0f, 1500000.0f);
        RenderSystem.disablePolygonOffset();

        stack.pop();
    }

    private static void renderItem(ItemStack itemStack, MatrixStack stack, int x) {
        stack.push();

        stack.translate(x, -10, 0);
        stack.scale(-16, -16, 1);

        mc.getBufferBuilders().getEntityVertexConsumers().draw();

        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.enableBlend();

        mc.getItemRenderer().zOffset = -150.0f;
        mc.getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.GUI, 0xF000F0, OverlayTexture.DEFAULT_UV, stack, mc.getBufferBuilders().getEntityVertexConsumers(), 0);
        mc.getBufferBuilders().getEntityVertexConsumers().draw();
        mc.getItemRenderer().zOffset = 0.0f;

        RenderSystem.disableBlend();
        DiffuseLighting.enableGuiDepthLighting();

        stack.pop();
    }
}
