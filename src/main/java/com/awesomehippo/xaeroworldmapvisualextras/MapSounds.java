package com.awesomehippo.xaeroworldmapvisualextras;

import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xaero.map.gui.GuiMap;

@Mod.EventBusSubscriber(modid = XaeroWorldMapVisualExtras.MOD_ID, value = Dist.CLIENT)
public class MapSounds {
    private static Screen lastScreen;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof GuiMap && lastScreen == null && Config.OPEN_SOUND.get()) {
            mc.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
        } else if (mc.screen == null && lastScreen instanceof GuiMap && Config.CLOSE_SOUND.get()) {
            mc.player.playSound(SoundEvents.BOOK_PUT, 1.0F, 1.0F);
        }
        lastScreen = mc.screen;
    }
}
