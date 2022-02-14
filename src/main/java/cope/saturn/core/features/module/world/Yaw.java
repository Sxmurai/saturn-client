/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;

public class Yaw extends Module {
    public Yaw() {
        super("Yaw", Category.WORLD, "Locks your client yaw");
    }

    public static final Setting<Direction> direction = new Setting<>("Direction", Direction.NORTH);
    public static final Setting<Float> custom = new Setting<>("Custom", 0.0f, 0.0f, 360.0f);

    public static final Setting<Boolean> lockPitch = new Setting<>("Lock Pitch", true);
    public static final Setting<Float> pitch = new Setting<>(lockPitch, "Pitch", 0.0f, -90.0f, 90.0f);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (!direction.getValue().equals(Direction.CUSTOM)) {
            mc.player.setYaw(direction.getValue().ordinal() * 45.0f);
        } else {
            mc.player.setYaw(custom.getValue());
        }

        if (lockPitch.getValue()) {
            mc.player.setPitch(pitch.getValue());
        }
    }

    public enum Direction {
        SOUTH,
        SOUTH_WEST,
        WEST,
        NORTH_WEST,
        NORTH,
        NORTH_EAST,
        EAST,
        SOUTH_EAST,

        CUSTOM
    }
}
