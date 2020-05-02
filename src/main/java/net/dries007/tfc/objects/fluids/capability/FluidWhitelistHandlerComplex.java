/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.capability;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

//This extends from the non-simple fluid handler, and FluidWhitelistHandler isn't labeled simple, so added -Complex here
public class FluidWhitelistHandlerComplex extends FluidHandlerItemStack
{
    private final Set<Fluid> whitelist;

    public FluidWhitelistHandlerComplex(@Nonnull ItemStack container, int capacity, String[] fluidNames)
    {
        this(container, capacity, Arrays.stream(fluidNames).map(FluidRegistry::getFluid).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public FluidWhitelistHandlerComplex(@Nonnull ItemStack container, int capacity, Set<Fluid> whitelist)
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
