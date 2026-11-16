package com.awesomehippo.xaeroworldmapvisualextras;

import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import com.awesomehippo.xaeroworldmapvisualextras.config.SettingsScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = XaeroWorldMapVisualExtras.MOD_ID, dist = Dist.CLIENT)
public class XaeroWorldMapVisualExtras {
    public static final String MOD_ID = "xaeroworldmapvisualextras";

    public XaeroWorldMapVisualExtras(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> new SettingsScreen(parent));
    }
}
