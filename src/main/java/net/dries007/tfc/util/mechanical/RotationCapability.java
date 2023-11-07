/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.mechanical;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;

public final class RotationCapability
{
    public static final Capability<Node> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("rotation");

    @Nullable
    public static RotationNetworkManager get(Level level)
    {
        return level.getCapability(WorldTrackerCapability.CAPABILITY)
            .map(WorldTracker::getRotationManager)
            .orElse(null);
    }
}
