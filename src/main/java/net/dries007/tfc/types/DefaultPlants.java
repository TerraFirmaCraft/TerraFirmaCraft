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

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultPlants
{
    /**
     * Default Plant ResourceLocations
     */
    public static final ResourceLocation ALLIUM = new ResourceLocation(MOD_ID, "allium");
    public static final ResourceLocation BLUE_DAWN = new ResourceLocation(MOD_ID, "blue_dawn");
    public static final ResourceLocation BLUE_ORCHID = new ResourceLocation(MOD_ID, "blue_orchid");
    public static final ResourceLocation BUTTERFLY_MILKWEED = new ResourceLocation(MOD_ID, "butterfly_milkweed");
    public static final ResourceLocation CACTUS = new ResourceLocation(MOD_ID, "cactus");
    public static final ResourceLocation CALENDULA = new ResourceLocation(MOD_ID, "calendula");
    public static final ResourceLocation DANDELION = new ResourceLocation(MOD_ID, "dandelion");
    public static final ResourceLocation GOLDENROD = new ResourceLocation(MOD_ID, "goldenrod");
    public static final ResourceLocation HOUSTONIA = new ResourceLocation(MOD_ID, "houstonia");
    public static final ResourceLocation LILYPAD = new ResourceLocation(MOD_ID, "lilypad");
    public static final ResourceLocation MEADS_MILKWEED = new ResourceLocation(MOD_ID, "meads_milkweed");
    public static final ResourceLocation MOSS = new ResourceLocation(MOD_ID, "moss");
    public static final ResourceLocation NASTURTIUM = new ResourceLocation(MOD_ID, "nasturtium");
    public static final ResourceLocation OSTRICH_FERN = new ResourceLocation(MOD_ID, "ostrich_fern");
    public static final ResourceLocation OXEYE_DAISY = new ResourceLocation(MOD_ID, "oxeye_daisy");
    public static final ResourceLocation PETROVSKIA = new ResourceLocation(MOD_ID, "petrovskia");
    public static final ResourceLocation POPPY = new ResourceLocation(MOD_ID, "poppy");
    public static final ResourceLocation RYEGRASS = new ResourceLocation(MOD_ID, "ryegrass");
    public static final ResourceLocation SAGEBRUSH = new ResourceLocation(MOD_ID, "sagebrush");
    public static final ResourceLocation SWITCHGRASS = new ResourceLocation(MOD_ID, "switchgrass");
    public static final ResourceLocation TROPICAL_MILKWEED = new ResourceLocation(MOD_ID, "tropical_milkweed");
    public static final ResourceLocation TULIP_ORANGE = new ResourceLocation(MOD_ID, "tulip_orange");
    public static final ResourceLocation TULIP_PINK = new ResourceLocation(MOD_ID, "tulip_pink");
    public static final ResourceLocation TULIP_RED = new ResourceLocation(MOD_ID, "tulip_red");
    public static final ResourceLocation TULIP_WHITE = new ResourceLocation(MOD_ID, "tulip_white");

    public static final ResourceLocation ATHYRIUM_FERN = new ResourceLocation(MOD_ID, "athyrium_fern");
    public static final ResourceLocation CANNA = new ResourceLocation(MOD_ID, "canna");
    public static final ResourceLocation FOUNTAIN_GRASS = new ResourceLocation(MOD_ID, "fountain_grass");
    public static final ResourceLocation FOXGLOVE = new ResourceLocation(MOD_ID, "foxglove");
    public static final ResourceLocation HORSETAIL = new ResourceLocation(MOD_ID, "horsetail");
    public static final ResourceLocation ORCHARD_GRASS = new ResourceLocation(MOD_ID, "orchard_grass");
    public static final ResourceLocation PAMPAS_GRASS = new ResourceLocation(MOD_ID, "pampas_grass");
    public static final ResourceLocation PRIMROSE = new ResourceLocation(MOD_ID, "primrose");
    public static final ResourceLocation ROSE = new ResourceLocation(MOD_ID, "rose");
    public static final ResourceLocation SACRED_DATURA = new ResourceLocation(MOD_ID, "sacred_datura");
    public static final ResourceLocation SCUTCH_GRASS = new ResourceLocation(MOD_ID, "scutch_grass");
    public static final ResourceLocation SWORD_FERN = new ResourceLocation(MOD_ID, "sword_fern");
    public static final ResourceLocation TALL_FESCUE_GRASS = new ResourceLocation(MOD_ID, "tall_fescue_grass");
    public static final ResourceLocation TIMOTHY_GRASS = new ResourceLocation(MOD_ID, "timothy_grass");
    public static final ResourceLocation TRILLIUM = new ResourceLocation(MOD_ID, "trillium");
    public static final ResourceLocation YUCCA = new ResourceLocation(MOD_ID, "yucca");

    @SubscribeEvent
    public static void onPreRegisterPlant(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(
            // Standard Plants
            new Plant(ALLIUM, Plant.PlantType.STANDARD, false, -40f, 33f, 150f, 500f, 12, 15),
            new Plant(BLUE_ORCHID, Plant.PlantType.STANDARD, false, 18f, 50f, 300f, 500f, 12, 15),
            new Plant(BUTTERFLY_MILKWEED, Plant.PlantType.STANDARD, false, -40f, 32f, 75f, 300f, 12, 15),
            new Plant(CALENDULA, Plant.PlantType.STANDARD, false, -46f, 30f, 130f, 300f, 9, 15),
            new Plant(DANDELION, Plant.PlantType.STANDARD, false, -40f, 40f, 75f, 400, 10, 15),
            new Plant(GOLDENROD, Plant.PlantType.STANDARD, true, -29f, 32f, 75f, 300f, 9, 15),
            new Plant(HOUSTONIA, Plant.PlantType.STANDARD, false, -46f, 36f, 150f, 500f, 9, 15),
            new Plant(MEADS_MILKWEED, Plant.PlantType.STANDARD, false, -23f, 31f, 130f, 500f, 12, 15),
            new Plant(NASTURTIUM, Plant.PlantType.STANDARD, false, -46f, 38f, 150f, 500, 12, 15),
            new Plant(OXEYE_DAISY, Plant.PlantType.STANDARD, false, -40f, 33f, 120f, 300f, 9, 15),
            new Plant(POPPY, Plant.PlantType.STANDARD, false, -40f, 36f, 150f, 250f, 12, 15),
            new Plant(TROPICAL_MILKWEED, Plant.PlantType.STANDARD, false, -6f, 36f, 120f, 300f, 12, 15),
            new Plant(TULIP_ORANGE, Plant.PlantType.STANDARD, false, -34f, 33f, 100f, 200f, 9, 15),
            new Plant(TULIP_PINK, Plant.PlantType.STANDARD, false, -34f, 33f, 100f, 200f, 9, 15),
            new Plant(TULIP_RED, Plant.PlantType.STANDARD, false, -34f, 33f, 100f, 200f, 9, 15),
            new Plant(TULIP_WHITE, Plant.PlantType.STANDARD, false, -34f, 33f, 100f, 200f, 9, 15),

            // Desert Plants
            new Plant(PETROVSKIA, Plant.PlantType.DESERT, true, -29f, 32f, 0f, 200f, 9, 15),
            new Plant(SAGEBRUSH, Plant.PlantType.DESERT, false, -34f, 50f, 0f, 100f, 12, 15),

            // Cactus Plants
            new Plant(CACTUS, Plant.PlantType.CACTUS, false, -6f, 50f, 0f, 75f, 12, 15),

            // Double Plants
            new Plant(OSTRICH_FERN, Plant.PlantType.DOUBLE, false, -40f, 33f, 300f, 500f, 4, 11),

            // Creeping Plants
            new Plant(BLUE_DAWN, Plant.PlantType.CREEPING, false, -40f, 25f, 150f, 500f, 12, 15),
            new Plant(MOSS, Plant.PlantType.CREEPING, false, -7f, 36f, 250f, 500f, 0, 11),

            // Floating Water Plants
            new Plant(LILYPAD, Plant.PlantType.FLOATING, false, -34f, 38f, 0f, 500f, 4, 15, 1, 1),

            // Grass
            new Plant(RYEGRASS, Plant.PlantType.SHORT_GRASS, false, -46f, 32f, 150f, 300f, 12, 15),
            new Plant(SWITCHGRASS, Plant.PlantType.TALL_GRASS, false, -29f, 32f, 100f, 300f, 9, 15),

            // Placeholder plants
            // todo: replace textures/models, add seasonal varieties
            new Plant(ATHYRIUM_FERN, Plant.PlantType.STANDARD, true, -34f, 32f, 200f, 500f, 9, 15),
            new Plant(CANNA, Plant.PlantType.STANDARD, true, -12f, 36f, 150f, 500f, 9, 15),
            new Plant(FOUNTAIN_GRASS, Plant.PlantType.SHORT_GRASS, false, -12f, 40f, 75f, 150f, 12, 15),
            new Plant(FOXGLOVE, Plant.PlantType.STANDARD, false, -34f, 34f, 150f, 300f, 9, 15),
            new Plant(HORSETAIL, Plant.PlantType.STANDARD, false, -40f, 33f, 300f, 500f, 9, 15),
            new Plant(ORCHARD_GRASS, Plant.PlantType.SHORT_GRASS, false, -29f, 30f, 75f, 300f, 9, 15),
            new Plant(PAMPAS_GRASS, Plant.PlantType.TALL_GRASS, true, -12f, 36f, 75f, 200f, 9, 15),
            new Plant(PRIMROSE, Plant.PlantType.STANDARD, false, -34f, 33f, 150f, 300f, 9, 11),
            new Plant(ROSE, Plant.PlantType.DOUBLE, true, -29f, 34f, 150f, 300f, 9, 15),
            new Plant(SACRED_DATURA, Plant.PlantType.STANDARD, false, -12f, 33f, 300f, 500f, 12, 15),
            new Plant(SCUTCH_GRASS, Plant.PlantType.SHORT_GRASS, false, -17f, 40f, 150f, 500f, 12, 15),
            new Plant(SWORD_FERN, Plant.PlantType.STANDARD, false, -40f, 30f, 100f, 200f, 4, 11),
            new Plant(TALL_FESCUE_GRASS, Plant.PlantType.SHORT_GRASS, false, -29f, 30f, 300f, 500f, 12, 15),
            new Plant(TIMOTHY_GRASS, Plant.PlantType.SHORT_GRASS, false, -46f, 30f, 300f, 500f, 12, 15),
            new Plant(TRILLIUM, Plant.PlantType.STANDARD, false, -34f, 33f, 150f, 300f, 4, 11),
            new Plant(YUCCA, Plant.PlantType.DESERT, true, -34f, 36f, 0f, 75, 9, 15)

            );
    }
}
