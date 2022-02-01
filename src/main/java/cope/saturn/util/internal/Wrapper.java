package cope.saturn.util.internal;

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
}
