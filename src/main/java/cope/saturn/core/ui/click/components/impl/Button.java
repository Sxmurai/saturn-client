/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click.components.impl;

import cope.saturn.core.ui.click.components.Component;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public abstract class Button extends Component {
    public Button(String name) {
        super(name);
    }

    public abstract void onInteract(int button);

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseInBounds(mouseX, mouseY)) {
            onInteract(button);
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
}
