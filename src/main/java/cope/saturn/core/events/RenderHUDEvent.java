/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.events;

import me.bush.eventbus.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderHUDEvent extends Event {
    private final MatrixStack stack;

    public RenderHUDEvent(MatrixStack stack) {
        this.stack = stack;
    }

    public MatrixStack getStack() {
        return stack;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
