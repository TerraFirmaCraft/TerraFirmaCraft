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

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultPlants
{
    /**
     * Default Plant ResourceLocations
     */
    public static final ResourceLocation DANDELION = new ResourceLocation(MOD_ID, "dandelion");
    public static final ResourceLocation NASTURTIUM = new ResourceLocation(MOD_ID, "nasturtium");
    public static final ResourceLocation MEADS_MILKWEED = new ResourceLocation(MOD_ID, "meads_milkweed");
    public static final ResourceLocation TROPICAL_MILKWEED = new ResourceLocation(MOD_ID, "tropical_milkweed");
    public static final ResourceLocation BUTTERFLY_MILKWEED = new ResourceLocation(MOD_ID, "butterfly_milkweed");
    public static final ResourceLocation CALENDULA = new ResourceLocation(MOD_ID, "calendula");
    public static final ResourceLocation POPPY = new ResourceLocation(MOD_ID, "poppy");
    public static final ResourceLocation BLUE_ORCHID = new ResourceLocation(MOD_ID, "blue_orchid");
    public static final ResourceLocation ALLIUM = new ResourceLocation(MOD_ID, "allium");
    public static final ResourceLocation HOUSTONIA = new ResourceLocation(MOD_ID, "houstonia");
    public static final ResourceLocation TULIP_RED = new ResourceLocation(MOD_ID, "tulip_red");
    public static final ResourceLocation TULIP_ORANGE = new ResourceLocation(MOD_ID, "tulip_orange");
    public static final ResourceLocation TULIP_WHITE = new ResourceLocation(MOD_ID, "tulip_white");
    public static final ResourceLocation TULIP_PINK = new ResourceLocation(MOD_ID, "tulip_pink");
    public static final ResourceLocation OXEYE_DAISY = new ResourceLocation(MOD_ID, "oxeye_daisy");
    //    public static final ResourceLocation PAEONIA = new ResourceLocation(MOD_ID, "paeonia");
    public static final ResourceLocation GOLDENROD = new ResourceLocation(MOD_ID, "goldenrod");
    public static final ResourceLocation FERN = new ResourceLocation(MOD_ID, "fern");
    public static final ResourceLocation PETROVSKIA = new ResourceLocation(MOD_ID, "petrovskia");

    public static final ResourceLocation DOUBLE_FERN = new ResourceLocation(MOD_ID, "double_fern");
//    public static final ResourceLocation DOUBLE_PAEONIA = new ResourceLocation(MOD_ID, "double_paeonia");

    public static final ResourceLocation MOSS = new ResourceLocation(MOD_ID, "moss");
    public static final ResourceLocation BLUE_DAWN = new ResourceLocation(MOD_ID, "blue_dawn");

    @SubscribeEvent
    public static void onPreRegisterPlantCategory(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(
            new Plant(DANDELION, -40f, 40f, 75f, 500, Plant.PlantType.PLANT),
            new Plant(NASTURTIUM, -46f, 38f, 150f, 500, Plant.PlantType.PLANT),
            new Plant(MEADS_MILKWEED, -23f, 31f, 130f, 500f, Plant.PlantType.PLANT),
            new Plant(TROPICAL_MILKWEED, -6f, 36f, 120f, 300f, Plant.PlantType.PLANT),
            new Plant(BUTTERFLY_MILKWEED, -40f, 32f, 75f, 300f, Plant.PlantType.PLANT),
            new Plant(CALENDULA, -46f, 30f, 130f, 300f, Plant.PlantType.PLANT),
            new Plant(POPPY, -40f, 36f, 150f, 250f, Plant.PlantType.PLANT),
            new Plant(BLUE_ORCHID, 18f, 50f, 300f, 10000f, Plant.PlantType.PLANT),
            new Plant(ALLIUM, -40f, 33f, 150f, 500f, Plant.PlantType.PLANT),
            new Plant(HOUSTONIA, -46f, 36f, 150f, 500f, Plant.PlantType.PLANT),
            new Plant(TULIP_RED, -34f, 33f, 100f, 200f, Plant.PlantType.PLANT),
            new Plant(TULIP_ORANGE, -34f, 33f, 100f, 200f, Plant.PlantType.PLANT),
            new Plant(TULIP_WHITE, -34f, 33f, 100f, 200f, Plant.PlantType.PLANT),
            new Plant(TULIP_PINK, -34f, 33f, 100f, 200f, Plant.PlantType.PLANT),
            new Plant(OXEYE_DAISY, -40f, 33f, 120f, 500f, Plant.PlantType.PLANT),
//            new Plant(PAEONIA, -40f, 33f, 150f, 500f, Plant.PlantType.PLANT),
            new Plant(GOLDENROD, -29f, 32f, 75f, 300f, Plant.PlantType.PLANT),
            new Plant(FERN, -40f, 33f, 300f, 10000f, Plant.PlantType.PLANT),
            new Plant(PETROVSKIA, -29f, 32f, 0f, 200f, Plant.PlantType.PLANT),

//            new Plant(DOUBLE_PAEONIA, -40f, 33f, 150f, 500f, Plant.PlantType.DOUBLEPLANT),
            new Plant(DOUBLE_FERN, -40f, 33f, 300f, 10000f, Plant.PlantType.DOUBLEPLANT),

            new Plant(MOSS, -7f, 36f, 0f, 10000f, Plant.PlantType.CREEPINGPLANT),
            new Plant(BLUE_DAWN, -40f, 25f, 250f, 500f, Plant.PlantType.CREEPINGPLANT)

        );
    }
}
