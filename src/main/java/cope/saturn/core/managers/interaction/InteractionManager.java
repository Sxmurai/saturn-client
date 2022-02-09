/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers.interaction;

import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.entity.player.rotation.RotationUtil;
import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.network.NetworkUtil;
import cope.saturn.util.world.BlockUtil;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Handles interactions in the world (eg opening containers, placing blocks, etc)
 */
public class InteractionManager implements Wrapper {
    /**
     * Places a block
     * @param pos The block position
     * @param type The placement type
     * @param rotate If to rotate
     */
    public void place(BlockPos pos, PlaceType type, Hand hand, boolean rotate) {
        Direction direction = BlockUtil.getDirection(pos);
        if (direction == null) {
            return;
        }

        BlockPos neighbor = pos.offset(direction);
        Vec3d vec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ());

        if (rotate) {
            RotationUtil
                    .rotation(vec.add(0.5, 0.5, 0.5))
                    .type(RotationType.PACKET)
                    .send(false);
        }

        Vec3d hitVec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ())
                .add(0.5, 0.5, 0.5)
                .add(new Vec3d(direction.getOpposite().getUnitVector()).multiply(0.5));

        BlockHitResult result = new BlockHitResult(hitVec, direction.getOpposite(), neighbor, false);

        if (type.equals(PlaceType.CLIENT)) {
            mc.interactionManager.interactBlock(mc.player, mc.world, hand, result);
        } else {
            NetworkUtil.sendPacket(new PlayerInteractBlockC2SPacket(hand, result));
        }

        mc.player.swingHand(hand);
    }
}
