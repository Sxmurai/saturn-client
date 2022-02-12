/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.KeyPressedEvent;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.features.module.client.ClickGUI;
import cope.saturn.core.features.module.client.HUD;
import cope.saturn.core.features.module.client.HUDEditor;
import cope.saturn.core.features.module.combat.*;
import cope.saturn.core.features.module.miscellaneous.PingSpoof;
import cope.saturn.core.features.module.movement.AntiVoid;
import cope.saturn.core.features.module.movement.NoSlow;
import cope.saturn.core.features.module.movement.Sprint;
import cope.saturn.core.features.module.movement.Velocity;
import cope.saturn.core.features.module.visuals.*;
import cope.saturn.core.features.module.world.*;
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
        modules.add(new HUD());
        modules.add(new HUDEditor());

        // Combat
        modules.add(new AimBot());
        modules.add(new Aura());
        modules.add(new AutoCrystal());
        modules.add(new AutoTotem());
        modules.add(new BowRelease());
        modules.add(new Criticals());
        modules.add(new FeetTrap());

        // Miscellaneous
        modules.add(new PingSpoof());

        // Movement
        modules.add(new AntiVoid());
        modules.add(new NoSlow());
        modules.add(new Sprint());
        modules.add(new Velocity());

        // Visuals
        modules.add(new CameraClip());
        modules.add(new Chams());
        modules.add(new Fullbright());
        modules.add(new NoRender());
        modules.add(new Tracers());

        // World
        modules.add(new AntiHunger());
        modules.add(new AutoFish());
        modules.add(new AutoTool());
        modules.add(new Scaffold());
        modules.add(new SpeedMine());
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
