/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.network.packet.s2c;

import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityStatusS2CPacket.class)
public interface IEntityStatusS2CPacket {
    @Accessor("id")
    @Final
    int getId();
}
