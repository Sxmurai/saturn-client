package cope.saturn.asm.mixins;

import cope.saturn.core.Saturn;
import cope.saturn.core.events.ClientTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow
    private Profiler profiler;

    @Shadow
    public ClientWorld world;

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        if (player != null && world != null) {
            profiler.push("saturn_tick");
            Saturn.EVENT_BUS.post(new ClientTickEvent());
            profiler.pop();
        }
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> info) {
        info.setReturnValue(Saturn.NAME + " v" + Saturn.VERSION);
    }
}
