/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.config.Config;
import cope.saturn.core.config.impl.Modules;
import cope.saturn.util.internal.Wrapper;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigManager implements Wrapper {
    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final ArrayList<Config> configs = new ArrayList<>();

    public ConfigManager() {
        configs.add(new Modules(getSaturn().getModuleManager()));

        configs.forEach(Config::load);
        SERVICE.scheduleAtFixedRate(this::saveConfigs, 5L, 5L, TimeUnit.MINUTES);
    }

    /**
     * Shuts down the executor and saves configs
     */
    public void shutdown() {
        SERVICE.shutdownNow();
        saveConfigs();
    }

    /**
     * Handle configurations
     */
    private void saveConfigs() {
        configs.forEach((config) -> {
            Saturn.LOGGER.info("Saving or loading configs...");
            config.save();
        });
    }
}
