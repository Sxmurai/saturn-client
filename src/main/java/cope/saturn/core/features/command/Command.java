/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cope.saturn.util.internal.Wrapper;

public class Command implements Wrapper {
    private final String name, description;
    private final LiteralArgumentBuilder<Object> builder;

    public Command(String name, String description, LiteralArgumentBuilder<Object> builder) {
        this.name = name;
        this.description = description;
        this.builder = builder;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LiteralArgumentBuilder<Object> getBuilder() {
        return builder;
    }
}
