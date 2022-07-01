/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.container.ISlotCallback;

public interface BarrelInventoryCallback extends FluidTankCallback, ISlotCallback
{
    boolean canModify();
}
