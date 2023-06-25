/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.dries007.tfc.config.TFCConfig;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCWorldType
{
    public static final DeferredRegister<WorldPreset> WORLD_PRESETS = DeferredRegister.create(Registries.WORLD_PRESET, MOD_ID);

    public static final RegistryObject<WorldPreset> WORLD_TYPE = WORLD_PRESETS.register("tng", () -> new WorldPreset((registries, seed, settings) -> {
        final Registry<NormalNoise.NoiseParameters> noiseParameters = registries.registryOrThrow(Registry.NOISE_REGISTRY);
        final Registry<NoiseGeneratorSettings> noiseGeneratorSettings = registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        final Registry<Biome> biomes = registries.registryOrThrow(Registries.BIOME);

        return TFCChunkGenerator.defaultChunkGenerator(noiseParameters, noiseGeneratorSettings.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD), biomes, seed);
    }));

    private WorldPreset createPresetWithCustomOverworld(LevelStem levelStem)
    {
        return new WorldPreset(Map.of(LevelStem.OVERWORLD, levelStem, LevelStem.NETHER, this.netherStem, LevelStem.END, this.endStem));
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
}
