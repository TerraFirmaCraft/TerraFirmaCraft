/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.egg;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class CapabilityEgg
{
    @CapabilityInject(IEgg.class)
    public static final Capability<IEgg> CAPABILITY = Helpers.getNull();

    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "egg");

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IEgg.class, new DumbStorage<>(), EggHandler::new);
    }
}
