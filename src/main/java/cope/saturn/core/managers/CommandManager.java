/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cope.saturn.core.Saturn;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.command.Command;
import cope.saturn.core.features.command.impl.FakePlayer;
import cope.saturn.core.features.command.impl.Hello;
import cope.saturn.core.features.command.impl.Toggle;
import cope.saturn.util.internal.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.ArrayList;

public class CommandManager {
    private final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
    private final ArrayList<Command> commands = new ArrayList<>();

    private String prefix = "&";

    public CommandManager() {
        Saturn.EVENT_BUS.subscribe(this);

        commands.add(new FakePlayer());
        commands.add(new Hello());
        commands.add(new Toggle());

        // register all commands to the dispatcher
        commands.forEach((command) -> dispatcher.register(command.getBuilder()));
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket packet) {
            String message = packet.getChatMessage();

            if (message.startsWith(prefix)) {
                event.setCancelled(true);

                try {
                    dispatcher.execute(dispatcher.parse(message.substring(prefix.length()), 1));
                } catch (CommandSyntaxException e) {
                    ChatUtil.send("An exception occurred. Please run the help command.");
                }
            }
        }
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
