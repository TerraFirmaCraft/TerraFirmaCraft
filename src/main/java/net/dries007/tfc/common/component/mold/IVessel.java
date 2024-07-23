/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.mold;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.component.heat.IHeat;

/**
 * A capability that is returned by items implementing a fluid and a heat handler. The internal inventory must act with multiple "modes",
 * where it is always in a specific mode, exclusive with other states.
 */
public interface IVessel extends IItemHandler, IFluidHandlerItem, IHeat
{

}
