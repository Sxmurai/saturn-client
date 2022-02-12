/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class MotionEvent extends Event {
    private final MovementType movementType;
    private final Vec3d vec;

    public MotionEvent(MovementType movementType, Vec3d vec) {
        this.movementType = movementType;
        this.vec = vec;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public Vec3d getVec() {
        return vec;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
