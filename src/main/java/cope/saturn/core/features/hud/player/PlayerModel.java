/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.hud.player;

import cope.saturn.core.features.hud.Category;
import cope.saturn.core.features.hud.HUDElement;
import cope.saturn.util.entity.player.rotation.Rotation;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;

public class PlayerModel extends HUDElement {
    public PlayerModel() {
        super("PlayerModel", Category.PLAYER);
    }

    @Override
    public void render(MatrixStack stack) {
        Rotation rotation = getSaturn().getRotationManager().getRotation();

        float yaw = rotation.isValid() ? rotation.yaw() : mc.player.getYaw();
        float pitch = rotation.isValid() ? rotation.pitch() : mc.player.getPitch();

        InventoryScreen.drawEntity(
                (int) (getX() + 50.0),
                (int) (getY() + 132.0),
                60,
                -yaw,
                -pitch,
                mc.player);

        setWidth(100.0f);
        setHeight(150.0f);
    }
}
