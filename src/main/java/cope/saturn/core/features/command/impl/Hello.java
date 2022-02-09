/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cope.saturn.core.features.command.Command;
import cope.saturn.util.internal.ChatUtil;

public class Hello extends Command {
    public Hello() {
        super("Hello", "says hi to you", LiteralArgumentBuilder.literal("hello")
                .executes((s) -> {
                    ChatUtil.send("Hi :)");
                    return 0;
                }));
    }
}
