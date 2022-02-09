/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core;

import cope.saturn.core.managers.CommandManager;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.managers.ModuleManager;
import cope.saturn.core.managers.RotationManager;
import cope.saturn.core.managers.interaction.InteractionManager;
import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.ReflectHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturn implements ClientModInitializer {
    public static Saturn INSTANCE;

    public static final String NAME = "Saturn";
    public static final String VERSION = "1.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static EventBus EVENT_BUS = new EventBus(ReflectHandler.class, LOGGER::error);

    private ModuleManager moduleManager;
    private CommandManager commandManager;

    private InventoryManager inventoryManager;
    private RotationManager rotationManager;
    private InteractionManager interactionManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        moduleManager = new ModuleManager();
        commandManager = new CommandManager();

        inventoryManager = new InventoryManager();
        rotationManager = new RotationManager();
        interactionManager = new InteractionManager();
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public RotationManager getRotationManager() {
        return rotationManager;
    }

    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    public static Saturn getInstance() {
        return INSTANCE;
    }
}
