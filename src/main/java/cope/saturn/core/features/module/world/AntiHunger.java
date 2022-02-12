/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.asm.mixins.network.packet.c2s.IPlayerMoveC2SPacket;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module {
    public AntiHunger() {
        super("AntiHunger", Category.WORLD, "Stops you from loosing hunger");
    }

    public static final Setting<Boolean> spoofGround = new Setting<>("SpoofGround", true);
    public static final Setting<Boolean> stopSprint = new Setting<>("StopSprint", true);

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (!spoofGround.getValue()) {
                return;
            }

            // setting the onGround state while falling to true would result in a nofall thing, which we dont want
            if (mc.player.fallDistance > 3.0f) {
                return;
            }

            ((IPlayerMoveC2SPacket) packet).setOnGround(true);
        } else if (event.getPacket() instanceof ClientCommandC2SPacket packet) {
            if (packet.getMode().equals(ClientCommandC2SPacket.Mode.START_SPRINTING) && stopSprint.getValue()) {
                event.setCancelled(true);
            }
        }
    }
}
