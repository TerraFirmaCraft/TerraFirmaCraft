/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.TerraFirmaCraft.MOD_NAME;

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

        register(r, new BiomeTFC(0x3232C8, new Biome.BiomeProperties(MOD_NAME + " Ocean").setBaseHeight(-2.6f).setHeightVariation(-2.69999f)), false, true, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
        register(r, new BiomeTFC(0x2B8CBA, new Biome.BiomeProperties(MOD_NAME + " River").setBaseHeight(-2.3f).setHeightVariation(-3f)), false, false, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
        register(r, new BiomeTFC(0xC7A03B, new Biome.BiomeProperties(MOD_NAME + " Beach").setBaseHeight(-1.69f).setHeightVariation(-2.68f)), false, false, BiomeDictionary.Type.BEACH);
        register(r, new BiomeTFC(0x7E7450, new Biome.BiomeProperties(MOD_NAME + " Gravel Beach").setBaseHeight(-1.69f).setHeightVariation(-2.68f).setBaseBiome("tfc:beach")), false, false, BiomeDictionary.Type.BEACH);
        register(r, new BiomeTFC(0x920072, new Biome.BiomeProperties(MOD_NAME + " High Hills").setBaseHeight(-0.9000001f).setHeightVariation(-1.1f)), false, true, BiomeDictionary.Type.HILLS);
        register(r, new BiomeTFC(0x346B25, new Biome.BiomeProperties(MOD_NAME + " Plains").setBaseHeight(-1.6000001f).setHeightVariation(-2.54f)).setSpawnBiome(), true, true, BiomeDictionary.Type.PLAINS);
        register(r, new BiomeTFC(0x099200, new Biome.BiomeProperties(MOD_NAME + " Swampland").setBaseHeight(-1.8f).setHeightVariation(-2.6000001f), 16, 45).setSpawnBiome(), true, true, BiomeDictionary.Type.SWAMP);
        register(r, new BiomeTFC(0x92567C, new Biome.BiomeProperties(MOD_NAME + " High Hills Edge").setBaseHeight(-1.5f).setHeightVariation(-2.3f).setBaseBiome("tfc:high_hills")), false, false, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.PLAINS);
        register(r, new BiomeTFC(0x734B92, new Biome.BiomeProperties(MOD_NAME + " Rolling Hills").setBaseHeight(-1.6000001f).setHeightVariation(-2.3f)).setSpawnBiome(), true, true, BiomeDictionary.Type.HILLS);
        register(r, new BiomeTFC(0x920000, new Biome.BiomeProperties(MOD_NAME + " Mountains").setBaseHeight(-0.9000001f).setHeightVariation(-1.1f)).setSpawnBiome(), true, true, BiomeDictionary.Type.MOUNTAIN);
        register(r, new BiomeTFC(0x924A4C, new Biome.BiomeProperties(MOD_NAME + " Mountains Edge").setBaseHeight(-1.3f).setHeightVariation(-1.9000001f).setBaseBiome("tfc:mountains")).setSpawnBiome(), true, false, BiomeDictionary.Type.MOUNTAIN);
        register(r, new BiomeTFC(0x225031, new Biome.BiomeProperties(MOD_NAME + " High Plains").setBaseHeight(-1.3f).setHeightVariation(-2.27f)).setSpawnBiome(), true, true, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.PLAINS);
        register(r, new BiomeTFC(0x000080, new Biome.BiomeProperties(MOD_NAME + " Deep Ocean").setBaseHeight(-3.2f).setHeightVariation(-2.49999f).setBaseBiome("tfc:ocean")), false, false, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
        register(r, new BiomeTFC(0x5D8C8D, new Biome.BiomeProperties(MOD_NAME + " Lake").setBaseHeight(-2.4f).setHeightVariation(-2.5990001f).setBaseBiome("tfc:ocean"), 4, 5), false, false, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.WET, BiomeDictionary.Type.WATER);
    }

    public static boolean isOceanicBiome(Biome b)
    {
        return OCEAN == b || DEEP_OCEAN == b;
    }

    public static boolean isRiverBiome(Biome b)
    {
        return RIVER == b;
    }

    public static boolean isLakeBiome(Biome b)
    {
        return LAKE == b;
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

        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER))
        {
            // Register aquatic creatures
            biome.getSpawnableList(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(EntitySquid.class, 20, 3, 7));
            // todo add fish (either in 1.15+ or if someone makes fish entities)
        }
    }

    private BiomesTFC() {}
}
