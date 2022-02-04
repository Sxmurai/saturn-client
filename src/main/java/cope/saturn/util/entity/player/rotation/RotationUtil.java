/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player.rotation;

import cope.saturn.util.internal.Wrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements Wrapper {
    public static Rotation rotation(Vec3d vec) {
        Vec3d to = mc.player.getEyePos();

        double diffX = vec.x - to.x;
        double diffY = (vec.y - to.y) * -1.0;
        double diffZ = vec.z - to.z;

        double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f);
        float pitch = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffY, distance)));

        return new Rotation(RotationType.NONE, yaw, pitch);
    }
}
