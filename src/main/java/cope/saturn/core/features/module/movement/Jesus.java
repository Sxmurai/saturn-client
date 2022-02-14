/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.asm.duck.IVec3d;
import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.FluidCollisionShapeEvent;
import cope.saturn.core.events.SendMovementPacketsEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;

import java.util.concurrent.ThreadLocalRandom;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", Category.MOVEMENT, "Makes you walk on water");
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.SOLID);
    public static final Setting<Boolean> lava = new Setting<>("Lava", true);

    private double packetOffset = 0.0;

    private int strictMovementTicks = 0;
    private boolean shouldSlowdown = false;

    @Override
    protected void onDisable() {
        super.onDisable();

        packetOffset = 0.0;
        strictMovementTicks = 0;
        shouldSlowdown = false;
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (isAboveLiquid()) {
            switch (mode.getValue()) {
                case STRICT -> {
                    ++strictMovementTicks;
                    if (shouldSlowdown) {
                        if (strictMovementTicks >= 50) {
                            shouldSlowdown = false;
                            strictMovementTicks = 0;
                            return;
                        }

                        if (mc.player.isSprinting()) {
                            NetworkUtil.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                        }

                        ((IVec3d) mc.player.getVelocity()).setX(mc.player.getVelocity().x * 0.57);
                        ((IVec3d) mc.player.getVelocity()).setZ(mc.player.getVelocity().z * 0.57);
                    } else {
                        if (strictMovementTicks >= 20) {
                            strictMovementTicks = 0;
                            shouldSlowdown = true;
                        }
                    }
                }

                case DOLPHIN -> mc.player.jump();
            }
        }
    }

    @EventListener
    public void onFluidCollisionShape(FluidCollisionShapeEvent event) {
        if (mode.getValue().equals(Mode.DOLPHIN)) {
            return;
        }

        if (event.getState().getBlock().equals(Blocks.LAVA) && !lava.getValue()) {
            return;
        }

        event.setShape(VoxelShapes.fullCube());
        event.setCancelled(true);
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onSendMovementPackets(SendMovementPacketsEvent e) {
        if (e instanceof SendMovementPacketsEvent.Pre event) {
            if (isAboveLiquid() && (mode.getValue().equals(Mode.SOLID) || mode.getValue().equals(Mode.STRICT))) {
                double yOffset = 0.0;

                if (mc.player.age % 2 == 0) {
                    if (mode.getValue().equals(Mode.SOLID)) {
                        yOffset = 0.2;
                    } else if (mode.getValue().equals(Mode.STRICT)) {
                        yOffset = handlePacketOffsetStrict();
                    }
                }

                event.setOnGround(false);
                event.setY(event.getY() - yOffset);

                event.setCancelled(true);
            }
        }
    }

    private double handlePacketOffsetStrict() {
        packetOffset += ThreadLocalRandom.current().nextDouble(0.0, 0.1024);
        if (packetOffset >= 0.2) {
            packetOffset = 0.0;
        }

        return packetOffset;
    }

    public static boolean isAboveLiquid() {
        return mc.world.getBlockState(new BlockPos(mc.player.getX(), mc.player.getY() - 1.0, mc.player.getZ())).getBlock() instanceof FluidBlock;
    }

    public enum Mode {
        /**
         * Normal bypassing NCP solid jesus
         */
        SOLID,

        /**
         * Holds the space bar for you
         */
        DOLPHIN,

        /**
         * Strict jesus working on NCP updated
         *
         * Slows you down every ~10 ticks and more random y dip values
         */
        STRICT
    }
}
