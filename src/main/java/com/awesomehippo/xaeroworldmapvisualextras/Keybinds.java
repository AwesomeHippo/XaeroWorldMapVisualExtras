package com.awesomehippo.xaeroworldmapvisualextras;

import com.awesomehippo.xaeroworldmapvisualextras.config.SettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import xaero.map.gui.GuiMap;

@EventBusSubscriber(modid = XaeroWorldMapVisualExtras.MOD_ID, value = Dist.CLIENT)
public class Keybinds {
    public static final KeyMapping SETTINGS_KEY = new KeyMapping("key.xaeroworldmapvisualextras.settings", InputConstants.UNKNOWN.getValue(), "key.categories.xaeroworldmapvisualextras");

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SETTINGS_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (SETTINGS_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new SettingsScreen(null));
        }
    }

    @SubscribeEvent
    public static void onMapKey(ScreenEvent.KeyPressed.Pre event) {
        if (event.getScreen() instanceof GuiMap map && !(map.getFocused() instanceof EditBox box && box.isFocused()) && SETTINGS_KEY.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            Minecraft.getInstance().setScreen(new SettingsScreen(map));
            event.setCanceled(true);
        }
    }
}
