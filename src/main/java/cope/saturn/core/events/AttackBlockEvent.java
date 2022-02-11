/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AttackBlockEvent extends Event {
    private final BlockPos pos;
    private final Direction direction;

    public AttackBlockEvent(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
