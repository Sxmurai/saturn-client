/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.miscellaneous;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", Category.MISCELLANEOUS, "Automatically respawns you when you die");
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (mc.currentScreen instanceof DeathScreen || mc.player.getHealth() <= 0.0f) {
            mc.player.requestRespawn();
        }
    }
}
