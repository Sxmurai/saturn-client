/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.asm.mixins.network.packet.s2c.IEntityVelocityUpdateS2CPacket;
import cope.saturn.asm.mixins.network.packet.s2c.IExplosionS2CPacket;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.events.PushOutOfBlocksEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", Category.MOVEMENT, "Ignores server velocity");
    }

    public static final Setting<Float> vertical = new Setting<>("Vertical", 100.0f, 0.0f, 100.0f);
    public static final Setting<Float> horizontal = new Setting<>("Vertical", 100.0f, 0.0f, 100.0f);

    public static final Setting<Boolean> blocks = new Setting<>("Blocks", true);

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getId() != mc.player.getId()) {
                return;
            }

            if (vertical.getValue() == 100.0f && horizontal.getValue() == 100.0f) {
                event.setCancelled(true);
                return;
            }

            ((IEntityVelocityUpdateS2CPacket) packet).setVelocityX(packet.getVelocityX() / horizontal.getValue().intValue());
            ((IEntityVelocityUpdateS2CPacket) packet).setVelocityY(packet.getVelocityY() / vertical.getValue().intValue());
            ((IEntityVelocityUpdateS2CPacket) packet).setVelocityZ(packet.getVelocityZ() / horizontal.getValue().intValue());
        } else if (event.getPacket() instanceof ExplosionS2CPacket packet) {
            if (vertical.getValue() == 100.0f && horizontal.getValue() == 100.0f) {
                event.setCancelled(true);
                return;
            }

            ((IExplosionS2CPacket) packet).setPlayerVelocityX(packet.getPlayerVelocityX() / horizontal.getValue());
            ((IExplosionS2CPacket) packet).setPlayerVelocityY(packet.getPlayerVelocityY() / vertical.getValue());
            ((IExplosionS2CPacket) packet).setPlayerVelocityZ(packet.getPlayerVelocityZ() / horizontal.getValue());
        }
    }

    @EventListener
    public void onPushOutOfBlocks(PushOutOfBlocksEvent event) {
        event.setCancelled(blocks.getValue());
    }
}
