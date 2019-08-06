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
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class FluidWhitelistHandler extends FluidHandlerItemStackSimple
{
    private final Set<Fluid> whitelist;

    public FluidWhitelistHandler(@Nonnull ItemStack container, int capacity, String[] fluidNames)
    {
        this(container, capacity, Arrays.stream(fluidNames).map(FluidRegistry::getFluid).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public FluidWhitelistHandler(@Nonnull ItemStack container, int capacity, Set<Fluid> whitelist)
    {
        super(container, capacity);
        this.whitelist = whitelist;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return whitelist.contains(fluid.getFluid());
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.getCount() != 1 || resource == null || resource.amount <= 0 || !canFillFluidType(resource))
        {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained == null)
        {
            int fillAmount = resource.amount;
            if (fillAmount >= capacity)
            {
                if (doFill)
                {
                    FluidStack filled = resource.copy();
                    filled.amount = capacity;
                    setFluid(filled);
                }

                return fillAmount;
            }
        }

        return 0;
    }
}
