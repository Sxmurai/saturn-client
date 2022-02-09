/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.world;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class BlockUtil implements Wrapper {
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
