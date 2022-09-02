/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Forge refactors their capability constants in 1.19, with intent to deprecate in 1.20
 * Refer to these to protect against future refactors.
 */
public final class Capabilities
{
    public static final Capability<IItemHandler> ITEM = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    public static final Capability<IFluidHandler> FLUID = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    public static final Capability<IFluidHandlerItem> FLUID_ITEM = CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
}
