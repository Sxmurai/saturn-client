/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.DeathEvent;
import cope.saturn.core.events.TotemPopEvent;
import cope.saturn.core.features.module.client.Notifier;
import cope.saturn.util.internal.ChatUtil;
import cope.saturn.util.internal.Wrapper;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TotemPopManager implements Wrapper {
    private final Map<PlayerEntity, Integer> totemPops = new ConcurrentHashMap<>();

    public TotemPopManager() {
        Saturn.EVENT_BUS.subscribe(this);
    }

    @EventListener
    public void onTotemPop(TotemPopEvent event) {
        if (event.getPlayer() != null) {
            PlayerEntity player = event.getPlayer();

            totemPops.merge(player, 1, Integer::sum);

            if (Notifier.INSTANCE.isToggled() && Notifier.totems.getValue()) {
                if (!Notifier.self.getValue() && player.equals(mc.player)) {
                    return;
                }

                ChatUtil.sendEditable(player.hashCode(),
                        player.getName().asString() + " has popped " + totemPops.get(player) + " totem(s).");
            }
        }
    }

    @EventListener
    public void onDeath(DeathEvent event) {
        if (totemPops.containsKey(event.getPlayer())) {
            PlayerEntity player = event.getPlayer();

            if (Notifier.INSTANCE.isToggled() && Notifier.totems.getValue()) {
                if (!Notifier.self.getValue() && player.equals(mc.player)) {
                    return;
                }

                ChatUtil.sendEditable(player.hashCode(),
                        player.getName().asString() + " has died after popping " + totemPops.get(player) + " totem(s).");

                // remove player from map
                totemPops.remove(player);
            }
        }
    }
}
