/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import cope.saturn.core.features.command.Command;
import cope.saturn.core.features.command.arg.ModuleArgumentType;
import cope.saturn.core.features.module.Module;
import cope.saturn.util.internal.ChatUtil;

public class Toggle extends Command {
    public Toggle() {
        super("Toggle", "Toggles a module", LiteralArgumentBuilder.literal("toggle")
                .then(RequiredArgumentBuilder.argument("module", ModuleArgumentType.module())
                        .executes((c) -> {
                            Module module = ModuleArgumentType.getModule(c, "module");
                            module.toggle();

                            ChatUtil.send("Toggled module.");

                            return 0;
                        })));
    }
}
