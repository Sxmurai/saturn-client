/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cope.saturn.core.Saturn;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.command.Command;
import cope.saturn.util.internal.ChatUtil;
import cope.saturn.util.internal.Stopwatch;
import cope.saturn.util.world.combat.DamagesUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class FakePlayer extends Command {
    private static OtherClientPlayerEntity fakePlayer;
    private final Stopwatch gappleStopwatch = new Stopwatch();

    public FakePlayer() {
        super("fakeplayer", "Spawns a fakeplayer in the world", LiteralArgumentBuilder.literal("fakeplayer")
                .executes((c) -> {
                    if (fakePlayer != null) {
                        mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.KILLED);
                        fakePlayer = null;

                        ChatUtil.send("Despawned fake player.");
                    } else {
                        fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), "FakePlayer")) {
                            @Override
                            public boolean isDead() {
                                return false;
                            }
                        };

                        fakePlayer.copyPositionAndRotation(mc.player);
                        fakePlayer.getInventory().clone(mc.player.getInventory());
                        fakePlayer.setId(-133769420);

                        fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));

                        mc.world.spawnEntity(fakePlayer);
                        mc.world.addEntity(fakePlayer.getId(), fakePlayer);

                        ChatUtil.send("Spawned fake player.");
                    }

                    return 0;
                }));

        Saturn.EVENT_BUS.subscribe(this);
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (fakePlayer != null) {
            if (fakePlayer.getHealth() <= 0.0f) {
                mc.getNetworkHandler().onEntityStatus(new EntityStatusS2CPacket(fakePlayer, (byte) 35));

                fakePlayer.setHealth(1.0f);
                fakePlayer.setAbsorptionAmount(8.0f);

                fakePlayer.clearActiveItem();
                fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));

                fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof ExplosionS2CPacket packet && fakePlayer != null) {
            float damage = DamagesUtil.crystalDamage(new Vec3d(packet.getX(), packet.getY(), packet.getZ()), fakePlayer);
            fakePlayer.setHealth(fakePlayer.getHealth() - damage);
        }
    }
}
