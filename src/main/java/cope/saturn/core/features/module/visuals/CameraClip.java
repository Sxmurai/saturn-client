/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;

public class CameraClip extends Module {
    public static CameraClip INSTANCE;

    public CameraClip() {
        super("CameraClip", Category.VISUALS, "Clips your camera through blocks");
        INSTANCE = this;
    }

    public static final Setting<Double> distance = new Setting<>("Distance", 3.0, 1.0, 10.0);
}
