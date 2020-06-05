/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.worldtracker;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CapabilityWorldTracker
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "world_tracker");
    @CapabilityInject(WorldTracker.class)
    public static Capability<WorldTracker> CAPABILITY = Helpers.getNull();

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(WorldTracker.class, new DumbStorage<>(), WorldTracker::new);
    }
}
