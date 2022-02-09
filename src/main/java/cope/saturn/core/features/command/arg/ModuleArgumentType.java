/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cope.saturn.core.Saturn;
import cope.saturn.core.features.module.Module;

public class ModuleArgumentType implements ArgumentType<Module> {
    public static ModuleArgumentType module() {
        return new ModuleArgumentType();
    }

    public static Module getModule(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Module.class);
    }

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        String text = reader.readString();

        for (Module module : Saturn.getInstance().getModuleManager().getModules()) {
            if (module.getName().equalsIgnoreCase(text)) {
                return module;
            }
        }

        return null;
    }
}
