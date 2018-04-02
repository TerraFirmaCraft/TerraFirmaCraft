package net.dries007.tfc.objects.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class BiomesTFC
{
    private BiomesTFC() {}

    public static final BiomeTFC OCEAN = null;
    public static final BiomeTFC RIVER = null;
    public static final BiomeTFC HELL = null;
    public static final BiomeTFC BEACH = null;
    public static final BiomeTFC GRAVEL_BEACH = null;
    public static final BiomeTFC HIGH_HILLS = null;
    public static final BiomeTFC PLAINS = null;
    public static final BiomeTFC SWAMPLAND = null;
    public static final BiomeTFC HIGH_HILLS_EDGE = null;
    public static final BiomeTFC ROLLING_HILLS = null;
    public static final BiomeTFC MOUNTAINS = null;
    public static final BiomeTFC MOUNTAINS_EDGE = null;
    public static final BiomeTFC HIGH_PLAINS = null;
    public static final BiomeTFC DEEP_OCEAN = null;
    public static final BiomeTFC LAKE = null;

    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Biome> event)
    {
        IForgeRegistry<Biome> r = event.getRegistry();

        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_ocean")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_river")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_hell")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_beach")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_gravel_beach").setBaseBiome(MOD_ID + ":beach")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_high_hills")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_plains")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_swampland")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_high_hills_edge").setBaseBiome(MOD_ID + ":high_hills")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_rolling_hills")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_mountains")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_mountains_edge").setBaseBiome(MOD_ID + ":mountains")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_high_plains")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_deep_ocean").setBaseBiome(MOD_ID + ":ocean")));
        register(r, new BiomeTFC(new Biome.BiomeProperties("tfc_lake").setBaseBiome(MOD_ID + ":ocean")));
    }

    private static void register(IForgeRegistry<Biome> r, Biome item)
    {
        r.register(item.setRegistryName(MOD_ID, item.getBiomeName().toLowerCase().replace("tfc_", "")));
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

    private static BiomeTFC[] PLAYER_SPAWN_BIOMES;
    private static BiomeTFC[] OVERWORLD_GENERATE_BIOMES;

    public static BiomeTFC[] getPlayerSpawnBiomes()
    {
        if (PLAYER_SPAWN_BIOMES == null)
        {
            PLAYER_SPAWN_BIOMES = new BiomeTFC[] {
                    PLAINS,
                    ROLLING_HILLS,
                    SWAMPLAND,
                    MOUNTAINS,
                    HIGH_PLAINS,
            };
        }
        return PLAYER_SPAWN_BIOMES;
    }

    public static BiomeTFC[] getOverworldGenerateBiomes()
    {
        if (OVERWORLD_GENERATE_BIOMES == null)
        {
            OVERWORLD_GENERATE_BIOMES = new BiomeTFC[] {
                    OCEAN,
                    HIGH_HILLS,
                    PLAINS,
                    HIGH_PLAINS,
                    SWAMPLAND,
                    ROLLING_HILLS,
                    MOUNTAINS,
            };
        }
        return OVERWORLD_GENERATE_BIOMES;
    }
}
