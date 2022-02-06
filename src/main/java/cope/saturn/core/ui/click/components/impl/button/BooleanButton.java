/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl.button;

import cope.saturn.core.settings.Setting;
import cope.saturn.util.input.MouseUtil;
import cope.saturn.util.render.RenderUtil;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class BooleanButton extends Button {
    private final Setting<Boolean> setting;

    public BooleanButton(Setting<Boolean> setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        if (setting.getValue()) {
            RenderUtil.rect(x, y, height, width, new Color(150, 46, 230).getRGB());
        }

        mc.textRenderer.draw(matrixStack, setting.getName(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
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
            setting.setValue(!setting.getValue());
        }
    }
}
