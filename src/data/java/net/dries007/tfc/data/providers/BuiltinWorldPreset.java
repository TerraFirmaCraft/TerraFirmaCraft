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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
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
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.util.Helpers;
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
        final HolderGetter<RockSettings> rockSettings = context.lookup(RockSettings.KEY);

        context.register(
            TerraFirmaCraft.PRESET,
            new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(
                    dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                    new TFCChunkGenerator(
                        new RegionBiomeSource(context.lookup(Registries.BIOME)),
                        noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD),
                        defaultSettings(rockSettings)
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

    public static Settings defaultSettings(HolderGetter<RockSettings> rockSettings)
    {
        return new Settings(false, 4_000, 0, 0, 20_000, 0, 20_000, 0, rockLayerSettings(rockSettings), 0.5f, 0.5f, false);
    }

    // For tests
    public static Settings defaultSettings()
    {
        return defaultSettings(new HolderGetter<>()
        {
            final Map<ResourceKey<RockSettings>, Holder.Reference<RockSettings>> values = Arrays.stream(Rock.values())
                .collect(Collectors.toMap(
                    BuiltinWorldPreset::rockKey,
                    r -> Holder.Reference.createIntrusive(null, BuiltinRockSettings.makeSetting(r))
                ));

            @Override
            public Optional<Holder.Reference<RockSettings>> get(ResourceKey<RockSettings> resourceKey)
            {
                return Optional.ofNullable(values.get(resourceKey));
            }

            @Override
            public Optional<HolderSet.Named<RockSettings>> get(TagKey<RockSettings> tagKey)
            {
                return Optional.empty();
            }
        });
    }

    private static final String BOTTOM = "bottom";
    private static final String IGNEOUS_EXTRUSIVE = "igneous_extrusive";
    private static final String IGNEOUS_EXTRUSIVE_X2 = "igneous_extrusive_x2";
    private static final String IGNEOUS_INTRUSIVE = "igneous_intrusive";
    private static final String SEDIMENTARY = "sedimentary";
    private static final String UPLIFT = "uplift";
    private static final String FELSIC = "felsic";
    private static final String INTERMEDIATE = "intermediate";
    private static final String MAFIC = "mafic";
    private static final String MM_LOW_GRADE = "low_grade";
    private static final String MM_HIGH_GRADE = "high_grade";
    private static final String MM_MARBLE = "marble";
    private static final String MM_QUARTZITE = "quartzite";

    private static RockLayerSettings rockLayerSettings(HolderGetter<RockSettings> rockSettings)
    {
        return RockLayerSettings.decode(new Data(
            Arrays.stream(Rock.values()).collect(Collectors.toMap(
                Rock::getSerializedName,
                r -> rockSettings.getOrThrow(rockKey(r))
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
                layerOf(IGNEOUS_INTRUSIVE, Map.of(
                    GRANITE, FELSIC,
                    DIORITE, INTERMEDIATE,
                    GABBRO, MAFIC
                )),
                layerOf(MM_HIGH_GRADE, Map.of(
                    SCHIST, BOTTOM,
                    GNEISS, BOTTOM
                )),
                layerOf(MM_LOW_GRADE, Map.of(
                    PHYLLITE, MM_HIGH_GRADE,
                    SLATE, MM_HIGH_GRADE
                )),
                layerOf(MM_MARBLE, Map.of(MARBLE, BOTTOM)),
                layerOf(MM_QUARTZITE, Map.of(QUARTZITE, BOTTOM)),
                layerOf(SEDIMENTARY, Map.of(
                    SHALE, MM_LOW_GRADE,
                    CLAYSTONE, MM_LOW_GRADE,
                    CONGLOMERATE, MM_LOW_GRADE,
                    LIMESTONE, MM_MARBLE,
                    DOLOMITE, MM_MARBLE,
                    CHALK, MM_MARBLE,
                    CHERT, MM_QUARTZITE
                )),
                layerOf(UPLIFT, Map.of(
                    SLATE, MM_HIGH_GRADE,
                    PHYLLITE, MM_HIGH_GRADE,
                    SCHIST, MM_HIGH_GRADE,
                    GNEISS, MM_HIGH_GRADE,
                    MARBLE, BOTTOM,
                    QUARTZITE, BOTTOM,
                    DIORITE, MM_LOW_GRADE,
                    GRANITE, MM_LOW_GRADE,
                    GABBRO, MM_LOW_GRADE
                ))
            ),
            // List of layers that can be the top layer for each variety of rock region
            List.of(IGNEOUS_EXTRUSIVE),
            List.of(SEDIMENTARY, SEDIMENTARY, SEDIMENTARY, IGNEOUS_EXTRUSIVE),
            List.of(IGNEOUS_EXTRUSIVE, IGNEOUS_EXTRUSIVE_X2, IGNEOUS_INTRUSIVE),
            List.of(UPLIFT, UPLIFT, UPLIFT, SEDIMENTARY)
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

    public static ResourceKey<RockSettings> rockKey(Rock rock)
    {
        return ResourceKey.create(RockSettings.KEY, Helpers.identifier(rock.getSerializedName()));
    }
}
