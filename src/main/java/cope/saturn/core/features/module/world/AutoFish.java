/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.world;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.entity.player.rotation.Rotation;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.internal.Stopwatch;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.FishingRodItem;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.concurrent.ThreadLocalRandom;

public class AutoFish extends Module {
    public AutoFish() {
        super("AutoFish", Category.WORLD, "Automatically fishes for you");
    }

    public static final Setting<Float> delay = new Setting<>("Delay", 50.0f, 0.0f, 500.0f);
    public static final Setting<Boolean> stopMotion = new Setting<>("StopMotion", true);

    private final Stopwatch stopwatch = new Stopwatch();
    private State state = State.IDLE;
    private long randomDelay = 0L;
    private Rotation rotation = null;

    @Override
    protected void onDisable() {
        super.onDisable();

        state = State.IDLE;
        randomDelay = 0L;
        rotation = null;
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        switch (state) {
            case WAITING -> {
                if (stopMotion.getValue()) {
                    mc.player.setVelocity(0.0, mc.player.getVelocity().getY(), 0.0);
                }

                if (rotation == null) {
                    rotation = new Rotation(RotationType.PACKET, mc.player.getYaw(), mc.player.getPitch());
                }

                // keep rotations on the place we put the fishing hook in
                getSaturn().getRotationManager().rotate(rotation);
            }

            case CAST -> {
                if (stopwatch.passedMs(delay.getValue().intValue() + randomDelay)) {
                    castRod();
                    state = State.WAITING;
                }
            }

            case REAL_IN -> {
                if (stopwatch.passedMs(delay.getValue().intValue() + randomDelay)) {
                    castRod();

                    stopwatch.reset();
                    randomDelay = ThreadLocalRandom.current().nextLong(0, 50);
                    state = State.CAST;
                }
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            SoundEvent soundEvent = packet.getSound();

            if (!InventoryUtil.isHolding(FishingRodItem.class, false)) {
                return;
            }

            if (soundEvent.equals(SoundEvents.ENTITY_FISHING_BOBBER_THROW)) {
                state = State.WAITING;
            } else {

                // if we got a fish
                if (soundEvent.equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
                    state = State.REAL_IN;
                    randomDelay = ThreadLocalRandom.current().nextLong(0, 50);
                    stopwatch.reset();
                }
            }
        }
    }

    private void castRod() {
        if (InventoryUtil.isHolding(FishingRodItem.class, false)) {
            ActionResult result = mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            if (result.isAccepted()) {
                if (result.shouldSwingHand()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }

                mc.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.MAIN_HAND);

                state = State.WAITING;
            }
        }
    }

    public enum State {
        IDLE,
        WAITING,
        REAL_IN,
        CAST
    }
}
