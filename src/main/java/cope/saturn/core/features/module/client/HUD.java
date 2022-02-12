/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.client;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;

public class HUD extends Module {
    public static HUD INSTANCE;

    public HUD() {
        super("HUD", Category.CLIENT, "Renders the client's HUD");
        INSTANCE = this;
    }
}
