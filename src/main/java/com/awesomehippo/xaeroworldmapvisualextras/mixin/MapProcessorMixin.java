package com.awesomehippo.xaeroworldmapvisualextras.mixin;

import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapProcessor;

@Mixin(value = MapProcessor.class, remap = false)
public class MapProcessorMixin {
    @Inject(method = "actuallySkippingWorldRender", at = @At("HEAD"), cancellable = true)
    private void xaeroworldmapvisualextras$keepWorld(CallbackInfoReturnable<Boolean> cir) {
        if (Config.TRANSPARENT_BACKGROUND.get()) {
            cir.setReturnValue(false);
        }
    }
}
