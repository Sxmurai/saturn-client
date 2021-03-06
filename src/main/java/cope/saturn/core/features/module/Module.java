/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.ModuleToggledEvent;
import cope.saturn.core.settings.Bind;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.internal.Wrapper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;

public class Module implements Wrapper {
    private final String name;
    private final Category category;
    private final String description;

    private final ArrayList<Setting> settings = new ArrayList<>();

    private final Bind bind = new Bind("Bind", GLFW.GLFW_KEY_UNKNOWN);
    private final Setting<Boolean> drawn = new Setting<>("Drawn", true);

    private boolean toggled = false;

    public Module(String name, Category category, String description) {
        this(name, category, description, GLFW.GLFW_KEY_UNKNOWN);
    }

    public Module(String name, Category category, String description, int keyCode) {
        this.name = name;
        this.category = category;
        this.description = description;

        bind.setValue(keyCode);

        settings.add(bind);
        settings.add(drawn);
    }

    public void register() {
        Arrays.stream(getClass().getDeclaredFields())
                .filter((field) -> Setting.class.isAssignableFrom(field.getType()))
                .forEach((field) -> {
                    field.setAccessible(true);

                    try {
                        settings.add((Setting) field.get(this));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    protected void onEnable() {
        Saturn.EVENT_BUS.subscribe(this);
    }

    protected void onDisable() {
        Saturn.EVENT_BUS.unsubscribe(this);
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Setting getSetting(String name) {
        for (Setting setting : settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }

        return null;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public int getBind() {
        return bind.getValue();
    }

    public void setBind(int in) {
        bind.setValue(in);
    }

    public boolean isDrawn() {
        return drawn.getValue();
    }

    public void setDrawn(boolean in) {
        drawn.setValue(in);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void toggle() {
        toggled = !toggled;

        if (toggled) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        Saturn.EVENT_BUS.post(new ModuleToggledEvent(this));

        toggled = true;
        onEnable();
    }

    public void disable() {
        Saturn.EVENT_BUS.post(new ModuleToggledEvent(this));

        toggled = false;
        onDisable();
    }
}
