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
