/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiVoid extends Module {
    public AntiVoid() {
        super("AntiVoid", Category.MOVEMENT, "Stops you from falling into the void");
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.SUSPEND);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (mc.player.getY() <= mc.world.getBottomY()) {
            switch (mode.getValue()) {
                case SUSPEND -> mc.player.setPosition(mc.player.getX(), mc.world.getBottomY(), mc.player.getZ());
                case JUMP -> mc.player.jump();
                case SETBACK -> NetworkUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() + 5.0, mc.player.getZ(), false));
            }
        }
    }

    public enum Mode {
        /**
         * Sets the y position to the bottom y value
         *
         * Setting the vertical velocity did not seem to work, as it slowly put you into the void
         *
         * Since velocity is just added onto the y pos for movement packets, we can do this
         */
        SUSPEND,

        /**
         * Calls mc.player.jump()
         */
        JUMP,

        /**
         * Sends an unnatural packet to flag the anticheat and set you back to your last onground state
         *
         * Should only be used on servers with an anticheat (like NCP)
         */
        SETBACK
    }
}
