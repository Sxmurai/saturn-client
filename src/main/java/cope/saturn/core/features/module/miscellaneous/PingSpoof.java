/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.miscellaneous;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PingSpoof extends Module {
    public PingSpoof() {
        super("PingSpoof", Category.MISCELLANEOUS, "Spoofs your ping");
    }

    public static final Setting<Integer> delay = new Setting<>("Delay", 500, 100, 5000);

    private final Stopwatch stopwatch = new Stopwatch();
    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!nullCheck()) {
            sendAll();
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof KeepAliveC2SPacket) {
            packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (stopwatch.passedMs(delay.getValue().longValue())) {
            stopwatch.reset();
            sendAll();
        }
    }

    private void sendAll() {
        while (!packets.isEmpty()) {
            Packet<?> packet = packets.poll();
            if (packet == null) {
                break;
            }

            NetworkUtil.sendPacketNoEvent(packet);
        }
    }
}
