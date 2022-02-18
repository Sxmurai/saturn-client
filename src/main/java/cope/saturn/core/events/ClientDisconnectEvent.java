/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class ClientDisconnectEvent extends Event {
    private final Text reason;
    private final ServerInfo serverInfo;

    public ClientDisconnectEvent(Text reason, ServerInfo serverInfo) {
        this.reason = reason;
        this.serverInfo = serverInfo;
    }

    public Text getReason() {
        return reason;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
