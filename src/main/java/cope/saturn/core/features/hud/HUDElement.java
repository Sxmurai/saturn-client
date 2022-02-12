/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.hud;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class HUDElement implements Wrapper {
    private final String name;
    private final Category category;

    protected boolean enabled = false;

    protected double x, y;
    protected double width, height;

    public HUDElement(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    /**
     * Abstract method meant to be overwritten to render this hud element to the screen
     * @param stack The current matrix stack
     */
    public abstract void render(MatrixStack stack);

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
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

    public void toggle() {
        enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
