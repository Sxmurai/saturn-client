/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.asm.mixins.render.gui;

import cope.saturn.core.features.module.miscellaneous.AutoReconnect;
import cope.saturn.util.internal.Stopwatch;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {
    private final Stopwatch stopwatch = new Stopwatch();

    protected MixinDisconnectedScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo info) {
        if (AutoReconnect.INSTANCE.isToggled() && AutoReconnect.serverInfo != null) {
            Drawable first = ((IScreen) this).getDrawables().get(0);
            if (first instanceof ButtonWidget widget) {
                stopwatch.reset();

                addDrawable(new ButtonWidget(
                        widget.x, widget.y + widget.getHeight() + 4,
                        200, 20, new LiteralText("Reconnect"),
                        (action) -> {
                            if (AutoReconnect.serverInfo != null) {
                                ConnectScreen.connect(this, client, ServerAddress.parse(AutoReconnect.serverInfo.address), AutoReconnect.serverInfo);
                            }
                        }));
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (AutoReconnect.INSTANCE.isToggled() && AutoReconnect.serverInfo != null) {
            String text = "Reconnecting in " + (int) (5.0f - (float) (stopwatch.getPassedMs() / 1000L)) + " seconds";
            client.textRenderer.draw(matrices, text, (width / 2.0f) - (client.textRenderer.getWidth(text) / 2.0f), 20, -1);

            if (stopwatch.passedMs(5L * 1000L)) {
                ConnectScreen.connect(this, client, ServerAddress.parse(AutoReconnect.serverInfo.address), AutoReconnect.serverInfo);
            }
        }
    }
}
