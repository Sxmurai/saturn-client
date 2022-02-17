/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core;

import cope.saturn.core.managers.*;
import cope.saturn.core.managers.interaction.InteractionManager;
import cope.saturn.core.updater.VersionChecker;
import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.ReflectHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturn implements ClientModInitializer {
    public static Saturn INSTANCE;

    public static final String NAME = "Saturn";
    public static final String VERSION = "0.9";

    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static EventBus EVENT_BUS = new EventBus(ReflectHandler.class, LOGGER::error);

    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private HUDManager hudManager;

    private InventoryManager inventoryManager;
    private RotationManager rotationManager;
    private InteractionManager interactionManager;
    private ServerManager serverManager;
    private TotemPopManager totemPopManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        hudManager = new HUDManager();

        inventoryManager = new InventoryManager();
        rotationManager = new RotationManager();
        interactionManager = new InteractionManager();
        serverManager = new ServerManager();
        totemPopManager = new TotemPopManager();

        // handle updates
        VersionChecker.handleUpdates();
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public HUDManager getHudManager() {
        return hudManager;
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

    public ServerManager getServerManager() {
        return serverManager;
    }

    public TotemPopManager getTotemPopManager() {
        return totemPopManager;
    }

    public static Saturn getInstance() {
        return INSTANCE;
    }
}
