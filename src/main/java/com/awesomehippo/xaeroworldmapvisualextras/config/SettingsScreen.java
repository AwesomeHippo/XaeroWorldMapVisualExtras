package com.awesomehippo.xaeroworldmapvisualextras.config;

import com.awesomehippo.xaeroworldmapvisualextras.Keybinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.minecraftforge.common.ForgeConfigSpec;
import xaero.lib.client.gui.IScreenBase;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;

public class SettingsScreen extends Screen implements IScreenBase {
    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(Component.translatable("xaeroworldmapvisualextras.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        GridLayout grid = new GridLayout(width / 2 - 205, height / 7 + 5).rowSpacing(4).columnSpacing(10);
        GridLayout.RowHelper rows = grid.createRowHelper(2);
        rows.addChild(CycleButton.onOffBuilder(Config.TRANSPARENT_BACKGROUND.get()).create(0, 0, 200, 20, Component.translatable("xaeroworldmapvisualextras.configuration.transparentBackground"), (button, value) -> Config.TRANSPARENT_BACKGROUND.set(value)));
        rows.addChild(CycleButton.builder(Config.Edges::getDisplayName).withValues(Config.Edges.values()).withInitialValue(Config.CLEAN_EDGES.get()).create(0, 0, 200, 20, Component.translatable("xaeroworldmapvisualextras.configuration.cleanEdges"), (button, value) -> Config.CLEAN_EDGES.set(value)));
        rows.addChild(CycleButton.builder(Config.Filter::getDisplayName).withValues(Config.Filter.values()).withInitialValue(Config.FILTER.get()).create(0, 0, 200, 20, Component.translatable("xaeroworldmapvisualextras.configuration.filter"), (button, value) -> Config.FILTER.set(value)));
        rows.addChild(new IntSlider("xaeroworldmapvisualextras.configuration.strength", Config.STRENGTH, 100));
        rows.addChild(new IntSlider("xaeroworldmapvisualextras.configuration.shadowDistance", Config.SHADOW_DISTANCE, Config.MAX_SHADOW_DISTANCE));
        rows.addChild(new IntSlider("xaeroworldmapvisualextras.configuration.shadowOpacity", Config.SHADOW_OPACITY, 100));
        rows.addChild(CycleButton.onOffBuilder(Config.OPEN_SOUND.get()).create(0, 0, 200, 20, Component.translatable("xaeroworldmapvisualextras.configuration.openSound"), (button, value) -> Config.OPEN_SOUND.set(value)));
        rows.addChild(CycleButton.onOffBuilder(Config.CLOSE_SOUND.get()).create(0, 0, 200, 20, Component.translatable("xaeroworldmapvisualextras.configuration.closeSound"), (button, value) -> Config.CLOSE_SOUND.set(value)));
        rows.addChild(CycleButton.onOffBuilder(Config.SINGLEPLAYER_WRITING_DISTANCE.get()).create(0, 0, 410, 20, Component.translatable("xaeroworldmapvisualextras.configuration.singleplayerWritingDistance"), (button, value) -> switchMap(value)), 2);
        grid.arrangeElements();
        grid.visitWidgets(this::addRenderableWidget);
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> onClose()).bounds(width / 2 - 100, height / 6 + 168, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, height / 7 - 13, 0xFFFFFF);
    }

    private void switchMap(boolean explored) {
        Config.SINGLEPLAYER_WRITING_DISTANCE.set(explored);
        WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session != null) {
            MapProcessor processor = session.getMapProcessor();
            synchronized (processor.uiSync) {
                if (processor.isMapWorldUsable()) {
                    processor.getMapWorld().getFutureDimension().resetCustomMultiworldUnsynced();
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Keybinds.SETTINGS_KEY.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void removed() {
        Config.SPEC.save();
    }

    @Override
    public boolean shouldSkipWorldRender() {
        return false;
    }

    @Override
    public Screen getEscape() {
        return parent;
    }

    @Override
    public void onDropdownOpen(DropDownWidget menu) {
    }

    @Override
    public void onDropdownClosed(DropDownWidget menu) {
    }

    static class IntSlider extends ForgeSlider {
        private final ForgeConfigSpec.IntValue config;

        IntSlider(String label, ForgeConfigSpec.IntValue config, int max) {
            super(0, 0, 200, 20, Component.translatable(label).append(": "), Component.empty(), 0, max, config.get(), true);
            this.config = config;
        }

        @Override
        protected void applyValue() {
            config.set(getValueInt());
        }
    }
}
