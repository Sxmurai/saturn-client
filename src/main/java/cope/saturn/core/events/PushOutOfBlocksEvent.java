/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;

public class PushOutOfBlocksEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
