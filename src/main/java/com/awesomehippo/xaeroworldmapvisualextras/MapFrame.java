package com.awesomehippo.xaeroworldmapvisualextras;

import com.awesomehippo.xaeroworldmapvisualextras.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import xaero.lib.client.graphics.shader.LibShaders;
import xaero.map.MapProcessor;
import xaero.map.file.RegionDetection;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.world.MapDimension;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = XaeroWorldMapVisualExtras.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MapFrame extends RenderType {
    private static ShaderInstance frameShader;
    private static ShaderInstance tileShader;
    private static ShaderInstance minimapShader;

    private static final ShaderStateShard FRAME_SHADER = new ShaderStateShard(() -> frameShader);

    private static final ShaderStateShard TILE_SHADER = new ShaderStateShard(() -> tileShader);

    private static final TexturingStateShard FRAME_FILTER = new TexturingStateShard("xaeroworldmapvisualextras_filter", () -> {
        Config.Filter filter = Config.FILTER.get();
        frameShader.safeGetUniform("Tone").set(filter.red, filter.green, filter.blue);
        frameShader.safeGetUniform("Strength").set(filter == Config.Filter.OFF ? 0.0F : Config.STRENGTH.get() / 100.0F);
        frameShader.safeGetUniform("Shadow").set((float) Minecraft.getInstance().getWindow().getGuiScale() * Config.SHADOW_DISTANCE.get());
        frameShader.safeGetUniform("ShadowOpacity").set(Config.SHADOW_OPACITY.get() / 100.0F);
    }, () -> {});

    private static final TexturingStateShard TILE_LIGHT = new TexturingStateShard("xaeroworldmapvisualextras_tile_light", () -> {
        tileShader.safeGetUniform("Brightness").set(LibShaders.WORLD_MAP.brightness.getFloatBuffer().get(0));
        tileShader.safeGetUniform("WithLight").set(LibShaders.WORLD_MAP.withLight.getIntBuffer().get(0));
    }, () -> {});

    public static final RenderType FRAME = create("xaeroworldmapvisualextras_frame", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder().setShaderState(FRAME_SHADER).setTexturingState(FRAME_FILTER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));

    public static final RenderType TILES = create("xaeroworldmapvisualextras_tiles", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder().setShaderState(TILE_SHADER).setTexturingState(TILE_LIGHT).setCullState(NO_CULL).createCompositeState(false));

    public static final RenderType EDGES = create("xaeroworldmapvisualextras_edges", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).createCompositeState(false));

    private static final float GRAY = 0.25F;

    private static final Map<RegionDetection, Boolean> TERRAIN = new WeakHashMap<>();
    private static int terrainLayer;
    private static long lastDraw;

    private MapFrame(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(XaeroWorldMapVisualExtras.MOD_ID, "map_frame"), DefaultVertexFormat.POSITION_COLOR_TEX), instance -> frameShader = instance);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(XaeroWorldMapVisualExtras.MOD_ID, "map_tiles"), DefaultVertexFormat.POSITION_TEX), instance -> tileShader = instance);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(XaeroWorldMapVisualExtras.MOD_ID, "minimap"), DefaultVertexFormat.POSITION_TEX), instance -> minimapShader = instance);
    }

    public static void drawMinimap(Matrix4f matrix, float x, float y, float width, float height) {
        Config.Filter filter = Config.FILTER.get();
        minimapShader.safeGetUniform("Tone").set(filter.red, filter.green, filter.blue);
        minimapShader.safeGetUniform("Strength").set(Config.STRENGTH.get() / 100.0F);
        RenderSystem.setShader(() -> minimapShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, 0.0F).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0.0F).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y, 0.0F).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, x, y, 0.0F).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawEdges(MapProcessor processor, int caveLayer, Matrix4f matrix, int cameraX, int cameraZ, int minX, int maxX, int minZ, int maxZ) {
        Config.Edges edges = Config.CLEAN_EDGES.get();
        if (edges == Config.Edges.OFF) {
            return;
        }
        MapDimension dimension = processor.getMapWorld().getCurrentDimension();
        LayeredRegionManager regions = dimension.getLayeredMapRegions();
        boolean worldSave = dimension.isUsingWorldSave();
        long time = Util.getMillis();
        boolean reopened = time - lastDraw > 1000L;
        lastDraw = time;
        if (caveLayer != terrainLayer) {
            TERRAIN.clear();
            terrainLayer = caveLayer;
        }
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int x1 = Integer.MAX_VALUE;
        int z1 = Integer.MAX_VALUE;
        int x2 = Integer.MIN_VALUE;
        int z2 = Integer.MIN_VALUE;
        for (RegionDetection region : regions.getLayer(caveLayer).getLinkedCompleteWorldSaveDetectedRegions()) {
            if (worldSave && !hasTerrain(processor, regions, caveLayer, region, reopened)) {
                continue;
            }
            int x = region.getRegionX();
            int z = region.getRegionZ();
            if (edges == Config.Edges.RECTANGLE) {
                x1 = Math.min(x1, x);
                z1 = Math.min(z1, z);
                x2 = Math.max(x2, x);
                z2 = Math.max(z2, z);
            } else if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                MapRenderHelper.fillIntoExistingBuffer(matrix, buffer, x * 512 - cameraX, z * 512 - cameraZ, (x + 1) * 512 - cameraX, (z + 1) * 512 - cameraZ, GRAY, GRAY, GRAY, 1.0F);
            }
        }
        x1 = Math.max(x1, minX);
        z1 = Math.max(z1, minZ);
        x2 = Math.min(x2, maxX);
        z2 = Math.min(z2, maxZ);
        if (x1 <= x2 && z1 <= z2) {
            MapRenderHelper.fillIntoExistingBuffer(matrix, buffer, x1 * 512 - cameraX, z1 * 512 - cameraZ, (x2 + 1) * 512 - cameraX, (z2 + 1) * 512 - cameraZ, GRAY, GRAY, GRAY, 1.0F);
        }
        EDGES.end(buffer, RenderSystem.getVertexSorting());
    }

    private static boolean hasTerrain(MapProcessor processor, LayeredRegionManager regions, int caveLayer, RegionDetection detection, boolean reopened) {
        Boolean known = TERRAIN.get(detection);
        if (Boolean.TRUE.equals(known)) {
            return true;
        }
        boolean terrain = (known == null || reopened) && hasCache(processor, caveLayer, detection);
        if (!terrain) {
            terrain = hasLoadedTerrain(regions.getLeaf(caveLayer, detection.getRegionX(), detection.getRegionZ()));
        }
        if (known == null || terrain) {
            TERRAIN.put(detection, terrain);
        }
        return terrain;
    }

    private static boolean hasCache(MapProcessor processor, int caveLayer, RegionDetection detection) {
        try {
            File cache = processor.getMapSaveLoad().getCacheFile(detection, caveLayer, true, false);
            return cache != null && cache.exists();
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean hasLoadedTerrain(MapRegion region) {
        if (region != null) {
            for (int x = 0; x < 8; x++) {
                for (int z = 0; z < 8; z++) {
                    MapTileChunk chunk = region.getChunk(x, z);
                    if (chunk != null && chunk.hasHadTerrain()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
