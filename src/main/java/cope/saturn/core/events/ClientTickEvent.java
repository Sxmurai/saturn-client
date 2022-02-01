package cope.saturn.core.events;

import me.bush.eventbus.event.Event;

public class ClientTickEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
