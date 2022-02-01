package cope.saturn.core.features.module.combat;

import cope.saturn.asm.mixins.network.packet.c2s.IPlayerInteractEntityC2SPacket;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.network.NetworkUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", Category.COMBAT, "Makes you deal critical hits", GLFW.GLFW_KEY_C);
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.PACKET);

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {
            // fuck you mojang
            if (((IPlayerInteractEntityC2SPacket) packet).getType().getType().equals(PlayerInteractEntityC2SPacket.InteractType.ATTACK)) {
                if (!mc.player.isOnGround()) {
                    return;
                }

                switch (mode.getValue()) {
                    case PACKET -> NetworkUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX(), mc.player.getY() + 0.2, mc.player.getZ(), false));
                    case JUMP -> mc.player.jump();
                    case MINIJUMP -> {
                        Vec3d velocity = mc.player.getVelocity();
                        mc.player.setVelocityClient(velocity.x, velocity.z + 0.2, velocity.z);
                    }
                }

                // return to original position
                NetworkUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
            }
        }
    }

    public enum Mode {
        /**
         * Sends a packet offset 0.2 in the air
         */
        PACKET,

        /**
         * Jumps upwards
         */
        JUMP,

        /**
         * Sets your player y velocity to 0.2
         */
        MINIJUMP
    }
}
