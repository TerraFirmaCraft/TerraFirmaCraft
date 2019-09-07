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
import net.minecraftforge.registries.ObjectHolder;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.biome.provider.TFCBiomeProvider;
import net.dries007.tfc.world.gen.TFCGenerationSettings;
import net.dries007.tfc.world.gen.TFCOverworldChunkGenerator;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCWorldType extends WorldType
{
    @ObjectHolder(MOD_ID + ":overworld")
    public static final ChunkGeneratorType<TFCGenerationSettings, TFCOverworldChunkGenerator> CHUNK_GENERATOR_TYPE = Helpers.getNull();

    @ObjectHolder(MOD_ID + ":overworld")
    public static final BiomeProviderType<TFCGenerationSettings, TFCBiomeProvider> BIOME_PROVIDER_TYPE = Helpers.getNull();

    public TFCWorldType()
    {
        super("tfc");
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator(World world)
    {
        // Create default settings objects
        // todo: are these able to be customized via gui somehow?

        TFCGenerationSettings settings = CHUNK_GENERATOR_TYPE.createSettings();
        //TFCGenerationSettings biomeGenSettings = BIOME_PROVIDER_TYPE.createSettings();

        settings.setWorldInfo(world.getWorldInfo());

        // Create biome provider and chunk generator
        BiomeProvider biomeProvider = BIOME_PROVIDER_TYPE.create(settings);
        return CHUNK_GENERATOR_TYPE.create(world, biomeProvider, settings);
    }
}
