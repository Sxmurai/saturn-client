/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl;

import cope.saturn.core.features.module.Module;
import cope.saturn.util.input.MouseUtil;
import cope.saturn.util.render.RenderUtil;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class ModuleButton extends Button {
    private final Module module;

    private boolean expanded = false;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        if (module.isToggled()) {
            RenderUtil.rect(x, y, height, width, new Color(150, 46, 230).getRGB());
        }

        mc.textRenderer.draw(matrixStack, getName(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(char typedChar, int keyCode) {

    }

    @Override
    public void onInteract(int button) {
        if (button == MouseUtil.LEFT_CLICK) {
            module.toggle();
        } else if (button == MouseUtil.RIGHT_CLICK) {
            expanded = !expanded;
        }
    }
}
