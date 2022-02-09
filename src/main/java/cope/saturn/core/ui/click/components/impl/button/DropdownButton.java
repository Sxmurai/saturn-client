/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl.button;

import cope.saturn.core.settings.Bind;
import cope.saturn.core.settings.Setting;
import cope.saturn.core.ui.click.components.Component;
import cope.saturn.util.input.MouseUtil;
import cope.saturn.util.render.RenderUtil;
import cope.saturn.util.render.TextUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.List;

public class DropdownButton extends Button {
    private final Setting setting;

    private boolean expanded = false;

    public DropdownButton(Setting setting) {
        super(setting.getName());

        this.setting = setting;

        ((List<Setting>) setting.getChildren()).forEach((child) -> {
            if (!child.getChildren().isEmpty()) {
                children.add(new DropdownButton(child));
            } else if (child instanceof Bind) {
                children.add(new BindButton((Bind) child));
            } else {
                if (child.getValue() instanceof Boolean) {
                    children.add(new BooleanButton(child));
                } else if (child.getValue() instanceof Enum) {
                    children.add(new EnumButton(child));
                }
            }
        });
    }

    @Override
    public void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks) {
        if (setting.getValue() instanceof Boolean) {
            if ((boolean) setting.getValue()) {
                RenderUtil.rect(x, y, height, width, new Color(150, 46, 230).getRGB());
            }

            mc.textRenderer.draw(matrixStack, getName(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
        } else if (setting.getValue() instanceof Enum) {
            mc.textRenderer.draw(matrixStack, setting.getName() + " \u00A77" + ((Enum) setting.getValue()).name(), (float) (x + 2.3), (float) TextUtil.alignH(y, height), -1);
        }

        String dots = expanded ? ". " : "...";
        int textWidth = mc.textRenderer.getWidth(dots);

        mc.textRenderer.draw(matrixStack, dots, (float) (x + width) - textWidth - 2.3f, (float) TextUtil.alignH(y, height), -1);

        if (expanded) {
            double startY = y + height;

            for (cope.saturn.core.ui.click.components.Component component : children) {
                if (component.isVisible()) {
                    component.setX(x + 2.0);
                    component.setY(startY);
                    component.setHeight(12.0);
                    component.setWidth(width - 4.0);

                    component.render(matrixStack, mouseX, mouseY, partialTicks);

                    startY += component.getHeight();
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        children.forEach((child) -> {
            if (child.isVisible()) {
                child.mouseClicked(mouseX, mouseY, button);
            }
        });
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(char typedChar, int keyCode) {
        children.forEach((child) -> {
            if (child.isVisible()) {
                child.keyPressed(typedChar, keyCode);
            }
        });
    }

    @Override
    public void onInteract(int button) {
        if (button == MouseUtil.LEFT_CLICK) {
            if (setting.getValue() instanceof Boolean) {
                setting.setValue(!(boolean) setting.getValue());
            } else if (setting.getValue() instanceof Enum) {
                setting.setValue(Setting.current((Enum) setting.getValue()));
            }
        } else if (button == MouseUtil.RIGHT_CLICK) {
            expanded = !expanded;
        }
    }

    @Override
    public double getHeight() {
        double height = this.height;

        if (expanded) {
            for (Component component : children) {
                if (component.isVisible()) {
                    height += component.getHeight();
                }
            }
        }

        return height;
    }
}
