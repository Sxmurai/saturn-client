/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.managers;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.RenderHUDEvent;
import cope.saturn.core.features.hud.HUDElement;
import cope.saturn.core.features.hud.client.Ping;
import cope.saturn.core.features.hud.client.Watermark;
import cope.saturn.core.features.hud.player.Armor;
import cope.saturn.core.features.hud.player.PlayerModel;
import cope.saturn.core.features.hud.world.Speed;
import cope.saturn.core.features.module.client.HUD;
import cope.saturn.util.internal.Wrapper;
import me.bush.eventbus.annotation.EventListener;

import java.util.ArrayList;

public class HUDManager implements Wrapper {
    private final ArrayList<HUDElement> elements = new ArrayList<>();

    public HUDManager() {
        // subscribe to events
        Saturn.EVENT_BUS.subscribe(this);

        // client
        elements.add(new Ping());
        elements.add(new Watermark());

        // player
        elements.add(new Armor());
        elements.add(new PlayerModel());

        // world
        elements.add(new Speed());

        Saturn.LOGGER.info("Loaded {} hud elements", elements.size());
    }

    @EventListener
    public void onRenderHud(RenderHUDEvent event) {
        // do not render while F3 is open
        if (!mc.options.debugEnabled && HUD.INSTANCE.isToggled()) {
            elements.forEach((element) -> {
                if (element.isEnabled()) {
                    element.render(event.getStack());
                }
            });
        }
    }

    public ArrayList<HUDElement> getElements() {
        return elements;
    }
}
