package com.awesomehippo.xaeroworldmapvisualextras.mixin;

import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

import java.util.Hashtable;

@Mixin(MapDimension.class)
public abstract class MapDimensionMixin {
    @Shadow
    private String futureCustomSelectedMultiworld;

    @Shadow
    @Final
    private MapWorld mapWorld;

    @Shadow
    @Final
    private Hashtable<String, String> multiworldNames;

    @Shadow
    public abstract void saveConfigUnsynced();

    @Inject(method = "resetCustomMultiworldUnsynced", at = @At("TAIL"))
    private void xaeroworldmapvisualextras$exploredMap(CallbackInfo ci) {
        if (Config.SINGLEPLAYER_WRITING_DISTANCE.get() && !mapWorld.isMultiplayer() && (futureCustomSelectedMultiworld == null || futureCustomSelectedMultiworld.isEmpty())) {
            futureCustomSelectedMultiworld = Config.EXPLORED_MAP;
            if (!multiworldNames.containsKey(Config.EXPLORED_MAP)) {
                multiworldNames.put(Config.EXPLORED_MAP, "Explored");
                saveConfigUnsynced();
            }
        }
    }
}
