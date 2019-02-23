/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.DoublePlant;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultDoublePlants
{
    /**
     * Default Double Plant ResourceLocations
     */
    public static final ResourceLocation DOUBLE_FERN = new ResourceLocation(MOD_ID, "double_fern");

    @SubscribeEvent
    public static void onPreRegisterPlantCategory(TFCRegistryEvent.RegisterPreBlock<DoublePlant> event)
    {
        event.getRegistry().registerAll(
            new DoublePlant.Builder(DOUBLE_FERN, 0f, 500f, 0f, 40f).build()
        );
    }
}
