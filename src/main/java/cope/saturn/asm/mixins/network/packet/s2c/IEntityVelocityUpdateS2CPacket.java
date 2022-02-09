/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.network.packet.s2c;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface IEntityVelocityUpdateS2CPacket {
    @Accessor("velocityX")
    @Mutable
    void setVelocityX(int velocityX);

    @Accessor("velocityY")
    @Mutable
    void setVelocityY(int velocityY);

    @Accessor("velocityZ")
    @Mutable
    void setVelocityZ(int velocityZ);
}
