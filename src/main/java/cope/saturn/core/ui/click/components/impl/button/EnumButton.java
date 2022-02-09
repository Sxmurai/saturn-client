/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl.button;

import cope.saturn.core.settings.Setting;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;

public class EnumButton extends Button {
    private final Setting<Enum> setting;

    public EnumButton(Setting<Enum> setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        mc.textRenderer.draw(matrixStack, setting.getName() + " \u00A77" + setting.getValue().name(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(char typedChar, int keyCode) {

    }

    @Override
    public void onInteract(int button) {
        setting.setValue(Setting.increaseEnum(setting.getValue()));
    }
}
