package cope.saturn.core;

import cope.saturn.core.managers.ModuleManager;
import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.ReflectHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturn implements ClientModInitializer {
    public static final String NAME = "Saturn";
    public static final String VERSION = "1.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static EventBus EVENT_BUS = new EventBus(ReflectHandler.class, LOGGER::error);

    private ModuleManager moduleManager;

    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();
    }
}
