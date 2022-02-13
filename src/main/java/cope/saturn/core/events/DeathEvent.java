/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.player.PlayerEntity;

public class DeathEvent extends Event {
    private final PlayerEntity player;

    public DeathEvent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
