/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Plant;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultPlants
{
    /**
     * Default Plant ResourceLocations
     */
    public static final ResourceLocation ALLIUM = new ResourceLocation(MOD_ID, "allium");
    public static final ResourceLocation ATHYRIUM_FERN = new ResourceLocation(MOD_ID, "athyrium_fern");
    public static final ResourceLocation BARREL_CACTUS = new ResourceLocation(MOD_ID, "barrel_cactus");
    public static final ResourceLocation BLACK_ORCHID = new ResourceLocation(MOD_ID, "black_orchid");
    public static final ResourceLocation BLOOD_LILY = new ResourceLocation(MOD_ID, "blood_lily");
    public static final ResourceLocation BLUE_ORCHID = new ResourceLocation(MOD_ID, "blue_orchid");
    public static final ResourceLocation BUTTERFLY_MILKWEED = new ResourceLocation(MOD_ID, "butterfly_milkweed");
    public static final ResourceLocation CALENDULA = new ResourceLocation(MOD_ID, "calendula");
    public static final ResourceLocation CANNA = new ResourceLocation(MOD_ID, "canna");
    public static final ResourceLocation DANDELION = new ResourceLocation(MOD_ID, "dandelion");
    public static final ResourceLocation DUCKWEED = new ResourceLocation(MOD_ID, "duckweed");
    public static final ResourceLocation FIELD_HORSETAIL = new ResourceLocation(MOD_ID, "field_horsetail");
    public static final ResourceLocation FOUNTAIN_GRASS = new ResourceLocation(MOD_ID, "fountain_grass");
    public static final ResourceLocation FOXGLOVE = new ResourceLocation(MOD_ID, "foxglove");
    public static final ResourceLocation GOLDENROD = new ResourceLocation(MOD_ID, "goldenrod");
    public static final ResourceLocation GRAPE_HYACINTH = new ResourceLocation(MOD_ID, "grape_hyacinth");
    public static final ResourceLocation GUZMANIA = new ResourceLocation(MOD_ID, "guzmania");
    public static final ResourceLocation HOUSTONIA = new ResourceLocation(MOD_ID, "houstonia");
    public static final ResourceLocation LABRADOR_TEA = new ResourceLocation(MOD_ID, "labrador_tea");
    public static final ResourceLocation LADY_FERN = new ResourceLocation(MOD_ID, "lady_fern");
    public static final ResourceLocation LICORICE_FERN = new ResourceLocation(MOD_ID, "licorice_fern");
    public static final ResourceLocation LOTUS = new ResourceLocation(MOD_ID, "lotus");
    public static final ResourceLocation MEADS_MILKWEED = new ResourceLocation(MOD_ID, "meads_milkweed");
    public static final ResourceLocation MORNING_GLORY = new ResourceLocation(MOD_ID, "morning_glory");
    public static final ResourceLocation MOSS = new ResourceLocation(MOD_ID, "moss");
    public static final ResourceLocation NASTURTIUM = new ResourceLocation(MOD_ID, "nasturtium");
    public static final ResourceLocation ORCHARD_GRASS = new ResourceLocation(MOD_ID, "orchard_grass");
    public static final ResourceLocation OSTRICH_FERN = new ResourceLocation(MOD_ID, "ostrich_fern");
    public static final ResourceLocation OXEYE_DAISY = new ResourceLocation(MOD_ID, "oxeye_daisy");
    public static final ResourceLocation PAMPAS_GRASS = new ResourceLocation(MOD_ID, "pampas_grass");
    public static final ResourceLocation PEROVSKIA = new ResourceLocation(MOD_ID, "perovskia");
    public static final ResourceLocation PISTIA = new ResourceLocation(MOD_ID, "pistia");
    public static final ResourceLocation POPPY = new ResourceLocation(MOD_ID, "poppy");
    public static final ResourceLocation PORCINI = new ResourceLocation(MOD_ID, "porcini");
    public static final ResourceLocation PRIMROSE = new ResourceLocation(MOD_ID, "primrose");
    public static final ResourceLocation PULSATILLA = new ResourceLocation(MOD_ID, "pulsatilla");
    public static final ResourceLocation REINDEER_LICHEN = new ResourceLocation(MOD_ID, "reindeer_lichen");
    public static final ResourceLocation ROSE = new ResourceLocation(MOD_ID, "rose");
    public static final ResourceLocation ROUGH_HORSETAIL = new ResourceLocation(MOD_ID, "rough_horsetail");
    public static final ResourceLocation RYEGRASS = new ResourceLocation(MOD_ID, "ryegrass");
    public static final ResourceLocation SACRED_DATURA = new ResourceLocation(MOD_ID, "sacred_datura");
    public static final ResourceLocation SAGEBRUSH = new ResourceLocation(MOD_ID, "sagebrush");
    public static final ResourceLocation SAPPHIRE_TOWER = new ResourceLocation(MOD_ID, "sapphire_tower");
    public static final ResourceLocation SARGASSUM = new ResourceLocation(MOD_ID, "sargassum");
    public static final ResourceLocation SCUTCH_GRASS = new ResourceLocation(MOD_ID, "scutch_grass");
    public static final ResourceLocation SNAPDRAGON_PINK = new ResourceLocation(MOD_ID, "snapdragon_pink");
    public static final ResourceLocation SNAPDRAGON_RED = new ResourceLocation(MOD_ID, "snapdragon_red");
    public static final ResourceLocation SNAPDRAGON_WHITE = new ResourceLocation(MOD_ID, "snapdragon_white");
    public static final ResourceLocation SNAPDRAGON_YELLOW = new ResourceLocation(MOD_ID, "snapdragon_yellow");
    public static final ResourceLocation SPANISH_MOSS = new ResourceLocation(MOD_ID, "spanish_moss");
    public static final ResourceLocation STRELITZIA = new ResourceLocation(MOD_ID, "strelitzia");
    public static final ResourceLocation SWITCHGRASS = new ResourceLocation(MOD_ID, "switchgrass");
    public static final ResourceLocation SWORD_FERN = new ResourceLocation(MOD_ID, "sword_fern");
    public static final ResourceLocation TALL_FESCUE_GRASS = new ResourceLocation(MOD_ID, "tall_fescue_grass");
    public static final ResourceLocation TIMOTHY_GRASS = new ResourceLocation(MOD_ID, "timothy_grass");
    public static final ResourceLocation TOQUILLA_PALM = new ResourceLocation(MOD_ID, "toquilla_palm");
    public static final ResourceLocation TREE_FERN = new ResourceLocation(MOD_ID, "tree_fern");
    public static final ResourceLocation TRILLIUM = new ResourceLocation(MOD_ID, "trillium");
    public static final ResourceLocation TROPICAL_MILKWEED = new ResourceLocation(MOD_ID, "tropical_milkweed");
    public static final ResourceLocation TULIP_ORANGE = new ResourceLocation(MOD_ID, "tulip_orange");
    public static final ResourceLocation TULIP_PINK = new ResourceLocation(MOD_ID, "tulip_pink");
    public static final ResourceLocation TULIP_RED = new ResourceLocation(MOD_ID, "tulip_red");
    public static final ResourceLocation TULIP_WHITE = new ResourceLocation(MOD_ID, "tulip_white");
    public static final ResourceLocation VRIESEA = new ResourceLocation(MOD_ID, "vriesea");
    public static final ResourceLocation WATER_CANNA = new ResourceLocation(MOD_ID, "water_canna");
    public static final ResourceLocation WATER_LILY = new ResourceLocation(MOD_ID, "water_lily");
    public static final ResourceLocation YUCCA = new ResourceLocation(MOD_ID, "yucca");

    @SubscribeEvent
    public static void onPreRegisterPlant(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(
            new Plant(ALLIUM, Plant.PlantType.STANDARD, new int[] {6, 6, 7, 0, 1, 1, 2, 2, 3, 4, 5, 6}, false, false, 8f, 20f, -40f, 33f, 150f, 500f, 12, 15, 1, 0.8D, null),
            new Plant(ATHYRIUM_FERN, Plant.PlantType.STANDARD, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true, false, 13f, 25f, -35f, 31f, 200f, 500f, 9, 15, 1, 0.8D, null),
            new Plant(BARREL_CACTUS, Plant.PlantType.CACTUS, new int[] {0, 0, 0, 0, 1, 2, 2, 2, 2, 3, 3, 0}, false, false, 18f, 40f, -6f, 50f, 0f, 75f, 12, 15, 3, 0D, "blockCactus"),
            new Plant(BLACK_ORCHID, Plant.PlantType.STANDARD, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}, false, false, 20f, 35f, 10f, 50f, 300f, 500f, 12, 15, 1, 0.8D, null),
            new Plant(BLOOD_LILY, Plant.PlantType.STANDARD, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}, false, false, 18f, 30f, 10f, 50f, 200f, 500f, 9, 15, 1, 0.9D, null),
            new Plant(BLUE_ORCHID, Plant.PlantType.STANDARD, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}, false, false, 20f, 35f, 10f, 50f, 300f, 500f, 12, 15, 1, 0.9D, null),
            new Plant(BUTTERFLY_MILKWEED, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}, false, false, 18f, 24f, -40f, 32f, 75f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(CALENDULA, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}, false, false, 15f, 20f, -46f, 30f, 130f, 300f, 9, 15, 1, 0.8D, null),
            new Plant(CANNA, Plant.PlantType.STANDARD, new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 0}, true, false, 18f, 30f, -12f, 36f, 150f, 500f, 9, 15, 1, 0.8D, null),
            new Plant(DANDELION, Plant.PlantType.STANDARD, new int[] {9, 9, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8}, false, false, 10f, 25f, -40f, 40f, 75f, 400f, 10, 15, 1, 0.9D, null),
            new Plant(DUCKWEED, Plant.PlantType.FLOATING, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 11f, 20f, -34f, 38f, 0f, 500f, 4, 15, 1, 2, 32, 0.8D, null),
            new Plant(FIELD_HORSETAIL, Plant.PlantType.STANDARD, new int[] {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1}, false, false, 5f, 20f, -40f, 33f, 300f, 500f, 9, 15, 1, 0.7D, "reed"),
            new Plant(FOUNTAIN_GRASS, Plant.PlantType.SHORT_GRASS, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 15f, 35f, -12f, 40f, 75f, 150f, 12, 15, 1, 0.8D, null),
            new Plant(FOXGLOVE, Plant.PlantType.TALL_PLANT, new int[] {0, 0, 0, 0, 0, 1, 1, 2, 3, 3, 3, 4}, false, false, 15f, 25f, -34f, 34f, 150f, 300f, 9, 15, 1, 0.8D, null),
            new Plant(GOLDENROD, Plant.PlantType.STANDARD, new int[] {4, 4, 4, 0, 0, 0, 1, 2, 2, 2, 2, 3}, true, false, 15f, 23f, -29f, 32f, 75f, 300f, 9, 15, 1, 0.6D, null),
            new Plant(GRAPE_HYACINTH, Plant.PlantType.STANDARD, new int[] {3, 3, 3, 0, 1, 1, 2, 3, 3, 3, 3, 3}, false, false, 4f, 18f, -34f, 32f, 150f, 250f, 9, 15, 1, 0.8D, null),
            new Plant(GUZMANIA, Plant.PlantType.EPIPHYTE, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 0f, 40f, 15f, 50f, 300f, 500f, 4, 11, 1, 0.9D, null),
            new Plant(HOUSTONIA, Plant.PlantType.STANDARD, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}, false, false, 15f, 30f, -46f, 36f, 150f, 500f, 9, 15, 1, 0.9D, null),
            new Plant(LABRADOR_TEA, Plant.PlantType.STANDARD, new int[] {0, 0, 1, 2, 3, 4, 4, 5, 6, 0, 0, 0}, false, false, 1f, 25f, -50f, 33f, 300f, 500f, 10, 15, 1, 0.8D, null),
            new Plant(LADY_FERN, Plant.PlantType.STANDARD, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 13f, 25f, -34f, 32f, 200f, 500f, 9, 11, 1, 0.6D, null),
            new Plant(LICORICE_FERN, Plant.PlantType.EPIPHYTE, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 5f, 18f, -29f, 25f, 300f, 500f, 4, 11, 1, 0.7D, null),
            new Plant(LOTUS, Plant.PlantType.FLOATING, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 0, 0}, false, false, 20f, 40f, 10f, 50f, 0f, 500f, 4, 15, 1, 1, 1, 0.9D, null),
            new Plant(MEADS_MILKWEED, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}, false, false, 13f, 25f, -23f, 31f, 130f, 500f, 12, 15, 1, 0.8D, null),
            new Plant(MORNING_GLORY, Plant.PlantType.CREEPING, new int[] {2, 2, 2, 0, 0, 1, 1, 1, 1, 1, 2, 2}, false, false, 15f, 30f, -40f, 31f, 150f, 500f, 12, 15, 1, 0.9D, null),
            new Plant(MOSS, Plant.PlantType.CREEPING, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, -2f, 15f, -7f, 36f, 250f, 500f, 0, 11, 1, 0.7D, null),
            new Plant(NASTURTIUM, Plant.PlantType.STANDARD, new int[] {4, 4, 4, 0, 1, 2, 2, 2, 2, 2, 3, 3}, false, false, 18f, 30f, -46f, 38f, 150f, 500f, 12, 15, 1, 0.8D, null),
            new Plant(ORCHARD_GRASS, Plant.PlantType.SHORT_GRASS, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 13f, 20f, -29f, 30f, 75f, 300f, 9, 15, 1, 0.8D, null),
            new Plant(OSTRICH_FERN, Plant.PlantType.TALL_PLANT, new int[] {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 0}, false, false, 10f, 18f, -40f, 33f, 300f, 500f, 4, 11, 2, 0.6D, null),
            new Plant(OXEYE_DAISY, Plant.PlantType.STANDARD, new int[] {5, 5, 5, 0, 1, 2, 3, 3, 3, 4, 4, 5}, false, false, 18f, 30f, -40f, 33f, 120f, 300f, 9, 15, 1, 0.9D, null),
            new Plant(PAMPAS_GRASS, Plant.PlantType.TALL_GRASS, new int[] {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1}, true, false, 20f, 30f, -12f, 36f, 75f, 200f, 9, 15, 3, 0.6D, null),
            new Plant(PEROVSKIA, Plant.PlantType.DRY, new int[] {5, 5, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4}, true, false, 18f, 35f, -29f, 32f, 0f, 200f, 9, 15, 1, 0.8D, null),
            new Plant(PISTIA, Plant.PlantType.FLOATING, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 20f, 40f, 10f, 50f, 0f, 500f, 4, 15, 1, 2, 32, 0.8D, null),
            new Plant(POPPY, Plant.PlantType.STANDARD, new int[] {4, 4, 4, 0, 1, 2, 2, 3, 3, 3, 3, 4}, false, false, 17f, 30f, -40f, 36f, 150f, 250f, 12, 15, 1, 0.9D, null),
            new Plant(PORCINI, Plant.PlantType.MUSHROOM, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 13f, 20f, 0f, 30f, 300f, 500f, 0, 12, 1, 0.8D, "mushroomBrown"),
            new Plant(PRIMROSE, Plant.PlantType.STANDARD, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2}, false, false, 15f, 25f, -34f, 33f, 150f, 300f, 9, 11, 1, 0.9D, null),
            new Plant(PULSATILLA, Plant.PlantType.STANDARD, new int[] {0, 1, 2, 3, 3, 4, 5, 5, 5, 0, 0, 0}, false, false, 1f, 25f, -50f, 30f, 50f, 200f, 12, 15, 1, 0.8D, null),
            new Plant(REINDEER_LICHEN, Plant.PlantType.CREEPING, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 1f, 25f, -50f, 33f, 50f, 500f, 9, 15, 1, 0.7D, null),
            new Plant(ROSE, Plant.PlantType.TALL_PLANT, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true, false, 11f, 21f, -29f, 34f, 150f, 300f, 9, 15, 2, 0.9D, null),
            new Plant(ROUGH_HORSETAIL, Plant.PlantType.REED, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 5f, 20f, -40f, 33f, 200f, 500f, 9, 15, 1, 0.7D, "reed"),
            new Plant(RYEGRASS, Plant.PlantType.SHORT_GRASS, new int[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, false, false, 10f, 18f, -46f, 32f, 150f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(SACRED_DATURA, Plant.PlantType.STANDARD, new int[] {3, 3, 3, 0, 1, 2, 2, 2, 2, 2, 2, 2}, false, false, 20f, 30f, 5f, 33f, 75f, 150f, 12, 15, 1, 0.8D, null),
            new Plant(SAGEBRUSH, Plant.PlantType.DRY, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0}, false, false, 18f, 35f, -34f, 50f, 0f, 100f, 12, 15, 1, 0.5D, null),
            new Plant(SAPPHIRE_TOWER, Plant.PlantType.TALL_PLANT, new int[] {2, 3, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}, false, false, 21f, 31f, -6f, 38f, 75f, 200f, 9, 15, 2, 0.6D, null),
            new Plant(SARGASSUM, Plant.PlantType.FLOATING_SEA, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 18f, 30f, 0f, 38f, 0f, 500f, 12, 15, 1, 5, 256, 0.9D, "seaweed"),
            new Plant(SCUTCH_GRASS, Plant.PlantType.SHORT_GRASS, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 18f, 35f, -17f, 50f, 150f, 500f, 12, 15, 1, 0.7D, null),
            new Plant(SNAPDRAGON_PINK, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}, false, false, 15f, 25f, -28f, 36f, 150f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(SNAPDRAGON_RED, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}, false, false, 15f, 25f, -28f, 36f, 150f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(SNAPDRAGON_WHITE, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}, false, false, 15f, 25f, -28f, 36f, 150f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(SNAPDRAGON_YELLOW, Plant.PlantType.STANDARD, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}, false, false, 15f, 25f, -28f, 36f, 150f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(SPANISH_MOSS, Plant.PlantType.HANGING, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, true, 20f, 40f, 0f, 40f, 300f, 500f, 4, 15, 3, 0.7D, null),
            new Plant(STRELITZIA, Plant.PlantType.STANDARD, new int[] {0, 0, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2}, false, false, 20f, 40f, 5f, 50f, 50f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(SWITCHGRASS, Plant.PlantType.TALL_GRASS, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0}, false, false, 13f, 25f, -29f, 32f, 100f, 300f, 9, 15, 2, 0.7D, null),
            new Plant(SWORD_FERN, Plant.PlantType.STANDARD, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 18f, 25f, -40f, 30f, 100f, 500f, 4, 11, 1, 0.7D, null),
            new Plant(TALL_FESCUE_GRASS, Plant.PlantType.TALL_GRASS, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 15f, 25f, -29f, 30f, 300f, 500f, 12, 15, 2, 0.5D, null),
            new Plant(TIMOTHY_GRASS, Plant.PlantType.SHORT_GRASS, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 15f, 25f, -46f, 30f, 300f, 500f, 12, 15, 1, 0.8D, null),
            new Plant(TOQUILLA_PALM, Plant.PlantType.TALL_PLANT, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 20f, 40f, 10f, 50f, 250f, 500f, 9, 15, 2, 0.4D, null),
            new Plant(TREE_FERN, Plant.PlantType.TALL_PLANT, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, false, false, 20f, 40f, 10f, 50f, 300f, 500f, 9, 15, 4, 0D, null),
            new Plant(TRILLIUM, Plant.PlantType.STANDARD, new int[] {5, 5, 5, 0, 1, 2, 3, 3, 4, 4, 4, 4}, false, false, 15f, 25f, -34f, 33f, 150f, 300f, 4, 11, 1, 0.8D, null),
            new Plant(TROPICAL_MILKWEED, Plant.PlantType.STANDARD, new int[] {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 0}, false, false, 20f, 35f, -6f, 36f, 120f, 300f, 12, 15, 1, 0.8D, null),
            new Plant(TULIP_ORANGE, Plant.PlantType.STANDARD, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}, false, false, 15f, 25f, -34f, 33f, 100f, 200f, 9, 15, 1, 0.9D, null),
            new Plant(TULIP_PINK, Plant.PlantType.STANDARD, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}, false, false, 15f, 25f, -34f, 33f, 100f, 200f, 9, 15, 1, 0.9D, null),
            new Plant(TULIP_RED, Plant.PlantType.STANDARD, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}, false, false, 15f, 25f, -34f, 33f, 100f, 200f, 9, 15, 1, 0.9D, null),
            new Plant(TULIP_WHITE, Plant.PlantType.STANDARD, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}, false, false, 15f, 25f, -34f, 33f, 100f, 200f, 9, 15, 1, 0.9D, null),
            new Plant(VRIESEA, Plant.PlantType.EPIPHYTE, new int[] {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}, false, false, 0f, 40f, 15f, 50f, 300f, 500f, 4, 11, 1, 0.8D, null),
            new Plant(WATER_CANNA, Plant.PlantType.FLOATING, new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 0}, true, false, 18f, 30f, -12f, 36f, 150f, 500f, 9, 15, 1, 1, 1, 0.8D, null),
            new Plant(WATER_LILY, Plant.PlantType.FLOATING, new int[] {5, 5, 6, 0, 1, 2, 2, 2, 2, 3, 4, 5}, false, false, 15f, 30f, -34f, 38f, 0f, 500f, 4, 15, 1, 1, 1, 0.8D, null),
            new Plant(YUCCA, Plant.PlantType.DESERT, new int[] {0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 3}, false, false, 20f, 30f, -34f, 36f, 0f, 75f, 9, 15, 1, 0.8D, null)
        );
    }
}
