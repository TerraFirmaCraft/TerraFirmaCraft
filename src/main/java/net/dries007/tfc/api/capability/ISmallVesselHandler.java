/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface ISmallVesselHandler extends IItemHandlerModifiable, IFluidHandler, INBTSerializable<NBTTagCompound> // todo: add IItemHeat
{

    /**
     * This sets the fluid mode. Only applicable for small vessels
     *
     * @param mode true = fluids, false = items
     */
    default void setFluidMode(boolean fluidMode) {}
}
