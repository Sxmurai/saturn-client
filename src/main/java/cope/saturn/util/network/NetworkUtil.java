package cope.saturn.util.network;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.network.Packet;

public class NetworkUtil implements Wrapper {
    public static void sendPacket(Packet<?> packet) {
        mc.player.networkHandler.sendPacket(packet);
    }

    public static void sendPacketNoEvent(Packet<?> packet) {

    }
}
