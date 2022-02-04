/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player.rotation;

import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.network.NetworkUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public record Rotation(RotationType type, float yaw, float pitch) implements Wrapper {
    public Rotation set(float yaw, float pitch) {
        return new Rotation(type, yaw, pitch);
    }

    public Rotation type(RotationType type) {
        return new Rotation(type, yaw, pitch);
    }

    public void send(boolean sendPacket) {
        if (type.equals(RotationType.NONE)) {
            return;
        }

        if (sendPacket) {
            NetworkUtil.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, Wrapper.mc.player.isOnGround()));
        }

        switch (type) {
            case CLIENT -> {
                Wrapper.mc.player.setYaw(yaw);
                Wrapper.mc.player.setPitch(pitch);
            }

            case PACKET -> getSaturn().getRotationManager().rotate(this);
        }
    }

    public boolean isValid() {
        return !Float.isNaN(yaw) && !Float.isNaN(pitch);
    }
}
