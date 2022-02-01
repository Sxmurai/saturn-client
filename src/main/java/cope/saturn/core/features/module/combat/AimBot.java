package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.player.rotation.RotationType;
import cope.saturn.util.player.rotation.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

public class AimBot extends Module {
    public AimBot() {
        super("AimBot", Category.COMBAT, "Aims you clientside to the closest entity", GLFW.GLFW_KEY_B);
    }

    public static final Setting<Double> range = new Setting<>("Range", 4.5, 1.0, 6.0);
    public static final Setting<Integer> ticksExisted = new Setting<>("TicksExisted", 20, 0, 100);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        // get the closest entity
        LivingEntity target = null;

        // TODO: mc.world.getEntities(LivingEntity.class) wouldnt work when using the lambda param (the type)
        for (Entity entity : mc.world.getEntities()) {
            if (entity == null || !(entity instanceof LivingEntity) || entity.equals(mc.player)) {
                continue;
            }

            if (entity.age < ticksExisted.getValue()) {
                continue;
            }

            double dist = mc.player.squaredDistanceTo(entity);
            if (dist > range.getValue() * range.getValue()) {
                continue;
            }

            if (target == null) {
                target = (LivingEntity) entity;
            } else {
                if (mc.player.squaredDistanceTo(target) > dist) {
                    target = (LivingEntity) entity;
                }
            }
        }

        if (target == null) {
            return;
        }

        RotationUtil.rotation(target.getEyePos()).type(RotationType.CLIENT).send(false);
    }
}
