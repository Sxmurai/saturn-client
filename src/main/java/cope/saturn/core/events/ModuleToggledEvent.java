/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import cope.saturn.core.features.module.Module;
import me.bush.eventbus.event.Event;

public class ModuleToggledEvent extends Event {
    private final Module module;
    private final boolean state;

    public ModuleToggledEvent(Module module) {
        this.module = module;
        this.state = module.isToggled();
    }

    public Module getModule() {
        return module;
    }

    public boolean isState() {
        return state;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
