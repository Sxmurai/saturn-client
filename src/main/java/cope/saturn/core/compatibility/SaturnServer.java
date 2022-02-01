package cope.saturn.core.compatibility;

import net.fabricmc.api.DedicatedServerModInitializer;

public class SaturnServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        System.out.println("ATTENTION! This mod should **NOT** be used in a server environment.");
    }
}
