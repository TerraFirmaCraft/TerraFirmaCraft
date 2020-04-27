/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.biome.TFCBiomeProvider;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCWorldType extends WorldType
{
    public static final DeferredRegister<ChunkGeneratorType<?, ?>> CHUNK_GENERATORS = new DeferredRegister<>(ForgeRegistries.CHUNK_GENERATOR_TYPES, MOD_ID);
    public static final DeferredRegister<BiomeProviderType<?, ?>> BIOME_PROVIDERS = new DeferredRegister<>(ForgeRegistries.BIOME_PROVIDER_TYPES, MOD_ID);

    public static final RegistryObject<ChunkGeneratorType<TFCGenerationSettings, TFCOverworldChunkGenerator>> CHUNK_GENERATOR_TYPE = CHUNK_GENERATORS.register("overworld", () -> new ChunkGeneratorType<>(TFCOverworldChunkGenerator::new, false, TFCGenerationSettings::new));

    public static final RegistryObject<BiomeProviderType<TFCGenerationSettings, TFCBiomeProvider>> BIOME_PROVIDER_TYPE = BIOME_PROVIDERS.register("overworld", () -> new BiomeProviderType<>(TFCBiomeProvider::new, TFCGenerationSettings::new));

    public TFCWorldType()
    {
        super("tfc");
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator(World world)
    {
        TFCGenerationSettings settings = CHUNK_GENERATOR_TYPE.get().createSettings();
        settings.setWorldInfo(world.getWorldInfo());
        BiomeProvider biomeProvider = BIOME_PROVIDER_TYPE.get().create(settings);
        return CHUNK_GENERATOR_TYPE.get().create(world, biomeProvider, settings);
    }
}
