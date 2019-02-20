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
    public static final ResourceLocation FLOWER_DANDELION = new ResourceLocation(MOD_ID, "dandelion");
    public static final ResourceLocation FLOWER_NASTURTIUM = new ResourceLocation(MOD_ID, "nasturtium");
    public static final ResourceLocation FLOWER_MEADS_MILKWEED = new ResourceLocation(MOD_ID, "meads_milkweed");
    public static final ResourceLocation FLOWER_TROPICAL_MILKWEED = new ResourceLocation(MOD_ID, "tropical_milkweed");
    public static final ResourceLocation FLOWER_BUTTERFLY_MILKWEED = new ResourceLocation(MOD_ID, "butterfly_milkweed");
    public static final ResourceLocation FLOWER_CALENDULA = new ResourceLocation(MOD_ID, "calendula");
    public static final ResourceLocation FLOWER_POPPY = new ResourceLocation(MOD_ID, "poppy");
    public static final ResourceLocation FLOWER_BLUE_ORCHID = new ResourceLocation(MOD_ID, "blue_orchid");
    public static final ResourceLocation FLOWER_ALLIUM = new ResourceLocation(MOD_ID, "allium");
    public static final ResourceLocation FLOWER_HOUSTONIA = new ResourceLocation(MOD_ID, "houstonia");
    public static final ResourceLocation FLOWER_TULIP_RED = new ResourceLocation(MOD_ID, "tulip_red");
    public static final ResourceLocation FLOWER_TULIP_ORANGE = new ResourceLocation(MOD_ID, "tulip_orange");
    public static final ResourceLocation FLOWER_TULIP_WHITE = new ResourceLocation(MOD_ID, "tulip_white");
    public static final ResourceLocation FLOWER_TULIP_PINK = new ResourceLocation(MOD_ID, "tulip_pink");
    public static final ResourceLocation FLOWER_OXEYE_DAISY = new ResourceLocation(MOD_ID, "oxeye_daisy");
    public static final ResourceLocation FLOWER_PAEONIA = new ResourceLocation(MOD_ID, "paeonia");
    public static final ResourceLocation FLOWER_GOLDENROD = new ResourceLocation(MOD_ID, "goldenrod");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistryEvent.RegisterPreBlock<Plant> event)
    {
        event.getRegistry().registerAll(
            new Plant.Builder(FLOWER_DANDELION, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_NASTURTIUM, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_MEADS_MILKWEED, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_TROPICAL_MILKWEED, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_BUTTERFLY_MILKWEED, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_CALENDULA, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_POPPY, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_BLUE_ORCHID, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_ALLIUM, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_HOUSTONIA, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_TULIP_RED, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_TULIP_ORANGE, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_TULIP_WHITE, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_TULIP_PINK, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_OXEYE_DAISY, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_PAEONIA, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new Plant.Builder(FLOWER_GOLDENROD, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build()
        );
    }
}
