/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.visuals;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;

public class NoRender extends Module {
    public static NoRender INSTANCE;

    public NoRender() {
        super("NoRender", Category.VISUALS, "Stops things from rendering");
        INSTANCE = this;
    }

    public static final Setting<Boolean> hurtcam = new Setting<>("Hurtcam", true);
    public static final Setting<Boolean> fire = new Setting<>("Fire", true);
    public static final Setting<Boolean> blocks = new Setting<>("Blocks", true);
    public static final Setting<Boolean> particles = new Setting<>("Particles", true);
    public static final Setting<Boolean> totemOverlay = new Setting<>("TotemOverlay", true);
    public static final Setting<Boolean> pumpkin = new Setting<>("Pumpkin", false);
    public static final Setting<Boolean> fov = new Setting<>("FOV", true);
    public static final Setting<Boolean> advancements = new Setting<>("Advancements", false);
    public static final Setting<Armor> armor = new Setting<>("Armor", Armor.NONE);
    public static final Setting<Boolean> weather = new Setting<>("Weather", true);
    public static final Setting<Boolean> nausea = new Setting<>("Nausea", true);

    public enum Armor {
        /**
         * Keeps the default armor rendering
         */
        NONE,

        /**
         * Does not render the armor glint (enchantments)
         */
        GLINT,

        /**
         * Doesn't render armor at all
         */
        ALL
    }
}
