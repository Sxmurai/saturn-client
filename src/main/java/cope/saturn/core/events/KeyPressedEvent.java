package cope.saturn.core.events;

import me.bush.eventbus.event.Event;

public class KeyPressedEvent extends Event {
    private final int key, scancode, action, modifiers;

    public KeyPressedEvent(int key, int scancode, int action, int modifiers) {
        this.key = key;
        this.action = action;
        this.scancode = scancode;
        this.modifiers = modifiers;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }

    public int getScancode() {
        return scancode;
    }

    public int getModifiers() {
        return modifiers;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
