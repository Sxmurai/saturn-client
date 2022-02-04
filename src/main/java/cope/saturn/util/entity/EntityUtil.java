/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity;

import net.minecraft.entity.LivingEntity;

public class EntityUtil {
    /**
     * Gets the total health value of a living entity.
     *
     * Accounts for the absorption amount gotten from golden apples or the effects after a totem pop.
     *
     * @param entity The living entity
     * @return the total health value of the entity
     */
    public static float getHealth(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }
}
