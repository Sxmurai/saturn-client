/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player.inventory.task;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Represents an inventory task
 *
 * Used for splitting up inventory interactions easier.
 */
public record Task(SlotActionType type, int slot, boolean update) implements Wrapper {
    /**
     * Executes the task using the type in the constructor
     */
    public void execute() {
        execute(type);
    }

    /**
     * Executes this task
     * @param type The SlotActionType enum
     */
    public void execute(SlotActionType type) {
        // sends a window click packet
        mc.interactionManager.clickSlot(0, slot, 0, type, mc.player);

        // updates the controller
        if (update) {
            mc.interactionManager.tick();
        }
    }
}
