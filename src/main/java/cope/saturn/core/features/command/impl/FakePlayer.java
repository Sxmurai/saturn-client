/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cope.saturn.core.features.command.Command;
import cope.saturn.util.internal.ChatUtil;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.UUID;

public class FakePlayer extends Command {
    private static OtherClientPlayerEntity fakePlayer;

    public FakePlayer() {
        super("fakeplayer", "Spawns a fakeplayer in the world", LiteralArgumentBuilder.literal("fakeplayer")
                .executes((c) -> {
                    if (fakePlayer != null) {
                        mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.KILLED);
                        fakePlayer = null;

                        ChatUtil.send("Despawned fake player.");
                    } else {
                        fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), "FakePlayer"));
                        fakePlayer.copyPositionAndRotation(mc.player);
                        fakePlayer.getInventory().clone(mc.player.getInventory());
                        fakePlayer.setId(-133769420);

                        mc.world.spawnEntity(fakePlayer);
                        mc.world.addEntity(fakePlayer.getId(), fakePlayer);

                        ChatUtil.send("Spawned fake player.");
                    }

                    return 0;
                }));
    }
}
