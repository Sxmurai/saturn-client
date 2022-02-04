/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.asm.mixins.network.packet.c2s.IPlayerMoveC2SPacket;
import cope.saturn.core.Saturn;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.events.SendMovementPacketsEvent;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.entity.player.rotation.Rotation;
import cope.saturn.util.entity.player.rotation.RotationType;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Manages rotations sent to the server
 *
 * Also used for showing rotations client-sided.
 */
public class RotationManager implements Wrapper {
    private Rotation rotation = new Rotation(RotationType.PACKET, Float.NaN, Float.NaN);
    private final Stopwatch stopwatch = new Stopwatch();

    public RotationManager() {
        Saturn.EVENT_BUS.subscribe(this);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = event.getPacket();

            // if the packet being sent contains rotation updates, we can override these with our rotations set here
            // of course, we only want to override these values if the rotation is valid
            if (packet.changesLook() && rotation.isValid()) {
                ((IPlayerMoveC2SPacket) packet).setYaw(rotation.yaw());
                ((IPlayerMoveC2SPacket) packet).setPitch(rotation.pitch());
            }
        }
    }

    @EventListener
    public void onSendMovementPackets(SendMovementPacketsEvent.Pre event) {
        if (!nullCheck() && rotation.isValid()) {
            if (stopwatch.passedMs(350L)) {
                reset();
                return;
            }

            if (rotation.type().equals(RotationType.CLIENT)) {
                mc.player.setYaw(rotation.yaw());
                mc.player.setPitch(rotation.pitch());
            } else if (rotation.type().equals(RotationType.PACKET)) {
                event.setYaw(rotation.yaw());
                event.setPitch(rotation.pitch());
                event.setCancelled(true);
            }
        }
    }

    public void reset() {
        rotation = rotation.set(Float.NaN, Float.NaN);
    }

    public void rotate(float yaw, float pitch) {
        rotate(new Rotation(RotationType.PACKET, yaw, pitch));
    }

    public void rotate(Rotation r) {
        stopwatch.reset();
        rotation = r;
    }

    public Rotation getRotation() {
        return rotation;
    }
}
