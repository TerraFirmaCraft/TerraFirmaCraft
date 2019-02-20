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
import net.dries007.tfc.api.types.CreepingPlant;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultCreepingPlants
{
    /**
     * Default Creeping Plant ResourceLocations
     */
    public static final ResourceLocation MOSS = new ResourceLocation(MOD_ID, "moss");
    public static final ResourceLocation BLUE_DAWN = new ResourceLocation(MOD_ID, "blue_dawn");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistryEvent.RegisterPreBlock<CreepingPlant> event)
    {
        event.getRegistry().registerAll(
            new CreepingPlant.Builder(MOSS, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build(),
            new CreepingPlant.Builder(BLUE_DAWN, 0f, 500f, 0f, 40f).setDominance(8.5f).setDensity(0.6f, 2f).build()
        );
    }
}
