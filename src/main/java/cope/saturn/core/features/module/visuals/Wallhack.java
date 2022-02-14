/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class Wallhack extends Module {
    public static Wallhack INSTANCE;

    public static final Set<Block> blocks = new HashSet<>();

    public Wallhack() {
        super("Wallhack", Category.VISUALS, "Allows you to see blocks through walls");
        INSTANCE = this;

        // TODO: add these from a file
        blocks.add(Blocks.NETHER_QUARTZ_ORE);
        blocks.add(Blocks.COAL_ORE);
        blocks.add(Blocks.COPPER_ORE);
        blocks.add(Blocks.IRON_ORE);
        blocks.add(Blocks.GOLD_ORE);
        blocks.add(Blocks.NETHER_GOLD_ORE);
        blocks.add(Blocks.REDSTONE_ORE);
        blocks.add(Blocks.LAPIS_ORE);
        blocks.add(Blocks.EMERALD_ORE);
        blocks.add(Blocks.DIAMOND_ORE);
        blocks.add(Blocks.ANCIENT_DEBRIS);

        blocks.add(Blocks.DEEPSLATE_COAL_ORE);
        blocks.add(Blocks.DEEPSLATE_COPPER_ORE);
        blocks.add(Blocks.DEEPSLATE_IRON_ORE);
        blocks.add(Blocks.DEEPSLATE_GOLD_ORE);
        blocks.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        blocks.add(Blocks.DEEPSLATE_LAPIS_ORE);
        blocks.add(Blocks.DEEPSLATE_EMERALD_ORE);
        blocks.add(Blocks.DEEPSLATE_DIAMOND_ORE);

        blocks.add(Blocks.BEDROCK);
        blocks.add(Blocks.OBSIDIAN);
        blocks.add(Blocks.CRYING_OBSIDIAN);

        blocks.add(Blocks.END_PORTAL);
        blocks.add(Blocks.END_PORTAL_FRAME);
        blocks.add(Blocks.NETHER_PORTAL);

        blocks.add(Blocks.WATER);
        blocks.add(Blocks.LAVA);

        blocks.add(Blocks.CHEST);
        blocks.add(Blocks.ENDER_CHEST);
        blocks.add(Blocks.TRAPPED_CHEST);
        blocks.add(Blocks.BARREL);
        blocks.add(Blocks.ENCHANTING_TABLE);
        blocks.add(Blocks.ANVIL);
        blocks.add(Blocks.CHIPPED_ANVIL);
        blocks.add(Blocks.DAMAGED_ANVIL);

        blocks.add(Blocks.COAL_BLOCK);
        blocks.add(Blocks.BONE_BLOCK);
        blocks.add(Blocks.COPPER_BLOCK);
        blocks.add(Blocks.RAW_COPPER_BLOCK);
        blocks.add(Blocks.WAXED_COPPER_BLOCK);
        blocks.add(Blocks.IRON_BLOCK);
        blocks.add(Blocks.GOLD_BLOCK);
        blocks.add(Blocks.RAW_GOLD_BLOCK);
        blocks.add(Blocks.REDSTONE_BLOCK);
        blocks.add(Blocks.LAPIS_BLOCK);
        blocks.add(Blocks.EMERALD_BLOCK);
        blocks.add(Blocks.DIAMOND_BLOCK);
        blocks.add(Blocks.NETHERITE_BLOCK);
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.BASIC);

    @Override
    protected void onEnable() {
        super.onEnable();

        if (nullCheck()) {
            disable();
            return;
        }

        mc.worldRenderer.reload();
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!nullCheck()) {
            mc.worldRenderer.reload();
        }
    }

    public enum Mode {
        BASIC,
        TRANSPARENCY
    }
}
