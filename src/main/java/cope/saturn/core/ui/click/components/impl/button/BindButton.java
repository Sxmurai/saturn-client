/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl.button;

import cope.saturn.core.settings.Bind;
import cope.saturn.util.input.InputUtil;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class BindButton extends Button {
    private final Bind bind;

    private boolean listening;

    public BindButton(Bind bind) {
        super(bind.getName());
        this.bind = bind;
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        String text = "";
        if (listening) {
            text = "Listening...";
        } else {
            text = getName() + " " + InputUtil.getKeyName(bind.getValue());
        }

        mc.textRenderer.draw(matrixStack, text, (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(char typedChar, int keyCode) {
        if (listening) {
            listening = false;

            if (keyCode == GLFW.GLFW_KEY_UNKNOWN || keyCode == GLFW.GLFW_KEY_DELETE) {
                bind.setValue(GLFW.GLFW_KEY_UNKNOWN);
                return;
            }

            bind.setValue(keyCode);
        }
    }

    @Override
    public void onInteract(int button) {
        listening = !listening;
    }
}
