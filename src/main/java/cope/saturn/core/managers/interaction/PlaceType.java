/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers.interaction;

public enum PlaceType {
    /**
     * Places using the player controller
     */
    CLIENT,

    /**
     * Places with a single packet
     *
     * This seems to cause alot of lagbacks when placing via scaffold, so idk bout this one.
     * Might remove this all together
     */
    PACKET
}
