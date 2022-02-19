/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class InventoryManager implements Wrapper {
    private int serverSlot = 0;

    public InventoryManager() {
        Saturn.EVENT_BUS.subscribe(this);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
            serverSlot = packet.getSelectedSlot();
        }
    }

    /**
     * Swaps to a hotbar slot
     * @param slot The slot to swap to in the hotbar
     * @param swap The swap type
     */
    public void swap(int slot, Swap swap) {
        if (slot < 0 || slot > 9) {
            Saturn.LOGGER.warn("Tried to swap to slot {} (out of bounds 0-9)", slot);
            return;
        }

        if (swap.equals(Swap.NONE)) {
            return;
        }

        if (swap.equals(Swap.CLIENT)) {
            mc.player.getInventory().selectedSlot = slot;
        }

        NetworkUtil.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

        mc.interactionManager.tick();
    }

    public boolean isSynced() {
        return mc.player.getInventory().selectedSlot == serverSlot;
    }

    public int getServerSlot() {
        return serverSlot;
    }

    public enum Swap {
        NONE,

        CLIENT,

        PACKET
    }
}
