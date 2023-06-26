/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;

/**
 * Forge refactors their capability constants in 1.19, with intent to deprecate in 1.20
 * Refer to these to protect against future refactors.
 */
public final class Capabilities
{
    public static final Capability<IItemHandler> ITEM = ForgeCapabilities.ITEM_HANDLER;
    public static final Capability<IFluidHandler> FLUID = ForgeCapabilities.FLUID_HANDLER;
    public static final Capability<IFluidHandlerItem> FLUID_ITEM = ForgeCapabilities.FLUID_HANDLER_ITEM;
}
