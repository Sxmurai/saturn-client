/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.internal;

import cope.saturn.core.Saturn;
import net.minecraft.client.MinecraftClient;

public interface Wrapper {
    MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Checks if modules are able to run safely
     * @return if player or the world == null
     */
    default boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    default Saturn getSaturn() {
        return Saturn.getInstance();
    }
}
