/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.hud;

import cope.saturn.core.features.hud.HUDElement;
import cope.saturn.core.features.module.client.HUDEditor;
import cope.saturn.core.ui.click.components.Component;
import cope.saturn.util.input.InputUtil;
import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.render.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.awt.*;

public class HUDEditorScreen extends Screen implements Wrapper {
    private static HUDEditorScreen INSTANCE;

    private HUDElement dragging = null;
    private double dragX, dragY;

    protected HUDEditorScreen() {
        super(new LiteralText("HUDEditor"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (dragging != null) {
            dragging.setX(dragX + mouseX);
            dragging.setY(dragY + mouseY);
        }

        renderBackground(matrices);

        // draw elements
        getSaturn().getHudManager().getElements().forEach((element) -> {
            if (dragging == null) {
                int color = element.isEnabled() ?
                        new Color(255, 255, 255, 50).getRGB() :
                        new Color(255, 0, 0, 50).getRGB();

                // draw rect under it
                RenderUtil.rect(
                        element.getX() - 2.0,
                        element.getY() - 2.0,
                        element.getHeight() + 4.0,
                        element.getWidth() + 4.0, color);
            } else {
                // highlight the dragging component
                if (dragging.equals(element)) {
                    RenderUtil.rect(
                            element.getX() - 2.0,
                            element.getY() - 2.0,
                            element.getHeight() + 4.0,
                            element.getWidth() + 4.0,
                            new Color(255, 255, 255, 50).getRGB());
                }
            }

            // render element
            element.render(matrices);
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        getSaturn().getHudManager().getElements().forEach((element) -> {
            if (Component.isMouseInBounds(mouseX, mouseY, element.getX(), element.getY(), element.getWidth(), element.getHeight())) {
                if (button == InputUtil.LEFT_CLICK) {
                    dragging = element;

                    dragX = element.getX() - mouseX;
                    dragY = element.getY() - mouseY;
                } else if (button == InputUtil.RIGHT_CLICK) {
                    element.toggle();
                }
            }
        });

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == InputUtil.LEFT_CLICK && dragging != null) {
            dragging = null;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        HUDEditor.INSTANCE.disable();
    }

    public static HUDEditorScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUDEditorScreen();
        }

        return INSTANCE;
    }
}
