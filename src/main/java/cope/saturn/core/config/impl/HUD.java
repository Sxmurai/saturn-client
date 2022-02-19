/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.config.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cope.saturn.core.config.Config;
import cope.saturn.core.features.hud.HUDElement;
import cope.saturn.core.managers.HUDManager;
import cope.saturn.util.internal.FileUtil;

public class HUD extends Config {
    private final HUDManager hudManager;

    public HUD(HUDManager hudManager) {
        super("HUD", FileUtil.CONFIG_FOLDER.resolve("hud.json"));
        this.hudManager = hudManager;
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
            HUDElement element = hudManager.getElement(name);
            if (element == null) {
                LOGGER.info("HUD element {} was not found.", name);
                continue;
            }
        }
    }

    @Override
    public void save() {

    }
}
