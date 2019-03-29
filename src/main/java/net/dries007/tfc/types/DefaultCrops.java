/*
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Crop;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)

public class DefaultCrops
{
    /**
     * Default crop ResourceLocations
     */
    public static final ResourceLocation BARLEY = new ResourceLocation(MOD_ID, "barley");
    public static final ResourceLocation MAIZE = new ResourceLocation(MOD_ID, "maize");
    public static final ResourceLocation OAT = new ResourceLocation(MOD_ID, "oat");
    public static final ResourceLocation RICE = new ResourceLocation(MOD_ID, "rice");
    public static final ResourceLocation RYE = new ResourceLocation(MOD_ID, "rye");
    public static final ResourceLocation WHEAT = new ResourceLocation(MOD_ID, "wheat");

    public static final ResourceLocation BEET = new ResourceLocation(MOD_ID, "beet");
    public static final ResourceLocation CABBAGE = new ResourceLocation(MOD_ID, "cabbage");
    public static final ResourceLocation CARROT = new ResourceLocation(MOD_ID, "carrot");
    public static final ResourceLocation GARLIC = new ResourceLocation(MOD_ID, "garlic");
    public static final ResourceLocation GREENBEAN = new ResourceLocation(MOD_ID, "greenbean");
    public static final ResourceLocation ONION = new ResourceLocation(MOD_ID, "onion");
    public static final ResourceLocation POTATO = new ResourceLocation(MOD_ID, "potato");
    public static final ResourceLocation SOYBEAN = new ResourceLocation(MOD_ID, "soybean");
    public static final ResourceLocation SQUASH = new ResourceLocation(MOD_ID, "squash");
    public static final ResourceLocation SUGARCANE = new ResourceLocation(MOD_ID, "sugarcane");
    public static final ResourceLocation REDBELLPEPPER = new ResourceLocation(MOD_ID, "redbellpepper");
    public static final ResourceLocation TOMATO = new ResourceLocation(MOD_ID, "tomato");
    public static final ResourceLocation YELLOWBELLPEPPER = new ResourceLocation(MOD_ID, "yellowbellpepper");

    public static final ResourceLocation JUTE = new ResourceLocation(MOD_ID, "jute");

    @SubscribeEvent
    public static void onPreRegisterCrop(TFCRegistryEvent.RegisterPreBlock<Crop> event) //todo Set temperatures, rainfall and lifespan of crops
    {
        event.getRegistry().registerAll(
            new Crop.Builder(BARLEY, 0f, 40f, 4f, 35f, 20, 210,7, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.BARLEY, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(MAIZE, 0f, 40f, 8f, 35f, 20, 210,5, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.MAIZE, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(OAT, 0f, 40f, 4f, 35f, 20, 210,7, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.OAT, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(RICE, 0f, 40f, 4f, 35f, 20, 210,7, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.RICE, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(RYE, 0f, 40f, 4f, 35f, 20, 210,7, (ConfigTFC.GENERAL.monthLength / 2), false,6,  DefaultFood.RYE, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(WHEAT, 0f, 40f, 4f, 35f, 20, 210,7, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.WHEAT, null).setDensity(0.1f, 0.6f).build(),

            new Crop.Builder(BEET, 0f, 40f, 4f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.BEET, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(CABBAGE, 0f, 40f, 10f, 35f, 20, 210,5, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.CABBAGE, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(CARROT, 0f, 40f, 8f, 35f, 20, 210,4, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.CARROT, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(GARLIC, 0f, 40f, 8f, 35f, 20, 210,4, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.GARLIC, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(GREENBEAN, 0f, 40f, 8f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.GREENBEAN, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(ONION, 0f, 40f, 8f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), false,6,  DefaultFood.ONION, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(POTATO, 0f, 40f, 4f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.POTATO, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(SOYBEAN, 0f, 40f, 8f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.SOYBEAN, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(SQUASH, 0f, 40f, 8f, 35f, 20, 210, 6, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.SQUASH, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(SUGARCANE, 12f, 40f, 18f, 35f, 20, 210, 7, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.SUGARCANE, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(REDBELLPEPPER, 4f, 40f, 12f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.REDBELLPEPPER, DefaultFood.GREENBELLPEPPER).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(TOMATO, 0f, 40f, 8f, 35f, 20, 210,7, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.TOMATO, null).setDensity(0.1f, 0.6f).build(),
            new Crop.Builder(YELLOWBELLPEPPER, 4f, 40f, 12f, 35f, 20, 210,6, (ConfigTFC.GENERAL.monthLength / 2), true,6, DefaultFood.YELLOWBELLPEPPER, DefaultFood.GREENBELLPEPPER).setDensity(0.1f, 0.6f).build(),

            new Crop.Builder(JUTE, 5f, 40f, 10f, 35f, 20, 210,5, (ConfigTFC.GENERAL.monthLength / 2), false,6, DefaultFood.JUTE, null).setDensity(0.1f, 0.6f).build()
            );
    }
}
