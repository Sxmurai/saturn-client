/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.ui.click.components.impl.button.ModuleButton;
import cope.saturn.util.input.InputUtil;
import cope.saturn.util.render.RenderUtil;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.List;

public class Frame extends Component {
    private static final Color HEADER_COLOR = new Color(29, 29, 29);
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);

    private boolean expanded = true;

    private boolean dragging = false;
    private double dragX, dragY;

    public Frame(double x, Category category, List<Module> modules) {
        super(category.getDisplayName());
        this.x = x;
        this.y = 26.0;

        this.width = 105.0;
        this.height = 15.0;

        modules.forEach((module) -> getChildren().add(new ModuleButton(module)));
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        if (dragging) {
            x = dragX + mouseX;
            y = dragY + mouseY;
        }

        RenderUtil.rect(x, y, height, width, HEADER_COLOR.getRGB());
        mc.textRenderer.draw(matrixStack, getName(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);

        if (expanded) {
            RenderUtil.rect(x, y + height, getTotalHeight(), width, BACKGROUND_COLOR.getRGB());

            double startY = y + height + 1.5;

            for (Component component : children) {
                if (component.isVisible()) {
                    component.setX(x);
                    component.setY(startY);
                    component.setWidth(width);
                    component.setHeight(12.0);

                    component.render(matrixStack, mouseX, mouseY, partialTicks);

                    startY += component.getHeight();
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseInBounds(mouseX, mouseY)) {
            if (button == InputUtil.LEFT_CLICK) {
                dragging = true;

                dragX = x - mouseX;
                dragY = y - mouseY;
            } else if (button == InputUtil.RIGHT_CLICK) {
                expanded = !expanded;
            }
        }

        children.forEach((child) -> {
            if (child.isVisible()) {
                child.mouseClicked(mouseX, mouseY, button);
            }
        });
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == InputUtil.LEFT_CLICK && dragging) {
            dragging = false;
        }
    }

    @Override
    public void keyPressed(char typedChar, int keyCode) {
        children.forEach((child) -> {
            if (child.isVisible()) {
                child.keyPressed(typedChar, keyCode);
            }
        });
    }

    private double getTotalHeight() {
        double height = 0.0;

        for (Component component : children) {
            if (component.isVisible()) {
                height += component.getHeight();
            }
        }

        return height + 1.5;
    }
}
