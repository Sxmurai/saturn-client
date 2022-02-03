package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.KeyPressedEvent;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.features.module.combat.AimBot;
import cope.saturn.core.features.module.combat.Aura;
import cope.saturn.core.features.module.combat.Criticals;
import cope.saturn.core.features.module.movement.NoSlow;
import cope.saturn.core.features.module.movement.Sprint;
import cope.saturn.core.features.module.visuals.Fullbright;
import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ModuleManager {
    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // subscribe to event bus (key presses, etc)
        Saturn.EVENT_BUS.subscribe(this);

        // Combat
        modules.add(new AimBot());
        modules.add(new Aura());
        modules.add(new Criticals());

        // Movement
        modules.add(new NoSlow());
        modules.add(new Sprint());

        // Visuals
        modules.add(new Fullbright());

        // register all settings
        modules.forEach(Module::register);

        Saturn.LOGGER.info("Loaded {} modules.", modules.size());
    }

    @EventListener
    public void onKeyPressed(KeyPressedEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() != GLFW.GLFW_KEY_UNKNOWN) {
            for (Module module : modules) {
                if (module.getBind() == event.getKey()) {
                    module.toggle();
                }
            }
        }
    }
}
