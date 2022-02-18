/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.asm.mixins.client.IMinecraftClient;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", Category.WORLD, "Places things faster");
    }

    public static final Setting<Integer> speed = new Setting<>("Speed", 4, 1, 4);

    public static final Setting<Object> items = new Setting<>("Items", null);
    public static final Setting<Boolean> blocks = new Setting<>(items, "Blocks", false);
    public static final Setting<Boolean> crystals = new Setting<>(items, "Crystals", false);
    public static final Setting<Boolean> exp = new Setting<>(items, "Exp", true);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if ((blocks.getValue() && InventoryUtil.isHolding(BlockItem.class, true)) ||
                (crystals.getValue() && InventoryUtil.isHolding(Items.END_CRYSTAL, true)) ||
                (exp.getValue() && InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE, true))) {

            ((IMinecraftClient) mc).setItemUseCooldown(4 - speed.getValue());
        }
    }
}
