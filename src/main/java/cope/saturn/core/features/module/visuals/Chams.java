/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;

public class Chams extends Module {
    public static Chams INSTANCE;

    public Chams() {
        super("Chams", Category.VISUALS, "Renders entities differently");
        INSTANCE = this;
    }
}
