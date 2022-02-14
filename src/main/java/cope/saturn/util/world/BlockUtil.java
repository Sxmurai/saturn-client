/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.world;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockUtil implements Wrapper {
    /**
     * Represents blocks that need to have a sneak packet sent along with it
     *
     * I actually died to an iron golem while scaffolding upwards because this wasn't implemented, so I just remembered to do this.
     */
    public static final Set<Block> SNEAK_BLOCKS = Set.of(
            Blocks.CRAFTING_TABLE,
            Blocks.FURNACE,
            Blocks.BLAST_FURNACE,
            Blocks.FLETCHING_TABLE,
            Blocks.CARTOGRAPHY_TABLE,
            Blocks.ENCHANTING_TABLE,
            Blocks.SMITHING_TABLE,
            Blocks.CHEST,
            Blocks.ENDER_CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.ANVIL,
            Blocks.CHIPPED_ANVIL,
            Blocks.DAMAGED_ANVIL,
            Blocks.JUKEBOX,
            Blocks.NOTE_BLOCK,
            Blocks.STONECUTTER
    );

    /**
     * Gets a sphere of blocks around a point of origin
     * @param origin The origin point
     * @param radius The radius
     * @return a list of BlockPos's that are surrounding the origin in a sphere
     */
    public static List<BlockPos> sphere(BlockPos origin, int radius) {
        ArrayList<BlockPos> positions = new ArrayList<>();

        for (double x = -radius; x <= radius; ++x) {
            for (double y = -radius; y <= radius; ++y) {
                for (double z = -radius; z <= radius; ++z) {
                    BlockPos pos = origin.add(x, y, z);

                    if (mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5) <= radius * radius) {
                        positions.add(pos);
                    }
                }
            }
        }

        return positions;
    }

    /**
     * Checks if the bounding box at this position is empty
     * @param pos The position
     * @return if no entities are intersecting with the bounding box
     */
    public static boolean areBoxesEmpty(BlockPos pos) {
        return mc.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos)).isEmpty();
    }

    /**
     * Checks if you can replace the block at this position
     * @param pos The block position
     * @return if it is replaceable
     */
    public static boolean isReplaceable(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    /**
     * Get the direction a block can be placed at
     * @param pos The position
     * @return a Direction enum or null
     */
    public static Direction getDirection(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            if (!isReplaceable(neighbor) && areBoxesEmpty(neighbor)) {
                return direction;
            }
        }

        return null;
    }
}
