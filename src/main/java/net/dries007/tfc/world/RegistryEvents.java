/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.biome.provider.TFCBiomeProvider;
import net.dries007.tfc.world.biome.provider.TFCBiomeProviderSettings;
import net.dries007.tfc.world.gen.TFCGenerationSettings;
import net.dries007.tfc.world.gen.TFCOverworldChunkGenerator;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryEvents
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerChunkGeneratorTypes(RegistryEvent.Register<ChunkGeneratorType<?, ?>> event)
    {
        LOGGER.debug("Registering TFC Chunk Generator Type");

        event.getRegistry().registerAll(
            new ChunkGeneratorType<>(TFCOverworldChunkGenerator::new, false, TFCGenerationSettings::new).setRegistryName(MOD_ID, "overworld")
        );
    }

    @SubscribeEvent
    public static void registerBiomeProviderTypes(RegistryEvent.Register<BiomeProviderType<?, ?>> event)
    {
        LOGGER.debug("Registering TFC Biome Provider Type");

        event.getRegistry().registerAll(
            new BiomeProviderType<>(TFCBiomeProvider::new, TFCBiomeProviderSettings::new).setRegistryName(MOD_ID, "overworld")
        );
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event)
    {
        LOGGER.debug("Registering Biomes");

        event.getRegistry().registerAll(
            new OceanBiome(false).setRegistryName(MOD_ID, "ocean"),
            new OceanBiome(true).setRegistryName(MOD_ID, "deep_ocean"),
            new OceanBiome(true).setRegistryName(MOD_ID, "deep_ocean_ridge"),

            new PlainsBiome(-4, 10).setRegistryName(MOD_ID, "plains"),
            new LowlandsBiome().setRegistryName(MOD_ID, "lowlands"),
            new HillsBiome(16).setRegistryName(MOD_ID, "hills"),
            new CanyonsBiome(-5, 14).setRegistryName(MOD_ID, "low_canyons"),

            new HillsBiome(28).setRegistryName(MOD_ID, "rolling_hills"),
            new BadlandsBiome().setRegistryName(MOD_ID, "badlands"),
            new PlainsBiome(20, 30).setRegistryName(MOD_ID, "plateau"),
            new MountainsBiome(48, 28).setRegistryName(MOD_ID, "old_mountains"),

            new MountainsBiome(48, 56).setRegistryName(MOD_ID, "mountains"),
            new MountainsBiome(30, 64).setRegistryName(MOD_ID, "flooded_mountains"),
            new CanyonsBiome(-7, 26).setRegistryName(MOD_ID, "canyons"),

            new ShoreBiome().setRegistryName(MOD_ID, "shore"),
            new ShoreBiome().setRegistryName(MOD_ID, "stone_shore"),

            new MountainsBiome(36, 34).setRegistryName(MOD_ID, "mountains_edge"),
            new LakeBiome().setRegistryName(MOD_ID, "lake")
        );
    }
}
