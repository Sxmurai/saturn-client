/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.input.Input;

public class ItemSlowdownEvent extends Event {
    private final Input input;

    public ItemSlowdownEvent(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return input;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
