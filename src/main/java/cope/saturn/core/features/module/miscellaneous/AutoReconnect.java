/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.miscellaneous;

import cope.saturn.core.events.ClientDisconnectEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.network.ServerInfo;

public class AutoReconnect extends Module {
    public static AutoReconnect INSTANCE;
    public static ServerInfo serverInfo;

    public AutoReconnect() {
        super("AutoReconnect", Category.MISCELLANEOUS, "Automatically reconnects you to the last server");
        INSTANCE = this;
    }

    @EventListener
    public void onClientDisconnect(ClientDisconnectEvent event) {
        serverInfo = event.getServerInfo();
    }
}
