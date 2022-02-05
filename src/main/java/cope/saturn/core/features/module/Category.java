/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module;

public enum Category {
    CLIENT("Client"),
    COMBAT("Combat"),
    MISCELLANEOUS("Miscellaneous"),
    MOVEMENT("Movement"),
    VISUALS("Visuals"),
    WORLD("World");

    private final String displayName;
    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
