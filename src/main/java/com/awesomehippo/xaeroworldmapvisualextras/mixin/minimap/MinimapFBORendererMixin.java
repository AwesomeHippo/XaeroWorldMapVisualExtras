package com.awesomehippo.xaeroworldmapvisualextras.mixin.minimap;

import com.awesomehippo.xaeroworldmapvisualextras.MapFrame;
import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.common.minimap.render.MinimapFBORenderer;
import xaero.common.minimap.render.MinimapRendererHelper;

@Mixin(MinimapFBORenderer.class)
public class MinimapFBORendererMixin {
    @WrapOperation(method = "renderChunksToFBO", at = @At(value = "INVOKE", target = "Lxaero/common/minimap/render/MinimapRendererHelper;drawMyTexturedModalRectPre(Lcom/mojang/blaze3d/vertex/PoseStack;FFIIFFFFF)V"), require = 0)
    private void xaeroworldmapvisualextras$filter(MinimapRendererHelper helper, PoseStack matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, float discardAlpha, Operation<Void> original) {
        if (Config.FILTER.get() == Config.Filter.OFF) {
            original.call(helper, matrixStack, x, y, textureX, textureY, width, height, theight, factor, discardAlpha);
        } else {
            MapFrame.drawMinimap(matrixStack.last().pose(), x, y, width, height);
        }
    }
}
