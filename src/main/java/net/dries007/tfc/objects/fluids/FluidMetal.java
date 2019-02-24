/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import net.dries007.tfc.api.types.Metal;

@ParametersAreNonnullByDefault
public class FluidMetal extends Fluid
{
    private final Metal metal;

    public FluidMetal(Metal metal, String fluidName, ResourceLocation still, ResourceLocation flowing, int color)
    {
        super(fluidName, still, flowing, color);

        this.metal = metal;
    }

    @Nonnull
    public Metal getMetal()
    {
        return metal;
    }
}
