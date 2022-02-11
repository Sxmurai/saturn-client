/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl;

import cope.saturn.core.settings.Setting;
import cope.saturn.core.ui.click.components.Component;
import cope.saturn.util.input.InputUtil;
import cope.saturn.util.render.RenderUtil;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class Slider extends Component {
    private final Setting<Number> setting;
    private final float difference;

    public Slider(Setting<Number> setting) {
        super(setting.getName());

        this.setting = setting;
        this.difference = setting.getMax().floatValue() - setting.getMin().floatValue();
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        if (InputUtil.isButtonDown(InputUtil.LEFT_CLICK) && isMouseInBounds(mouseX, mouseY)) {
            set(mouseX);
        }

        double w = setting.getValue().floatValue() <= setting.getMin().floatValue() ? 0.0 : width * partialMultiplier();

        RenderUtil.rect(x, y, height, w, new Color(150, 46, 230).getRGB());

        mc.textRenderer.draw(matrixStack, getName() + " " + setting.getValue(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == InputUtil.LEFT_CLICK && isMouseInBounds(mouseX, mouseY)) {
            set(mouseX);
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(char typedChar, int keyCode) {

    }

    private float part() {
        return setting.getValue().floatValue() - setting.getMin().floatValue();
    }

    private float partialMultiplier() {
        return part() / difference;
    }

    private void set(double mouseX) {
        float percent = (float) ((mouseX - x) / (float) width);

        if (setting.getValue() instanceof Float) {
            float result = setting.getMin().floatValue() + difference * percent;
            setting.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (setting.getValue() instanceof Double) {
            double result = setting.getMin().doubleValue() + difference * percent;
            setting.setValue(Math.round(10.0 * result) / 10.0);
        } else {
            setting.setValue(Math.round(setting.getMin().intValue() + difference * percent));
        }
    }
}
