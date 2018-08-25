/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import net.dries007.tfc.api.types.Metal;

public class FluidMetal extends Fluid
{

    private final Metal metal;

    public FluidMetal(Metal metal, String fluidName, ResourceLocation still, ResourceLocation flowing, int color)
    {
        super(fluidName, still, flowing, color);

        this.metal = metal;
    }

    public boolean doesFluidHaveMold(Metal.ItemType type)
    {
        if (type == Metal.ItemType.INGOT || type == Metal.ItemType.UNSHAPED)
            return true;
        return type.hasMold && metal.isToolMetal() && (metal.tier == Metal.Tier.TIER_I || metal.tier == Metal.Tier.TIER_II);
    }

    public Metal getMetal()
    {
        return metal;
    }
}
