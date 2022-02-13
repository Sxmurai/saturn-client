/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.network.packet.c2s;

import cope.saturn.asm.duck.IPlayerInteractEntityC2SPacket;
import cope.saturn.util.internal.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Meteor client once again comes to save me
 *
 * I tried using a mixin accessor before, but that would just throw an exception in the built jar outside the dev env
 *
 * Update: im retarded, i forgot to add accessWidener to fabric.mod.json, but this is cleaner so i'll go with this one
 */
@Mixin(PlayerInteractEntityC2SPacket.class)
public class MixinPlayerInteractEntityC2SPacket implements IPlayerInteractEntityC2SPacket {
    @Shadow
    @Final
    private int entityId;

    @Shadow
    @Final
    private PlayerInteractEntityC2SPacket.InteractTypeHandler type;

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getType() {
        return type.getType();
    }

    @Override
    public Entity getEntity() {
        return Wrapper.mc.world.getEntityById(entityId);
    }
}
