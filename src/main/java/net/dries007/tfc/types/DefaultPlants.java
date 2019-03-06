/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.block.material.Material;
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
    public static final ResourceLocation FERN = new ResourceLocation(MOD_ID, "fern");
    public static final ResourceLocation GOLDENROD = new ResourceLocation(MOD_ID, "goldenrod");
    public static final ResourceLocation HOUSTONIA = new ResourceLocation(MOD_ID, "houstonia");
    public static final ResourceLocation LILYPAD = new ResourceLocation(MOD_ID, "lilypad");
    public static final ResourceLocation MEADS_MILKWEED = new ResourceLocation(MOD_ID, "meads_milkweed");
    public static final ResourceLocation MOSS = new ResourceLocation(MOD_ID, "moss");
    public static final ResourceLocation NASTURTIUM = new ResourceLocation(MOD_ID, "nasturtium");
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

    @SubscribeEvent
    public static void onPreRegisterPlant(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(
            // Standard Plants
            new Plant(ALLIUM, -40f, 33f, 150f, 500f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(BLUE_ORCHID, 18f, 50f, 300f, 500f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(BUTTERFLY_MILKWEED, -40f, 32f, 75f, 300f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(CALENDULA, -46f, 30f, 130f, 300f, Plant.PlantType.STANDARD, 9, 15),
            new Plant(DANDELION, -40f, 40f, 75f, 400, Plant.PlantType.STANDARD, 10, 15),
            new Plant(GOLDENROD, -29f, 32f, 75f, 300f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(HOUSTONIA, -46f, 36f, 150f, 500f, Plant.PlantType.STANDARD, 9, 15),
            new Plant(MEADS_MILKWEED, -23f, 31f, 130f, 500f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(NASTURTIUM, -46f, 38f, 150f, 500, Plant.PlantType.STANDARD, 12, 15),
            new Plant(OXEYE_DAISY, -40f, 33f, 120f, 300f, Plant.PlantType.STANDARD, 9, 15),
            new Plant(POPPY, -40f, 36f, 150f, 250f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(TROPICAL_MILKWEED, -6f, 36f, 120f, 300f, Plant.PlantType.STANDARD, 12, 15),
            new Plant(TULIP_ORANGE, -34f, 33f, 100f, 200f, Plant.PlantType.STANDARD, 9, 15),
            new Plant(TULIP_PINK, -34f, 33f, 100f, 200f, Plant.PlantType.STANDARD, 9, 15),
            new Plant(TULIP_RED, -34f, 33f, 100f, 200f, Plant.PlantType.STANDARD, 9, 15),
            new Plant(TULIP_WHITE, -34f, 33f, 100f, 200f, Plant.PlantType.STANDARD, 9, 15),

            // Desert Plants
            new Plant(PETROVSKIA, -29f, 32f, 0f, 200f, Plant.PlantType.DESERT, 12, 15),
            new Plant(SAGEBRUSH, -34f, 50f, 0f, 100f, Plant.PlantType.DESERT, 12, 15),

            // Cactus Plants
            new Plant(CACTUS, Material.CACTUS, -6f, 50f, 0f, 75f, Plant.PlantType.CACTUS, 12, 15),

            // Double Plants
            new Plant(FERN, -40f, 33f, 300f, 500f, Plant.PlantType.DOUBLE, 4, 11),

            // Creeping Plants
            new Plant(BLUE_DAWN, -40f, 25f, 150f, 500f, Plant.PlantType.CREEPING, 12, 15),
            new Plant(MOSS, -7f, 36f, 250f, 500f, Plant.PlantType.CREEPING, 0, 11),

            // Floating Water Plants
            new Plant(LILYPAD, -34f, 38f, 0f, 500f, Plant.PlantType.LILYPAD, 4, 15),

            // Grass
            new Plant(RYEGRASS, Material.VINE, -46f, 32f, 150f, 500f, Plant.PlantType.SHORT_GRASS, 12, 15),
            new Plant(SWITCHGRASS, Material.VINE, -29f, 32f, 100f, 300f, Plant.PlantType.TALL_GRASS, 9, 15)
        );
    }
}
