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
            new ChunkGeneratorType<>(TFCOverworldChunkGenerator::new, false, TFCGenerationSettings::new).setRegistryName("overworld")
        );
    }

    @SubscribeEvent
    public static void registerBiomeProviderTypes(RegistryEvent.Register<BiomeProviderType<?, ?>> event)
    {
        LOGGER.debug("Registering TFC Biome Provider Type");

        event.getRegistry().registerAll(
            new BiomeProviderType<>(TFCBiomeProvider::new, TFCGenerationSettings::new).setRegistryName("overworld")
        );
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event)
    {
        LOGGER.debug("Registering Biomes");

        event.getRegistry().registerAll(
            new OceanBiome(false).setRegistryName("ocean"),
            new OceanBiome(true).setRegistryName("deep_ocean"),
            new OceanBiome(true).setRegistryName("deep_ocean_ridge"),

            new PlainsBiome(-4, 10).setRegistryName("plains"),
            new LowlandsBiome().setRegistryName("lowlands"),
            new HillsBiome(16).setRegistryName("hills"),
            new CanyonsBiome(-5, 14).setRegistryName("low_canyons"),

            new HillsBiome(28).setRegistryName("rolling_hills"),
            new BadlandsBiome().setRegistryName("badlands"),
            new PlainsBiome(20, 30).setRegistryName("plateau"),
            new MountainsBiome(48, 28, false).setRegistryName("old_mountains"),

            new MountainsBiome(48, 56, false).setRegistryName("mountains"),
            new MountainsBiome(30, 64, true).setRegistryName("flooded_mountains"),
            new CanyonsBiome(-7, 26).setRegistryName("canyons"),

            new ShoreBiome().setRegistryName("shore"),
            new ShoreBiome().setRegistryName("stone_shore"),

            new MountainsBiome(36, 34, false).setRegistryName("mountains_edge"),
            new LakeBiome().setRegistryName("lake"),
            new RiverBiome().setRegistryName("river")
        );
    }
}
