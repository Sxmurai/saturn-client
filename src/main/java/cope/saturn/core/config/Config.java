/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class Config {
    protected final String name;
    protected final Path path;

    protected final Logger LOGGER;

    public Config(String name, Path path) {
        this.name = name;
        this.path = path;

        LOGGER = LoggerFactory.getLogger(name + "_config");
    }

    public abstract void load();

    public abstract void save();

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }
}
