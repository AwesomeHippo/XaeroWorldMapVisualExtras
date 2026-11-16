package com.awesomehippo.xaeroworldmapvisualextras.config;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final int MAX_SHADOW_DISTANCE = 16;
    public static final String EXPLORED_MAP = "cm$exploredvisualextras";

    public static final ModConfigSpec.BooleanValue TRANSPARENT_BACKGROUND = BUILDER
            .comment("Show the world behind the map")
            .define("transparentBackground", true);

    public static final ModConfigSpec.EnumValue<Filter> FILTER = BUILDER
            .comment("Map color filter")
            .defineEnum("filter", Filter.OFF);

    public static final ModConfigSpec.IntValue STRENGTH = BUILDER
            .comment("Filter strength in %")
            .defineInRange("strength", 100, 0, 100);

    public static final ModConfigSpec.EnumValue<Edges> CLEAN_EDGES = BUILDER
            .comment("Clean map edges")
            .defineEnum("cleanEdges", Edges.OFF);

    public static final ModConfigSpec.IntValue SHADOW_DISTANCE = BUILDER
            .comment("Shadow distance (0 = no shadow)")
            .defineInRange("shadowDistance", 4, 0, MAX_SHADOW_DISTANCE);

    public static final ModConfigSpec.IntValue SHADOW_OPACITY = BUILDER
            .comment("Shadow opacity in %")
            .defineInRange("shadowOpacity", 100, 0, 100);

    public static final ModConfigSpec.BooleanValue OPEN_SOUND = BUILDER
            .comment("Play a sound when opening the map")
            .define("openSound", true);

    public static final ModConfigSpec.BooleanValue CLOSE_SOUND = BUILDER
            .comment("Play a sound when closing the map")
            .define("closeSound", true);

    public static final ModConfigSpec.BooleanValue SINGLEPLAYER_WRITING_DISTANCE = BUILDER
            .comment("Force Xaero's Map Writing Distance in singleplayer")
            .define("singleplayerWritingDistance", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public enum Filter {
        OFF(Component.translatable("xaeroworldmapvisualextras.filter.off"), 0.0F, 0.0F, 0.0F),
        SEPIA(Component.translatable("xaeroworldmapvisualextras.filter.sepia"), 0.088F, -0.02F, -0.127F),
        GRAYSCALE(Component.translatable("xaeroworldmapvisualextras.filter.grayscale"), 0.0F, 0.0F, 0.0F);

        private final Component displayName;
        public final float red;
        public final float green;
        public final float blue;

        Filter(Component displayName, float red, float green, float blue) {
            this.displayName = displayName;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public Component getDisplayName() {
            return displayName;
        }
    }

    public enum Edges {
        OFF(Component.translatable("xaeroworldmapvisualextras.edges.off")),
        REGIONS(Component.translatable("xaeroworldmapvisualextras.edges.regions")),
        RECTANGLE(Component.translatable("xaeroworldmapvisualextras.edges.rectangle"));

        private final Component displayName;

        Edges(Component displayName) {
            this.displayName = displayName;
        }

        public Component getDisplayName() {
            return displayName;
        }
    }
}
