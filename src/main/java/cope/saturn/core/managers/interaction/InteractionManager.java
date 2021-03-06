/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers.interaction;

import cope.saturn.core.features.module.world.AirPlace;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.entity.player.rotation.RotationUtil;
import cope.saturn.util.internal.Wrapper;
import cope.saturn.util.network.NetworkUtil;
import cope.saturn.util.world.BlockUtil;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

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
        BlockPos neighbor;

        Direction direction = BlockUtil.getDirection(pos);
        if (direction == null) {
            if (AirPlace.INSTANCE.isToggled()) {
                direction = Direction.UP;
                neighbor = pos;
            } else {
                return;
            }
        } else {
            neighbor = pos.offset(direction);
        }

        boolean sneak = BlockUtil.SNEAK_BLOCKS.contains(mc.world.getBlockState(neighbor).getBlock());
        if (sneak) {
            NetworkUtil.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }

        Vec3d vec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ());

        if (rotate) {
            RotationUtil
                    .rotation(vec.add(0.5, 0.5, 0.5))
                    .type(RotationType.PACKET)
                    .send(true);
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

        if (sneak) {
            NetworkUtil.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }

    /**
     * Places an end crystal
     * @param pos The position to place at
     * @param hand The hand to place with
     * @param swing If to swing your hand
     * @param raytrace If to raytrace
     * @param strict If to use StrictDirection
     */
    public void placeCrystal(BlockPos pos, Hand hand, boolean swing, boolean raytrace, boolean strict) {
        Direction direction = Direction.UP;

        if (strict && pos.getY() > mc.player.getY() + mc.player.getStandingEyeHeight()) {
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                    mc.player.getEyePos(),
                    new Vec3d(pos.getX() + 0.5, pos.getY() + (raytrace ? 0.5 : 0.0), pos.getZ() + 0.5),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    mc.player
            ));

            direction = (result == null || result.getSide() == null) ? Direction.DOWN : result.getSide();
        }

        NetworkUtil.sendPacket(new PlayerInteractBlockC2SPacket(hand,
                new BlockHitResult(
                        new Vec3d(pos.getX(), pos.getY(), pos.getZ()),
                        direction,
                        pos,
                        false
                )));

        if (swing) {
            mc.player.swingHand(hand);
        }
    }

    /**
     * Right clicks a block
     * @param pos The pos
     * @param swing if to swing
     * @param rotate if to rotate
     */
    public void rightClick(BlockPos pos, boolean swing, boolean rotate) {
        Direction direction = Direction.UP;

        if (pos.getY() > mc.player.getY() + mc.player.getStandingEyeHeight()) {
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                    mc.player.getEyePos(),
                    new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    mc.player
            ));

            direction = (result == null || result.getSide() == null) ? Direction.DOWN : result.getSide();
        }

        Vec3d posVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

        if (rotate) {
            RotationUtil.rotation(posVec).type(RotationType.PACKET).send(true);
        }

        ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
                new BlockHitResult(posVec, direction, pos, false));

        if (result.isAccepted() && result.shouldSwingHand() && swing) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
