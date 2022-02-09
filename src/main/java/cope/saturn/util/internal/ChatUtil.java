/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.internal;

import net.minecraft.text.LiteralText;

public class ChatUtil implements Wrapper {
    public static final String CHAT_PREFIX = "\u00A7c[Saturn]\u00A7r ";

    /**
     * Sends a message to the local player
     * @param text The text to send
     */
    public static void send(String text) {
        mc.inGameHud.getChatHud().addMessage(new LiteralText(CHAT_PREFIX + text));
    }
}
