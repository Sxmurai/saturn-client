/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.asm.mixins.client.IMinecraftClient;
import cope.saturn.asm.mixins.client.IRenderTickCounter;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;

public class Timer extends Module {
    public Timer() {
        super("Timer", Category.WORLD, "Modifies the games tick length");
    }

    public static final Setting<Float> speed = new Setting<>("Speed", 1.5f, 0.1f, 20.0f);

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!nullCheck()) {
            // reset tick length to default
            // default is 1000 / 20 (50, which 50ms = 1 tick)
            ((IRenderTickCounter) ((IMinecraftClient) mc).getRenderTickCounter()).setTickTime(50.0f);
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        ((IRenderTickCounter) ((IMinecraftClient) mc).getRenderTickCounter()).setTickTime(50.0f / speed.getValue());
    }
}
