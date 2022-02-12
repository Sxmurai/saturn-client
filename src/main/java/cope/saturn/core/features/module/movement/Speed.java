/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.asm.duck.IVec3d;
import cope.saturn.asm.mixins.client.IMinecraftClient;
import cope.saturn.asm.mixins.client.IRenderTickCounter;
import cope.saturn.core.events.MotionEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.events.SendMovementPacketsEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.MotionUtil;
import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    public Speed() {
        super("Speed", Category.MOVEMENT, "Makes you move faster");
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.NCPHOP);
    public static final Setting<Boolean> timer = new Setting<>("Timer", true);

    private double distanceTraveled = 0.0;
    private double moveSpeed = 0.0;
    private int stage = 1;

    private boolean useTimer = false;
    private int timerTicks = 0;

    @Override
    protected void onDisable() {
        super.onDisable();

        distanceTraveled = 0.0;
        moveSpeed = 0.0;
        stage = 4;

        timerTicks = 0;
        if (useTimer && !nullCheck()) {
            useTimer = false;

            ((IRenderTickCounter) ((IMinecraftClient) mc).getRenderTickCounter()).setTickTime(50.0f);
        }
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onMotionUpdatePre(SendMovementPacketsEvent.Pre event) {
        distanceTraveled = Math.sqrt(Math.pow(mc.player.prevX - mc.player.getX(), 2) + Math.pow(mc.player.prevZ - mc.player.getZ(), 2));
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (mode.getValue().equals(Mode.NCPHOP) || mode.getValue().equals(Mode.STRICTHOP)) {
            if (mc.player.isOnGround() && MotionUtil.isMoving()) {
                stage = 2;
            }

            if (timer.getValue()) {
                if (useTimer) {
                    ((IRenderTickCounter) ((IMinecraftClient) mc).getRenderTickCounter()).setTickTime(50.0f / 1.1f);

                    Vec3d velocity = mc.player.getVelocity();
                    ((IVec3d) mc.player.getVelocity()).setX(velocity.x * 1.02);
                    ((IVec3d) mc.player.getVelocity()).setZ(velocity.z * 1.02);

                    ++timerTicks;

                    if (timerTicks >= 15) {
                        timerTicks = 0;
                        useTimer = false;

                        ((IRenderTickCounter) ((IMinecraftClient) mc).getRenderTickCounter()).setTickTime(50.0f);
                    }
                } else {
                    ++timerTicks;
                    if (timerTicks >= 10) {
                        useTimer = true;
                        timerTicks = 0;
                    }
                }
            } else {
                if (useTimer) {
                    timerTicks = 0;
                    useTimer = false;

                    ((IRenderTickCounter) ((IMinecraftClient) mc).getRenderTickCounter()).setTickTime(50.0f);
                }
            }

            if (stage == 1) {
                moveSpeed = (1.38 * MotionUtil.getBaseNCPSpeed()) - 0.1;
                stage = 2;
            } else if (stage == 2) {
                if (mc.player.isOnGround() && MotionUtil.isMoving()) {
                    ((IVec3d) event.getVec()).setY(MotionUtil.getJumpHeight());
                    ((IVec3d) mc.player.getVelocity()).setY(MotionUtil.getJumpHeight());

                    moveSpeed *= 2.149;
                }

                stage = 3;
            } else if (stage == 3) {
                double difference = 0.66 * (distanceTraveled - MotionUtil.getBaseNCPSpeed());
                moveSpeed = distanceTraveled - difference;
                stage = 4;
            } else if (stage == 4) {
                if (mc.player.horizontalCollision) {
                    stage = 1;
                }

                moveSpeed = distanceTraveled - distanceTraveled / 149.0;
            }

            double maxSpeed = mode.getValue().equals(Mode.NCPHOP) ? (mc.player.age % 2 == 0 ? 0.535 : 0.503) : 0.399999999998;
            moveSpeed = MathHelper.clamp(moveSpeed, MotionUtil.getBaseNCPSpeed(), maxSpeed);

            double[] motion = MotionUtil.strafe(moveSpeed);

            ((IVec3d) event.getVec()).setX(motion[0]);
            ((IVec3d) event.getVec()).setZ(motion[1]);

            if (!MotionUtil.isMoving()) {
                ((IVec3d) event.getVec()).setX(0.0);
                ((IVec3d) event.getVec()).setZ(0.0);
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            moveSpeed = MotionUtil.getBaseNCPSpeed();
            distanceTraveled = 0.0;
            stage = 4;
        }
    }

    public enum Mode {
        /**
         * Works on normal NCP servers
         */
        NCPHOP,

        /**
         * Meant for servers with an updated NCP anticheat
         */
        STRICTHOP
    }
}
