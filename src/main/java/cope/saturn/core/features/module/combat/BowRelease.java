/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

public class BowRelease extends Module {
    public BowRelease() {
        super("BowRelease", Category.COMBAT, "Automatically releases your bow");
    }

    public static final Setting<Integer> ticks = new Setting<>("Ticks", 3, 3, 20);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (mc.player.isUsingItem() &&
                mc.player.getActiveItem().getItem() instanceof BowItem &&
                mc.player.getItemUseTime() >= ticks.getValue()) {

            mc.player.stopUsingItem();
            NetworkUtil.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, mc.player.getBlockPos(), mc.player.getHorizontalFacing()));
        }
    }
}
