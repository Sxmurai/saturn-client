/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.network;

import cope.saturn.asm.mixins.network.packet.s2c.IEntityS2CPacket;
import cope.saturn.asm.mixins.network.packet.s2c.IEntityStatusS2CPacket;
import cope.saturn.core.Saturn;
import cope.saturn.core.events.DeathEvent;
import cope.saturn.core.events.TotemPopEvent;
import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    /**
     * Represents the status id for a totem pop
     */
    private static final byte TOTEM_POP_STATUS = 35;

    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    public void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo info) {
        if (packet.getStatus() == TOTEM_POP_STATUS &&
            Wrapper.mc.world.getEntityById(((IEntityStatusS2CPacket) packet).getId()) instanceof PlayerEntity player) {

            Saturn.EVENT_BUS.post(new TotemPopEvent(player));
        }
    }

    @Inject(method = "onEntity", at = @At("HEAD"))
    public void onEntity(EntityS2CPacket packet, CallbackInfo info) {
        if (Wrapper.mc.world.getEntityById(((IEntityS2CPacket) packet).getId()) instanceof PlayerEntity player) {
            if (player.getHealth() <= 0.0f || !player.isAlive()) {
                Saturn.EVENT_BUS.post(new DeathEvent(player));
            }
        }
    }
}
