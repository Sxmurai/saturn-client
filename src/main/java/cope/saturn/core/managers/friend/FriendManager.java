/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers.friend;

import java.util.ArrayList;
import java.util.UUID;

public class FriendManager {
    private final ArrayList<Friend> friends = new ArrayList<>();

    public void add(UUID uuid, String alias) {
        friends.add(new Friend(uuid, alias));
    }

    public void remove(UUID uuid) {
        friends.removeIf((friend) -> friend.uuid().equals(uuid));
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public boolean isFriend(UUID uuid) {
        return friends.stream().anyMatch((friend) -> friend.uuid().equals(uuid));
    }
}
