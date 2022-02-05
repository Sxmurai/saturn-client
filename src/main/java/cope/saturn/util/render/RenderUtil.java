/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;

public class RenderUtil {
    /**
     * Draws a rectangle to the screen
     * @param x The x coordinate
     * @param y The y coordinate
     * @param height The height of the rect
     * @param width The width of the rect
     * @param color The color of the rect
     */
    public static void rect(double x, double y, double height, double width, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x, y + height, 0.0).color(color).next();
        buffer.vertex(x + width, y + height, 0.0).color(color).next();
        buffer.vertex(x + width, y, 0.0).color(color).next();
        buffer.vertex(x, y, 0.0).color(color).next();

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
