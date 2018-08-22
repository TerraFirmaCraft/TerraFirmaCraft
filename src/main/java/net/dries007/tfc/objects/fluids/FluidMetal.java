/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.MetalType;

public class FluidMetal extends Fluid
{
    private final Metal metal;

    public FluidMetal(Metal metal, String fluidName, ResourceLocation still, ResourceLocation flowing, int color)
    {
        super(fluidName, still, flowing, color);

        this.metal = metal;
    }

    public boolean doesFluidHaveMold(MetalType type)
    {
        if (type == MetalType.INGOT || type == MetalType.UNSHAPED)
            return true;
        return type.hasMold && metal.isToolMetal() && (metal.tier == Metal.Tier.TIER_I || metal.tier == Metal.Tier.TIER_II);

    }

    public Metal getMetal()
    {
        return metal;
    }
}
