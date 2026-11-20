package com.awesomehippo.xaeroworldmapvisualextras.mixin;

import com.awesomehippo.xaeroworldmapvisualextras.MapFrame;
import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;

@Mixin(GuiMap.class)
public abstract class GuiMapMixin extends Screen {
    @Shadow(remap = false)
    private MapProcessor mapProcessor;

    protected GuiMapMixin(Component title) {
        super(title);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"))
    private void xaeroworldmapvisualextras$background(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (Config.TRANSPARENT_BACKGROUND.get() && this.minecraft.screen == this) {
            this.renderBackground(guiGraphics);
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        }
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lxaero/map/graphics/ImprovedFramebuffer;bindAsMainTarget(Z)V", remap = false)), index = 3)
    private float xaeroworldmapvisualextras$clearAlpha(float alpha) {
        return Config.TRANSPARENT_BACKGROUND.get() ? 0.0F : alpha;
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target = "Lxaero/map/graphics/MapRenderHelper;fillIntoExistingBuffer(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIFFFF)V", remap = false), slice = @Slice(from = @At(value = "INVOKE", target = "Lxaero/map/graphics/ImprovedFramebuffer;bindDefaultFramebuffer(Lnet/minecraft/client/Minecraft;)V", remap = false)))
    private void xaeroworldmapvisualextras$skipStrip(Matrix4f matrix, VertexConsumer consumer, int x1, int y1, int x2, int y2, float r, float g, float b, float a) {
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "FIELD", target = "Lxaero/map/graphics/CustomRenderTypes;MAP:Lnet/minecraft/client/renderer/RenderType;", remap = false))
    private RenderType xaeroworldmapvisualextras$tileType() {
        return MapFrame.TILES;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target = "Lxaero/lib/client/graphics/shader/WorldMapShader;setBrightness(F)V", remap = false))
    private void xaeroworldmapvisualextras$edges(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci, @Local(name = "renderedCaveLayer") int caveLayer, @Local(name = "matrix") Matrix4f matrix, @Local(name = "flooredCameraX") int cameraX, @Local(name = "flooredCameraZ") int cameraZ, @Local(name = "minLeafRegX") int minX, @Local(name = "maxLeafRegX") int maxX, @Local(name = "minLeafRegZ") int minZ, @Local(name = "maxLeafRegZ") int maxZ) {
        MapFrame.drawEdges(this.mapProcessor, caveLayer, matrix, cameraX, cameraZ, minX, maxX, minZ, maxZ);
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "FIELD", target = "Lxaero/map/graphics/CustomRenderTypes;GUI_BILINEAR:Lnet/minecraft/client/renderer/RenderType;", ordinal = 0, remap = false), slice = @Slice(from = @At(value = "INVOKE", target = "Lxaero/map/graphics/ImprovedFramebuffer;bindDefaultFramebuffer(Lnet/minecraft/client/Minecraft;)V", remap = false)))
    private RenderType xaeroworldmapvisualextras$frameType() {
        return MapFrame.FRAME;
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target = "Lxaero/map/gui/GuiMap;renderTexturedModalRect(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFIIFFFFFFFF)V", remap = false), index = 6)
    private float xaeroworldmapvisualextras$frameWidth(float width) {
        return width + 1.0F;
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target = "Lxaero/map/gui/GuiMap;renderTexturedModalRect(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFIIFFFFFFFF)V", remap = false), index = 7)
    private float xaeroworldmapvisualextras$frameHeight(float height) {
        return height + 1.0F;
    }

    @Redirect(method = "renderMessageScreen(Lnet/minecraft/client/gui/GuiGraphics;Ljava/lang/String;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", remap = true), remap = false)
    private void xaeroworldmapvisualextras$messageFill(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int color) {
        if (!Config.TRANSPARENT_BACKGROUND.get()) {
            guiGraphics.fill(minX, minY, maxX, maxY, color);
        }
    }
}
