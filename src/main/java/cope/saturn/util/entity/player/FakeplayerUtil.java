/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player;

import com.mojang.authlib.GameProfile;
import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.Map;

public class FakeplayerUtil implements Wrapper {
    // <entity id, fake player instance>
    private static final Map<Integer, OtherClientPlayerEntity> fakeplayers = new HashMap<>();

    /**
     * Spawns a fake player into the world
     * @param id The entities entity id
     * @param profile The gameprofile to use for this player
     * @return the fakeplayer instance
     */
    public static OtherClientPlayerEntity spawn(int id, GameProfile profile) {
        OtherClientPlayerEntity fakePlayer = new OtherClientPlayerEntity(mc.world, profile) {
            @Override
            public boolean isDead() {
                return false;
            }
        };

        fakePlayer.copyPositionAndRotation(mc.player);
        fakePlayer.getInventory().clone(mc.player.getInventory());
        fakePlayer.setId(id);

        fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));

        mc.world.spawnEntity(fakePlayer);
        mc.world.addEntity(fakePlayer.getId(), fakePlayer);

        fakeplayers.put(id, fakePlayer);

        return fakePlayer;
    }

    /**
     * Despawns a fake player
     * @param id the fakeplayer entity id
     */
    public static void despawn(int id) {
        mc.world.removeEntity(id, Entity.RemovalReason.KILLED);
        fakeplayers.remove(id);
    }
}
