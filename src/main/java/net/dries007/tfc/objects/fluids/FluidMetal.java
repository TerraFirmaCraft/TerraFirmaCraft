/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidMetal extends Fluid
{
    public FluidMetal(String fluidName, ResourceLocation still, ResourceLocation flowing, int color)
    {
        super(fluidName, still, flowing, color);
    }
}
