/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core;

import cope.saturn.core.managers.*;
import cope.saturn.core.managers.friend.FriendManager;
import cope.saturn.core.managers.interaction.InteractionManager;
import cope.saturn.core.updater.VersionChecker;
import cope.saturn.util.internal.FileUtil;
import me.bush.eventbus.bus.EventBus;
import me.bush.eventbus.handler.handlers.ReflectHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturn implements ClientModInitializer {
    public static Saturn INSTANCE;

    public static final String NAME = "Saturn";
    public static final String VERSION = "1.0-beta";

    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static EventBus EVENT_BUS = new EventBus(ReflectHandler.class, LOGGER::error);

    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private HUDManager hudManager;
    private FriendManager friendManager;

    private InventoryManager inventoryManager;
    private RotationManager rotationManager;
    private InteractionManager interactionManager;
    private ServerManager serverManager;
    private TotemPopManager totemPopManager;

    private ConfigManager configManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        // create our config folder
        if (!FileUtil.exists(FileUtil.CONFIG_FOLDER)) {
            LOGGER.info("Config folder did not exist, creating at {}", FileUtil.CONFIG_FOLDER);
            FileUtil.mkDir(FileUtil.CONFIG_FOLDER);
        }

        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        hudManager = new HUDManager();
        friendManager = new FriendManager();

        inventoryManager = new InventoryManager();
        rotationManager = new RotationManager();
        interactionManager = new InteractionManager();
        serverManager = new ServerManager();
        totemPopManager = new TotemPopManager();

        configManager = new ConfigManager();

        // handle updates
        VersionChecker.handleUpdates();

        // add shutdown hook to save our configs
        Runtime.getRuntime().addShutdownHook(new Thread(() -> configManager.shutdown()));
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

    public FriendManager getFriendManager() {
        return friendManager;
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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static Saturn getInstance() {
        return INSTANCE;
    }
}
