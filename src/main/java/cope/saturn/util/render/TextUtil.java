/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.render;

import cope.saturn.util.internal.Wrapper;

public class TextUtil implements Wrapper {
    public static double alignH(double y, double height) {
        return (y + (height / 2.0)) - mc.textRenderer.fontHeight / 2.0;
    }
}
