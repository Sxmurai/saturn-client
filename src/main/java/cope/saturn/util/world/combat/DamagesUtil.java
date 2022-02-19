/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.world.combat;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

/**
 * Calculates damage from different things from the world
 */
public class DamagesUtil implements Wrapper {
    /**
     * Calculates crystal damage
     * @param vec The position of the damage
     * @param target The target of the damage
     * @return the damage the target took from the crystal explosion
     */
    public static float crystalDamage(Vec3d vec, LivingEntity target) {
        return explosionDamage(vec, target, 6.0, true, false);
    }

    /**
     * Calculates crystal damage
     * @param vec The position of the damage
     * @param target The target of the damage
     * @return the damage the target took from the bed explosion
     */
    public static float bedDamage(Vec3d vec, LivingEntity target) {
        return explosionDamage(vec, target, 5.0, true, true);
    }

    /**
     * Calculates the damage taken from an explosion
     * @param vec The position of the damage
     * @param target The target of the damage
     * @param power The power of the explosion
     * @param damagesTerrain If the explosion damages terrain
     * @param causesFire If the explosion sets the surrounding terrain on fire
     * @return the damage from the explosion to the target
     */
    public static float explosionDamage(Vec3d vec, LivingEntity target, double power, boolean damagesTerrain, boolean causesFire) {
        if (target.equals(mc.player) && mc.player.isCreative()) {
            return 0.0f;
        }

        double doublePower = power * 2;

        double distanced = target.squaredDistanceTo(vec) / doublePower;
        float density = Explosion.getExposure(vec, target);

        double impact = (1.0 - distanced) * density;

        float damage = (float) ((impact * impact + impact) / 2.0f * 7.0f * doublePower + 1.0);

        return blastReduction(target, difficultyMultiplier(damage),
                new Explosion(target.world, target, vec.x, vec.y, vec.z, (float) power, causesFire,
                        damagesTerrain ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE));
    }

    /**
     * Calculates the reductions from the targets armor and status effects
     * @param target The target
     * @param damage The pre-calculated damage
     * @param explosion The explosion object
     * @return the reduced damage
     */
    public static float blastReduction(LivingEntity target, float damage, Explosion explosion) {
        DamageSource source = DamageSource.explosion(explosion);
        damage = DamageUtil.getDamageLeft(damage, (float) target.getArmor(), (float) target.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));

        float eof = MathHelper.clamp(EnchantmentHelper.getProtectionAmount(target.getArmorItems(), source),
                0.0f, 20.0f);

        damage *= 1.0f - eof / 25.0f;

        if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
            damage -= damage / 4.0f;
        }

        return damage;
    }

    /**
     * Multiplies the damage by the difficulty
     * @param damage The damage
     * @return The damage * (diff id * 0.5)
     */
    public static float difficultyMultiplier(float damage) {
        return damage * (mc.world.getDifficulty().getId() * 0.5f);
    }
}
