package com.awesomehippo.xaeroworldmapvisualextras;

import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import com.awesomehippo.xaeroworldmapvisualextras.config.SettingsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(XaeroWorldMapVisualExtras.MOD_ID)
public class XaeroWorldMapVisualExtras {
    public static final String MOD_ID = "xaeroworldmapvisualextras";

    public XaeroWorldMapVisualExtras() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new SettingsScreen(parent)));
        }
    }
}
