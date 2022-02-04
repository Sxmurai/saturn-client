/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player.rotation;

public enum RotationType {
    /**
     * Server-sided rotations, client is not affected
     */
    PACKET,

    /**
     * Client-sided rotations, will force camera to look at rotation
     */
    CLIENT,

    /**
     * Do not rotate
     */
    NONE
}
