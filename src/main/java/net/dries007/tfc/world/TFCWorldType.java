/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.config.TFCConfig;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCWorldType
{
    public static final DeferredRegister<ForgeWorldPreset> WORLD_TYPES = DeferredRegister.create(ForgeRegistries.WORLD_TYPES, MOD_ID);

    public static final Supplier<NoiseGeneratorSettings> NOISE_SETTINGS = Lazy.of(() -> new NoiseGeneratorSettings(
        new StructureSettings(false),
        NoiseSettings.create(
            -64, 384,
            new NoiseSamplingSettings(1, 1, 80, 160),
            new NoiseSlider(-0.078125, 2, 8),
            new NoiseSlider(0.1171875D, 3, 0),
            1, 2, false, false, false,
            TerrainProvider.overworld(false)),
        Blocks.STONE.defaultBlockState(),
        Blocks.WATER.defaultBlockState(),
        createOverworldSurfaceRules(),
        63, false, true, true, true, true, false
    ));

    public static final RegistryObject<ForgeWorldPreset> WORLD_TYPE = WORLD_TYPES.register("tng", () -> new ForgeWorldPreset((registries, seed, settings) -> {
        final Registry<NoiseGeneratorSettings> noiseGeneratorSettings = registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        final Registry<Biome> biomes = registries.registryOrThrow(Registry.BIOME_REGISTRY);

        return TFCChunkGenerator.defaultChunkGenerator(() -> noiseGeneratorSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD), biomes, seed);
    }));

    public static void registerDefaultWorldgen()
    {
        NOISE_SETTINGS.get();
    }

    /**
     * Override the default world type, in a safe, mixin free, and API providing manner :D
     * Thank you gigahertz!
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void overrideDefaultWorldType()
    {
        if (TFCConfig.COMMON.setTFCWorldTypeAsDefault.get() && ForgeConfig.COMMON.defaultWorldType.get().equals("default"))
        {
            ((ForgeConfigSpec.ConfigValue) ForgeConfig.COMMON.defaultWorldType).set(TFCWorldType.WORLD_TYPE.getId().toString());
        }
    }

    private static SurfaceRules.RuleSource createOverworldSurfaceRules()
    {
        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(
                SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
                SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())
            ),
            SurfaceRules.ifTrue(
                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SurfaceRules.sequence(

                ))
            )
        );
    }
}
