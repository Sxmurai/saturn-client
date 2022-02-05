/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public abstract class Component implements Wrapper {
    private final String name;
    protected double x, y, width, height;

    protected final ArrayList<Component> children = new ArrayList<>();

    public Component(String name) {
        this.name = name;
    }

    public abstract void render(MatrixStack matrixStack, float mouseX, float mouseY, float partialTicks);
    public abstract void mouseClicked(double mouseX, double mouseY, int button);
    public abstract void mouseReleased(double mouseX, double mouseY, int button);
    public abstract void keyPressed(char typedChar, int keyCode);

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isVisible() {
        return true;
    }

    public ArrayList<Component> getChildren() {
        return children;
    }

    public static boolean isMouseInBounds(double mX, double mY, double x, double y, double w, double h) {
        return mX >= x && mX <= x + w && mY >= y && mY <= y + h;
    }

    public boolean isMouseInBounds(double mx, double mY) {
        return isMouseInBounds(mx, mY, x, y, width, height);
    }
}
