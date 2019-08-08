/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

import net.dries007.tfc.api.types.Metal;

public class MetalFluidWrapper extends FluidWrapper
{
    private final Metal metal;

    public MetalFluidWrapper(@Nonnull Fluid fluid, boolean isNewFluid, @Nonnull Metal metal)
    {
        super(fluid, isNewFluid);
        this.metal = metal;
    }

    @Nonnull
    public Metal getMetal()
    {
        return metal;
    }
}
