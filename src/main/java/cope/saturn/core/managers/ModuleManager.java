/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.KeyPressedEvent;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.features.module.client.ClickGUI;
import cope.saturn.core.features.module.combat.*;
import cope.saturn.core.features.module.movement.NoSlow;
import cope.saturn.core.features.module.movement.Sprint;
import cope.saturn.core.features.module.visuals.Fullbright;
import cope.saturn.core.features.module.world.Scaffold;
import cope.saturn.core.features.module.world.Timer;
import cope.saturn.util.internal.Wrapper;
import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ModuleManager implements Wrapper {
    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // subscribe to event bus (key presses, etc)
        Saturn.EVENT_BUS.subscribe(this);

        // Client
        modules.add(new ClickGUI());

        // Combat
        modules.add(new AimBot());
        modules.add(new Aura());
        modules.add(new AutoTotem());
        modules.add(new BowRelease());
        modules.add(new Criticals());

        // Movement
        modules.add(new NoSlow());
        modules.add(new Sprint());

        // Visuals
        modules.add(new Fullbright());

        // World
        modules.add(new Scaffold());
        modules.add(new Timer());

        // register all settings
        modules.forEach(Module::register);

        Saturn.LOGGER.info("Loaded {} modules.", modules.size());
    }

    @EventListener
    public void onKeyPressed(KeyPressedEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS &&
                event.getKey() != GLFW.GLFW_KEY_UNKNOWN &&
                mc.currentScreen == null) {
            for (Module module : modules) {
                if (module.getBind() == event.getKey()) {
                    module.toggle();
                }
            }
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
