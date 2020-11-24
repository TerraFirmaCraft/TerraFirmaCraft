/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.tracker;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import net.dries007.tfc.util.Helpers;

public class WorldTrackerCapability
{
    @CapabilityInject(IWorldTracker.class)
    public static final Capability<IWorldTracker> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = Helpers.identifier("world_tracker");

    public static void setup()
    {
        Helpers.registerSimpleCapability(IWorldTracker.class);
    }
}