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
import cope.saturn.core.features.module.client.Notifier;
import cope.saturn.core.features.module.combat.*;
import cope.saturn.core.features.module.miscellaneous.*;
import cope.saturn.core.features.module.movement.*;
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
        modules.add(new Notifier());

        // Combat
        modules.add(new AimBot());
        modules.add(new Aura());
        modules.add(new AutoCrystal());
        modules.add(new AutoTotem());
        modules.add(new BowRelease());
        modules.add(new Criticals());
        modules.add(new FeetTrap());
        modules.add(new SelfFill());

        // Miscellaneous
        modules.add(new AutoReconnect());
        modules.add(new AutoRespawn());
        modules.add(new MiddleClick());
        modules.add(new PingSpoof());
        modules.add(new Portal());

        // Movement
        modules.add(new AntiVoid());
        modules.add(new ElytraFlight());
        modules.add(new EntitySpeed());
        modules.add(new Flight());
        modules.add(new Jesus());
        modules.add(new NoSlow());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Velocity());

        // Visuals
        modules.add(new CameraClip());
        modules.add(new Chams());
        modules.add(new Fullbright());
        modules.add(new Nametags());
        modules.add(new NoRender());
        modules.add(new Tracers());
        modules.add(new Wallhack());

        // World
        modules.add(new AirPlace());
        modules.add(new AntiHunger());
        modules.add(new AutoFish());
        modules.add(new AutoTool());
        modules.add(new FastPlace());
        modules.add(new Freecam());
        modules.add(new Scaffold());
        modules.add(new SpeedMine());
        modules.add(new Timer());
        modules.add(new Yaw());

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

    public <T extends Module> T getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return (T) module;
            }
        }

        return null;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
