/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.ui.click;

import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.features.module.client.ClickGUI;
import cope.saturn.core.ui.click.components.Frame;
import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends Screen implements Wrapper {
    private static ClickGUIScreen INSTANCE;

    private final ArrayList<Frame> frames = new ArrayList<>();

    protected ClickGUIScreen() {
        super(new LiteralText("ClickGUI"));

        double x = 4.0;

        for (Category category : Category.values()) {
            List<Module> modules = getSaturn().getModuleManager().getModules()
                    .stream().filter((mod) -> mod.getCategory().equals(category)).toList();

            if (modules.isEmpty()) {
                continue;
            }

            frames.add(new Frame(x, category, modules));
            x += 110.0;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        frames.forEach((frame) -> frame.render(matrices, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        frames.forEach((frame) -> frame.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        frames.forEach((frame) -> frame.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        frames.forEach((frame) -> frame.keyPressed('n', keyCode));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        ClickGUI.INSTANCE.disable();
    }

    public static ClickGUIScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGUIScreen();
        }

        return INSTANCE;
    }
}
