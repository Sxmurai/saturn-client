/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import cope.saturn.core.events.RenderWorldEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import me.bush.eventbus.annotation.EventListener;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", Category.VISUALS, "Renders tracers");
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {

    }
}
