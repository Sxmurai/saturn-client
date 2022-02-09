/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.combat;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;

public class ShieldAura extends Module {
    public ShieldAura() {
        super("ShieldAura", Category.COMBAT, "Disables players shields around you");
    }

    public static final Setting<Object> ranges = new Setting<>("Ranges", null);
    public static final Setting<Double> range = new Setting<>(ranges, "Range", 4.5, 1.0, 6.0);
    public static final Setting<Boolean> walls = new Setting<>(ranges, "Walls", true);
    public static final Setting<Double> wallRange = new Setting<>(walls, "WallRange", 3.5, 1.0, 6.0);

    public static final Setting<Boolean> rotate = new Setting<>("Rotate", true);
    public static final Setting<Boolean> swing = new Setting<>("Swing", true);
}
