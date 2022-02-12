/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class RenderWorldEvent extends Event {
    private final MatrixStack stack;
    private final Vec3d cameraPos;

    public RenderWorldEvent(MatrixStack stack, Vec3d cameraPos) {
        this.stack = stack;
        this.cameraPos = cameraPos;
    }

    public MatrixStack getStack() {
        return stack;
    }

    public Vec3d getCameraPos() {
        return cameraPos;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
