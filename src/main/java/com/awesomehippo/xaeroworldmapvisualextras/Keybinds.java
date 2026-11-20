package com.awesomehippo.xaeroworldmapvisualextras;

import com.awesomehippo.xaeroworldmapvisualextras.config.SettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xaero.map.gui.GuiMap;

@Mod.EventBusSubscriber(modid = XaeroWorldMapVisualExtras.MOD_ID, value = Dist.CLIENT)
public class Keybinds {
    public static final KeyMapping SETTINGS_KEY = new KeyMapping("key.xaeroworldmapvisualextras.settings", InputConstants.UNKNOWN.getValue(), "key.categories.xaeroworldmapvisualextras");

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
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

    @Mod.EventBusSubscriber(modid = XaeroWorldMapVisualExtras.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Registration {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(SETTINGS_KEY);
        }
    }
}
