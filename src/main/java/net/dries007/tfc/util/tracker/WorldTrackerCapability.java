/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.util.Helpers;

public class WorldTrackerCapability
{
    @CapabilityInject(IWorldTracker.class)
    public static final Capability<IWorldTracker> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = Helpers.identifier("world_tracker");

    public static void setup()
    {
        CapabilityManager.INSTANCE.register((Class<?>) IWorldTracker.class);
    }
}