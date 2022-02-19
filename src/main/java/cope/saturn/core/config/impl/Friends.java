/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.config.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cope.saturn.core.config.Config;
import cope.saturn.core.managers.friend.FriendManager;
import cope.saturn.util.internal.FileUtil;

import java.util.UUID;

public class Friends extends Config {
    private final FriendManager friendManager;

    public Friends(FriendManager friendManager) {
        super("friends", FileUtil.CONFIG_FOLDER.resolve("friends.json"));
        this.friendManager = friendManager;
    }

    @Override
    public void load() {
        String fileData = FileUtil.read(path);
        if (fileData == null) {
            save();
            return;
        }

        JsonArray json = new Gson().fromJson(fileData, JsonArray.class);

        for (JsonElement o : json) {
            if (!(o instanceof JsonObject object)) {
                continue;
            }

            friendManager.add(
                    UUID.fromString(object.get("Uuid").getAsString()),
                    object.get("Alias").getAsString());
        }
    }

    @Override
    public void save() {
        JsonArray array = new JsonArray();

        friendManager.getFriends().forEach((friend) -> {
            JsonObject object = new JsonObject();

            object.addProperty("Uuid", friend.uuid().toString());
            object.addProperty("Alias", friend.alias());

            array.add(object);
        });

        FileUtil.write(path, array.toString());
    }
}
