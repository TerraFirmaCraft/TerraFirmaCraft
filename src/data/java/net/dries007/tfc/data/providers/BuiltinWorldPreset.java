/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.RegionBiomeSource;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockLayerSettings.Data;
import net.dries007.tfc.world.settings.RockLayerSettings.LayerData;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.settings.Settings;

import static net.dries007.tfc.common.blocks.rock.Rock.*;


public final class BuiltinWorldPreset
{
    public static void load(BootstrapContext<WorldPreset> context)
    {
        final HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        final HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);

        context.register(
            TerraFirmaCraft.PRESET,
            new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(
                    dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                    new TFCChunkGenerator(
                        new RegionBiomeSource(context.lookup(Registries.BIOME)),
                        noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD),
                        new Settings(false, 4_000, 0, 0, 20_000, 0, 20_000, 0, rockLayerSettings(), 0.5f, 0.5f)
                    )
                ),
                LevelStem.NETHER, new LevelStem(
                    dimensionTypes.getOrThrow(BuiltinDimensionTypes.NETHER),
                    new NoiseBasedChunkGenerator(
                        MultiNoiseBiomeSource.createFromPreset(context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST).getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)),
                        noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER)
                    )
                ),
                LevelStem.END, new LevelStem(
                    dimensionTypes.getOrThrow(BuiltinDimensionTypes.END),
                    new NoiseBasedChunkGenerator(
                        TheEndBiomeSource.create(context.lookup(Registries.BIOME)),
                        noiseSettings.getOrThrow(NoiseGeneratorSettings.END)
                    )
                )
            )));
    }

    public static Settings defaultSettings()
    {
        return new Settings(false, 4_000, 0, 0, 20_000, 0, 20_000, 0, rockLayerSettings(), 0.5f, 0.5f);
    }

    private static final Map<Rock, SandBlockType> ROCK_TO_SAND_COLOR = ImmutableMap.<Rock, SandBlockType>builder()
        .put(GRANITE, SandBlockType.BROWN)
        .put(DIORITE, SandBlockType.WHITE)
        .put(GABBRO, SandBlockType.BLACK)
        .put(SHALE, SandBlockType.BLACK)
        .put(CLAYSTONE, SandBlockType.BROWN)
        .put(LIMESTONE, SandBlockType.WHITE)
        .put(CONGLOMERATE, SandBlockType.GREEN)
        .put(DOLOMITE, SandBlockType.BLACK)
        .put(CHERT, SandBlockType.YELLOW)
        .put(CHALK, SandBlockType.WHITE)
        .put(RHYOLITE, SandBlockType.RED)
        .put(BASALT, SandBlockType.RED)
        .put(ANDESITE, SandBlockType.RED)
        .put(DACITE, SandBlockType.RED)
        .put(QUARTZITE, SandBlockType.YELLOW)
        .put(SLATE, SandBlockType.BROWN)
        .put(PHYLLITE, SandBlockType.BROWN)
        .put(SCHIST, SandBlockType.GREEN)
        .put(GNEISS, SandBlockType.GREEN)
        .put(MARBLE, SandBlockType.WHITE)
        .build();

    private static final String BOTTOM = "bottom";
    private static final String IGNEOUS_EXTRUSIVE = "igneous_extrusive";
    private static final String IGNEOUS_EXTRUSIVE_X2 = "igneous_extrusive_x2";
    private static final String SEDIMENTARY = "sedimentary";
    private static final String UPLIFT = "uplift";
    private static final String FELSIC = "felsic";
    private static final String INTERMEDIATE = "intermediate";
    private static final String MAFIC = "mafic";
    private static final String MM_PHYLLITE = "phyllite";
    private static final String MM_SLATE = "slate";
    private static final String MM_MARBLE = "marble";
    private static final String MM_QUARTZITE = "quartzite";

    private static RockLayerSettings rockLayerSettings()
    {
        return RockLayerSettings.decode(new Data(
            Arrays.stream(Rock.values()).collect(Collectors.toMap(
                Rock::getSerializedName,
                BuiltinWorldPreset::rockOf
            )),
            namesOf(GNEISS, SCHIST, DIORITE, GRANITE, GABBRO),
            List.of(
                layerOf(FELSIC, Map.of(GRANITE, BOTTOM)),
                layerOf(INTERMEDIATE, Map.of(DIORITE, BOTTOM)),
                layerOf(MAFIC, Map.of(GABBRO, BOTTOM)),
                layerOf(IGNEOUS_EXTRUSIVE, Map.of(
                    RHYOLITE, FELSIC,
                    ANDESITE, INTERMEDIATE,
                    DACITE, INTERMEDIATE,
                    BASALT, MAFIC
                )),
                layerOf(IGNEOUS_EXTRUSIVE_X2, Map.of(
                    RHYOLITE, IGNEOUS_EXTRUSIVE,
                    ANDESITE, IGNEOUS_EXTRUSIVE,
                    DACITE, IGNEOUS_EXTRUSIVE,
                    BASALT, IGNEOUS_EXTRUSIVE
                )),
                layerOf(MM_PHYLLITE, Map.of(
                    PHYLLITE, BOTTOM,
                    GNEISS, BOTTOM,
                    SCHIST, BOTTOM
                )),
                layerOf(MM_SLATE, Map.of(
                    SLATE, BOTTOM,
                    PHYLLITE, MM_PHYLLITE
                )),
                layerOf(MM_MARBLE, Map.of(MARBLE, BOTTOM)),
                layerOf(MM_QUARTZITE, Map.of(QUARTZITE, BOTTOM)),
                layerOf(SEDIMENTARY, Map.of(
                    SHALE, MM_SLATE,
                    CLAYSTONE, MM_SLATE,
                    CONGLOMERATE, MM_SLATE,
                    LIMESTONE, MM_MARBLE,
                    DOLOMITE, MM_MARBLE,
                    CHALK, MM_MARBLE,
                    CHERT, MM_QUARTZITE
                )),
                layerOf(UPLIFT, Map.of(
                    SLATE, MM_PHYLLITE,
                    MARBLE, BOTTOM,
                    QUARTZITE, BOTTOM,
                    DIORITE, SEDIMENTARY,
                    GRANITE, SEDIMENTARY,
                    GABBRO, SEDIMENTARY
                ))
            ),
            List.of(IGNEOUS_EXTRUSIVE),
            List.of(IGNEOUS_EXTRUSIVE, SEDIMENTARY),
            List.of(IGNEOUS_EXTRUSIVE, IGNEOUS_EXTRUSIVE_X2),
            List.of(SEDIMENTARY, UPLIFT)
        )).getOrThrow();
    }
    
    private static List<String> namesOf(Rock... rocks)
    {
        return Stream.of(rocks).map(Rock::getSerializedName).toList();
    }

    private static LayerData layerOf(String layerId, Map<Rock, String> layers)
    {
        return new LayerData(layerId, layers.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getSerializedName(), Map.Entry::getValue)));
    }
    
    private static RockSettings rockOf(Rock rock)
    {
        final var blocks = TFCBlocks.ROCK_BLOCKS.get(rock);
        final var color = ROCK_TO_SAND_COLOR.get(rock);
        return new RockSettings(
            blocks.get(BlockType.RAW).get(),
            blocks.get(BlockType.HARDENED).get(),
            blocks.get(BlockType.GRAVEL).get(),
            blocks.get(BlockType.COBBLE).get(),
            TFCBlocks.SAND.get(color).get(),
            TFCBlocks.SANDSTONE.get(color).get(SandstoneBlockType.RAW).get(),
            Optional.of(blocks.get(BlockType.SPIKE).get()),
            Optional.of(blocks.get(BlockType.LOOSE).get()),
            Optional.of(blocks.get(BlockType.MOSSY_LOOSE).get())
        );
    }
}
