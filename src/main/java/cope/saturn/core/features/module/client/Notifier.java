/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.client;

import cope.saturn.core.events.ModuleToggledEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.internal.ChatUtil;
import me.bush.eventbus.annotation.EventListener;

public class Notifier extends Module {
    public static Notifier INSTANCE;

    public Notifier() {
        super("Notifier", Category.CLIENT, "Notifies you of shit");
        INSTANCE = this;
    }

    public static final Setting<Boolean> modules = new Setting<>("Modules", true);

    public static final Setting<Boolean> totems = new Setting<>("Totems", true);
    public static final Setting<Boolean> self = new Setting<>(totems, "Self", false);

    @EventListener
    public void onModuleToggled(ModuleToggledEvent event) {
        if (!nullCheck() && modules.getValue()) {
            ChatUtil.sendEditable(event.getModule().hashCode(),
                    event.getModule().getName() + " " + (event.isState() ? "\u00A7aenabled" : "\u00A7cdisabled") + ".");
        }
    }
}
