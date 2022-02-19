/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.config.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cope.saturn.core.config.Config;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.ModuleManager;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.internal.FileUtil;

public class Modules extends Config {
    private final ModuleManager moduleManager;

    public Modules(ModuleManager moduleManager) {
        super("modules", FileUtil.CONFIG_FOLDER.resolve("modules.json"));
        this.moduleManager = moduleManager;
    }

    @Override
    public void load() {
        String fileData = FileUtil.read(path);
        if (fileData == null) {
            save();
            return;
        }

        JsonObject json = new Gson().fromJson(fileData, JsonObject.class);

        for (String name : json.keySet()) {
            Module module = moduleManager.getModule(name);
            if (module == null) {
                LOGGER.info("Module {} was not found.", name);
                continue;
            }

            JsonObject data = json.getAsJsonObject(name);

            if (data.get("Toggled").getAsBoolean()) {
                module.toggle();
            }

            if (data.has("Settings")) {
                JsonObject settings = data.getAsJsonObject("Settings");

                for (String settingName : settings.keySet()) {
                    Setting setting = module.getSetting(settingName);
                    if (setting == null) {
                        LOGGER.info("Setting {} was not found on {}", settingName, name);
                        continue;
                    }

                    JsonElement element = settings.get(settingName);

                    if (setting.getValue() instanceof Boolean) {
                        setting.setValue(element.getAsBoolean());
                    } else if (setting.getValue() instanceof Number) {
                        if (setting.getValue() instanceof Float) {
                            setting.setValue(element.getAsFloat());
                        } else if (setting.getValue() instanceof Double) {
                            setting.setValue(element.getAsDouble());
                        } else if (setting.getValue() instanceof Integer) {
                            setting.setValue(element.getAsInt());
                        }
                    } else if (setting.getValue() instanceof String) {
                        setting.setValue(element.getAsString());
                    }
                }
            }
        }
    }

    @Override
    public void save() {
        JsonObject json = new JsonObject();

        for (Module module : moduleManager.getModules()) {
            JsonObject data = new JsonObject();

            data.addProperty("Name", module.getName());
            data.addProperty("Toggled", module.isToggled());

            JsonObject settings = new JsonObject();
            for (Setting setting : module.getSettings()) {
                if (setting.getValue() instanceof Number) {
                    settings.addProperty(setting.getName(), (Number) setting.getValue());
                } else if (setting.getValue() instanceof String) {
                    settings.addProperty(setting.getName(), (String) setting.getValue());
                } else if (setting.getValue() instanceof Boolean) {
                    settings.addProperty(setting.getName(), (Boolean) setting.getValue());
                }
            }

            data.add("Settings", settings);
            json.add(module.getName(), data);
        }

        FileUtil.write(path, json.toString());
    }
}
