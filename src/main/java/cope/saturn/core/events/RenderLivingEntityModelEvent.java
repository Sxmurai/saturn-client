/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class RenderLivingEntityModelEvent extends Event {
    private final EntityModel model;
    private final MatrixStack matrixStack;
    private final VertexConsumer vertexConsumer;
    private final int light, overlay;
    private final float red, green, blue, alpha;

    public RenderLivingEntityModelEvent(EntityModel model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.model = model;
        this.matrixStack = matrixStack;
        this.vertexConsumer = vertexConsumer;
        this.light = light;
        this.overlay = overlay;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public EntityModel getModel() {
        return model;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public VertexConsumer getVertexConsumer() {
        return vertexConsumer;
    }

    public int getLight() {
        return light;
    }

    public int getOverlay() {
        return overlay;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    /**
     * A helper method to quickly call model#render
     */
    public void render() {
        model.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
