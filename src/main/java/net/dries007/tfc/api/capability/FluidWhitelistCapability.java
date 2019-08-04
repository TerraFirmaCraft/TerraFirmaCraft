/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability;

import java.util.Set;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class FluidWhitelistCapability extends FluidHandlerItemStack
{
    private final Set<Fluid> whitelist;

    public FluidWhitelistCapability(@Nonnull ItemStack container, int capacity, Set<Fluid> whitelist)
    {
        super(container, capacity);
        this.whitelist = whitelist;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return whitelist.contains(fluid.getFluid());
    }
}
