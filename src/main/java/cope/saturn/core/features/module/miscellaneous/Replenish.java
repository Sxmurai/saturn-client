/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.miscellaneous;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;

// TODO
public class Replenish extends Module {
    public Replenish() {
        super("Replenish", Category.MISCELLANEOUS, "Replenishes your hotbar");
    }

    public static final Setting<Double> percent = new Setting<>("Percent", 30.0, 0.0, 99.0);
    public static final Setting<Integer> delay = new Setting<>("Delay", 1, 0, 10);
}
