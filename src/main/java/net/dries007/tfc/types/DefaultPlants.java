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

    @SubscribeEvent
    public static void onPreRegisterPlantCategory(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(
            new Plant.Builder(DANDELION, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(NASTURTIUM, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(MEADS_MILKWEED, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(TROPICAL_MILKWEED, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(BUTTERFLY_MILKWEED, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(CALENDULA, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(POPPY, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(BLUE_ORCHID, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(ALLIUM, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(HOUSTONIA, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(TULIP_RED, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(TULIP_ORANGE, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(TULIP_WHITE, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(TULIP_PINK, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(OXEYE_DAISY, 0f, 500f, 0f, 40f).build(),
//            new Plant.Builder(PAEONIA, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(GOLDENROD, 0f, 500f, 0f, 40f).build(),
            new Plant.Builder(FERN, 200f, 500f, 20f, 40f).build()
        );
    }
}
