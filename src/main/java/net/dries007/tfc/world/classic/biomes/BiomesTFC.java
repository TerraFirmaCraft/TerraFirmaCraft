/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.api.util.TFCConstants.MOD_NAME;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class BiomesTFC
{
    public static final BiomeTFC OCEAN = Helpers.getNull();
    public static final BiomeTFC RIVER = Helpers.getNull();
    public static final BiomeTFC BEACH = Helpers.getNull();
    public static final BiomeTFC GRAVEL_BEACH = Helpers.getNull();
    public static final BiomeTFC HIGH_HILLS = Helpers.getNull();
    public static final BiomeTFC PLAINS = Helpers.getNull();
    public static final BiomeTFC SWAMPLAND = Helpers.getNull();
    public static final BiomeTFC HIGH_HILLS_EDGE = Helpers.getNull();
    public static final BiomeTFC ROLLING_HILLS = Helpers.getNull();
    public static final BiomeTFC MOUNTAINS = Helpers.getNull();
    public static final BiomeTFC MOUNTAINS_EDGE = Helpers.getNull();
    public static final BiomeTFC HIGH_PLAINS = Helpers.getNull();
    public static final BiomeTFC DEEP_OCEAN = Helpers.getNull();
    public static final BiomeTFC LAKE = Helpers.getNull();

    private static final List<Biome> SPAWN_BIOMES = new ArrayList<>();
    private static final List<Biome> WORLD_GEN_BIOMES = new ArrayList<>();

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event)
    {
        IForgeRegistry<Biome> r = event.getRegistry();

        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Ocean").setBaseHeight(-3.6f).setHeightVariation(-2.69999f)), false, true, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " River").setBaseHeight(-3.2f).setHeightVariation(-3f)), false, false, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Beach").setBaseHeight(-2.69f).setHeightVariation(-2.68f)), false, false, BiomeDictionary.Type.BEACH);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Gravel Beach").setBaseHeight(-2.69f).setHeightVariation(-2.68f).setBaseBiome("tfc:beach")), false, false, BiomeDictionary.Type.BEACH);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " High Hills").setBaseHeight(-1.9000001f).setHeightVariation(-1.1f)), false, true, BiomeDictionary.Type.HILLS);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Plains").setBaseHeight(-2.6000001f).setHeightVariation(-2.54f)).setSpawnBiome(), true, true, BiomeDictionary.Type.PLAINS);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Swampland").setBaseHeight(-2.8f).setHeightVariation(-2.6000001f), 16, 45).setSpawnBiome(), true, true, BiomeDictionary.Type.SWAMP);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " High Hills Edge").setBaseHeight(-2.5f).setHeightVariation(-2.3f).setBaseBiome("tfc:high_hills")), false, false, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.PLAINS);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Rolling Hills").setBaseHeight(-2.6000001f).setHeightVariation(-2.3f)).setSpawnBiome(), true, true, BiomeDictionary.Type.HILLS);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Mountains").setBaseHeight(-1.9000001f).setHeightVariation(-1.1f)).setSpawnBiome(), true, true, BiomeDictionary.Type.MOUNTAIN);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Mountains Edge").setBaseHeight(-2.3f).setHeightVariation(-1.9000001f).setBaseBiome("tfc:mountains")), false, false, BiomeDictionary.Type.MOUNTAIN);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " High Plains").setBaseHeight(-2.3f).setHeightVariation(-2.27f)).setSpawnBiome(), true, true, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.PLAINS);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Deep Ocean").setBaseHeight(-4.2f).setHeightVariation(-2.69999f).setBaseBiome("tfc:ocean")), false, false, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
        register(r, new BiomeTFC(new Biome.BiomeProperties(MOD_NAME + " Lake").setBaseHeight(-3.2f).setHeightVariation(-2.6990001f).setBaseBiome("tfc:ocean"), 4, 5), false, false, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
    }

    public static boolean isOceanicBiome(int id)
    {
        return Biome.getIdForBiome(OCEAN) == id || Biome.getIdForBiome(DEEP_OCEAN) == id;
    }

    public static boolean isMountainBiome(int id)
    {
        return Biome.getIdForBiome(MOUNTAINS) == id || Biome.getIdForBiome(MOUNTAINS_EDGE) == id;
    }

    public static boolean isBeachBiome(int id)
    {
        return Biome.getIdForBiome(BEACH) == id || Biome.getIdForBiome(GRAVEL_BEACH) == id;
    }

    public static boolean isOceanicBiome(Biome b)
    {
        return OCEAN == b || DEEP_OCEAN == b;
    }

    public static boolean isMountainBiome(Biome b)
    {
        return MOUNTAINS == b || MOUNTAINS_EDGE == b;
    }

    public static boolean isBeachBiome(Biome b)
    {
        return BEACH == b || GRAVEL_BEACH == b;
    }

    public static List<Biome> getSpawnBiomes()
    {
        return SPAWN_BIOMES;
    }

    public static List<Biome> getWorldGenBiomes()
    {
        return WORLD_GEN_BIOMES;
    }

    private static void register(IForgeRegistry<Biome> r, Biome biome, boolean isSpawn, boolean isWorldGen, BiomeDictionary.Type... types)
    {
        r.register(biome.setRegistryName(MOD_ID, biome.biomeName.replace(MOD_NAME + " ", "").replace(' ', '_').toLowerCase()));

        // Other biome registration stuff
        BiomeDictionary.addTypes(biome, types);

        // These need to happen after the biomes are constructed, otherwise they will be null
        if (isSpawn)
        {
            SPAWN_BIOMES.add(biome);
        }
        if (isWorldGen)
        {
            WORLD_GEN_BIOMES.add(biome);
        }
    }

    private BiomesTFC() {}
}
