/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.util.internal.Wrapper;
import me.bush.eventbus.annotation.EventListener;

public class ServerManager implements Wrapper {
    private double speed = 0.0;

    public ServerManager() {
        Saturn.EVENT_BUS.subscribe(this);
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        speed = Math.sqrt(Math.pow(mc.player.getX() - mc.player.prevX, 2) + Math.pow(mc.player.getZ() - mc.player.prevZ, 2));
    }

    public double getSpeed() {
        return speed;
    }
}
