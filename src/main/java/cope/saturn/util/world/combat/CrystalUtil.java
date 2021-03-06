/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.world.combat;

import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CrystalUtil implements Wrapper {
    /**
     * Checks if we can place a crystal at this position
     * @param pos The position
     * @param placement The crystal placement mode
     * @return if we can place the crystal there
     */
    public static boolean canPlace(BlockPos pos, Placement placement) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block.equals(Blocks.OBSIDIAN) || block.equals(Blocks.BEDROCK))) {
            return false;
        }

        BlockPos boost = pos.add(0, 1, 0);
        BlockPos nextBoost = pos.add(0, 2, 0);

        if ((placement.equals(Placement.OLD) && !mc.world.isAir(nextBoost)) || !mc.world.isAir(boost)) {
            return false;
        }

        if (!mc.world.getOtherEntities(null, new Box(boost), (e) -> e != null || !e.isAlive()).isEmpty()) {
            return false;
        }

        if (placement.equals(Placement.OLD) &&
                mc.world.getOtherEntities(null, new Box(nextBoost), (e) -> e != null || !e.isAlive()).isEmpty()) {
            return false;
        }

        return true;
    }

    public enum Placement {
        UPDATED,
        OLD
    }
}
