/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;

/**
 * This is an interface wrapper for the capability provided by an ItemMold. You can safely cast to this
 */
public interface IMoldHandler extends IFluidHandler, INBTSerializable<NBTTagCompound>, IItemHeat
{
    /**
     * Gets the metal currently in the mold. Null if empty. Used in model loading.
     *
     * @return The metal
     */
    @Nullable
    Metal getMetal();
}
