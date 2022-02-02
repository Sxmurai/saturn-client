package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.EntityUtil;
import cope.saturn.util.entity.player.InventoryUtil;
import cope.saturn.util.entity.player.rotation.Rotation;
import cope.saturn.util.entity.player.rotation.RotationType;
import cope.saturn.util.entity.player.rotation.RotationUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public class Aura extends Module {
    public Aura() {
        super("Aura", Category.COMBAT, "Attacks entities around you", GLFW.GLFW_KEY_R);
    }

    public static final Setting<Priority> priority = new Setting<>("Priority", Priority.RANGE);

    public static final Setting<Timing> timing = new Setting<>("Timing", Timing.SEQUENTIAL);

    public static final Setting<Double> range = new Setting<>("Range", 4.5, 1.0, 6.0);
    public static final Setting<Boolean> walls = new Setting<>("Walls", true);
    public static final Setting<Double> wallRange = new Setting<>(range, "WallRange", 3.5, 1.0, 6.0);

    public static final Setting<Rotate> rotate = new Setting<>("Rotate", Rotate.PACKET);
    public static final Setting<Float> rate = new Setting<>(rotate, "Rate", 55.0f, 10.0f, 180.0f);
    public static final Setting<Boolean> jitter = new Setting<>(rotate, "Jitter", true);

    public static final Setting<Boolean> swing = new Setting<>("Swing", true);

    public static final Setting<Boolean> autoBlock = new Setting<>("AutoBlock", true);
    public static final Setting<Boolean> unblock = new Setting<>(autoBlock, "Unblock", true);

    public static final Setting<Weapon> weapon = new Setting<>("Weapon", Weapon.REQUIRE);

    private LivingEntity target = null;

    private int oldSlot = -1;

    private Rotation rotation = new Rotation(RotationType.NONE, Float.NaN, Float.NaN);
    private Rotation progressing = new Rotation(RotationType.NONE, Float.NaN, Float.NaN);

    @Override
    protected void onDisable() {
        super.onDisable();

        resetSlot();
        target = null;
    }

    @EventListener
    public void onTick(ClientTickEvent event) {
        setTarget();
        if (target == null) {
            resetSlot();
            return;
        }

        if (!weapon.getValue().equals(Weapon.NONE)) {
            if (weapon.getValue().equals(Weapon.REQUIRE) && !InventoryUtil.isHolding(SwordItem.class, false)) {
                return;
            }

            int slot = InventoryUtil.getSlot(SwordItem.class, false);
            if (slot == -1) {
                return;
            }

            oldSlot = mc.player.getInventory().selectedSlot;
            getSaturn().getInventoryManager().swap(slot, InventoryManager.Swap.CLIENT);
        }

        if (!rotate.getValue().equals(Rotate.NONE)) {
            Rotation r = RotationUtil.rotation(target.getEyePos());

            if (rotate.getValue().equals(Rotate.LIMIT)) {
                if (!rotation.isValid()) {
                    rotation = r;
                    progressing = r;
                } else {
                    // TODO:
                }
            }

            if (jitter.getValue()) {
                float random = ThreadLocalRandom.current().nextFloat(-0.6f, 0.6f);
                r = r.set(r.yaw() + random, r.pitch() + random);
            }

            rotation = r.type(rotate.getValue().type);
            rotation.send(false);
        }

        boolean canAttack = timing.equals(Timing.VANILLA) ? mc.player.getAttackCooldownProgress(0.0f) == 1.0f :
                target.hurtTime == 0 && mc.player.getAttackCooldownProgress(1.0f) == 1.0f;

        if (!canAttack && autoBlock.getValue() &&
                ShieldItem.class.isInstance(mc.player.getOffHandStack().getItem())) {

            // TODO: this doesnt actually block your shield... fix this
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
        }

        if (canAttack) {
            if (autoBlock.getValue() && mc.player.isBlocking() && unblock.getValue()) {
                mc.player.clearActiveItem();
            }

            mc.interactionManager.attackEntity(mc.player, target);

            if (swing.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private void resetSlot() {
        if (oldSlot != -1) {
            getSaturn().getInventoryManager().swap(oldSlot, InventoryManager.Swap.CLIENT);
            oldSlot = -1;
        }
    }

    private void setTarget() {
        LivingEntity efficientTarget = null;

        for (Entity entity : mc.world.getEntities()) {
            if (entity == null || !(entity instanceof LivingEntity living) || entity.equals(mc.player)) {
                continue;
            }

            boolean canSee = mc.player.canSee(entity);
            if (!walls.getValue() && !canSee) {
                continue;
            }

            double distSq = mc.player.squaredDistanceTo(entity);

            double rangeSq = Math.pow(canSee ? range.getValue() : wallRange.getValue(), 2);
            if (distSq > rangeSq) {
                continue;
            }

            if (efficientTarget == null) {
                efficientTarget = living;
            } else {
                if (priority.getValue().equals(Priority.RANGE)) {
                    if (mc.player.distanceTo(efficientTarget) > distSq) {
                        efficientTarget = living;
                    }
                } else if (priority.getValue().equals(Priority.HEALTH)) {
                    if (EntityUtil.getHealth(efficientTarget) < EntityUtil.getHealth(living)) {
                        efficientTarget = living;
                    }
                }
            }
        }

        target = efficientTarget;
    }

    public enum Rotate {
        NONE(RotationType.NONE),
        CLIENT(RotationType.CLIENT),
        PACKET(RotationType.PACKET),
        LIMIT(RotationType.PACKET);

        private final RotationType type;

        Rotate(RotationType type) {
            this.type = type;
        }
    }

    public enum Weapon {
        NONE,
        SWAP,
        REQUIRE
    }

    public enum Priority {
        RANGE,
        HEALTH
    }

    public enum Timing {
        VANILLA,
        SEQUENTIAL
    }
}
