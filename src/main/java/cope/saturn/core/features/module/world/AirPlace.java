/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.interaction.PlaceType;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.world.BlockUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class AirPlace extends Module {
    public static AirPlace INSTANCE;

    public AirPlace() {
        super("AirPlace", Category.WORLD, "Allows you to place blocks mid-air on newer servers");
        INSTANCE = this;
    }

    public static final Setting<Double> range = new Setting<>("Range", 4.0, 1.0, 6.0);
    public static final Setting<Boolean> rotate = new Setting<>("Rotate", false);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (mc.cameraEntity != null && mc.options.keyUse.isPressed() && InventoryUtil.isHolding(BlockItem.class, false)) {
            HitResult result = mc.cameraEntity.raycast(range.getValue(), 0.0f, false);
            if (result instanceof BlockHitResult blockHitResult) {
                BlockPos pos = blockHitResult.getBlockPos();

                // if theres no valid faces to place on, we can airplace
                if (BlockUtil.getDirection(pos) == null) {
                    getSaturn().getInteractionManager().place(pos, PlaceType.CLIENT, Hand.MAIN_HAND, rotate.getValue());
                }
            }
        }
    }
}
