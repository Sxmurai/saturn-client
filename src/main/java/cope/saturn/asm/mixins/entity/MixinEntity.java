/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.entity;

import cope.saturn.asm.duck.IEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class MixinEntity implements IEntity {
    @Shadow
    protected boolean inNetherPortal;

    @Override
    public boolean isInPortal() {
        return inNetherPortal;
    }

    @Override
    public void setInPortal(boolean inPortal) {
        inNetherPortal = inPortal;
    }
}
