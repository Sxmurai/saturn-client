/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.miscellaneous;

import cope.saturn.asm.duck.IEntity;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public class Portal extends Module {
    public Portal() {
        super("Portal", Category.MISCELLANEOUS, "Modifies portal behavior");
    }

    public static final Setting<Boolean> chat = new Setting<>("Chat", true);
    public static final Setting<Boolean> godmode = new Setting<>("GodMode", false);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        ((IEntity) mc.player).setInPortal(!chat.getValue());
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof TeleportConfirmC2SPacket && godmode.getValue()) {
            event.setCancelled(true);
        }
    }
}
