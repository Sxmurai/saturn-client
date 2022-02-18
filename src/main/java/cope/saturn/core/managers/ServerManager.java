/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.util.internal.Wrapper;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.network.PlayerListEntry;

import java.util.UUID;

public class ServerManager implements Wrapper {
    private double speed = 0.0;

    public ServerManager() {
        Saturn.EVENT_BUS.subscribe(this);
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        speed = Math.sqrt(Math.pow(mc.player.getX() - mc.player.prevX, 2) + Math.pow(mc.player.getZ() - mc.player.prevZ, 2));
    }

    /**
     * Gets the latency for a player
     * @param uuid The players UUID
     * @return the ping or 0 if none found
     */
    public int getLatency(UUID uuid) {
        try {
            for (PlayerListEntry entry : mc.player.networkHandler.getPlayerList()) {
                if (entry.getProfile().getId().equals(uuid)) {
                    return entry.getLatency();
                }
            }
        } catch (Exception ignored) {

        }

        return 0;
    }

    public double getSpeed() {
        return speed;
    }
}
