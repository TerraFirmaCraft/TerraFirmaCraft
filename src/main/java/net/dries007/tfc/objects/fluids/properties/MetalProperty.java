/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

import javax.annotation.Nonnull;

import net.dries007.tfc.api.types.Metal;

public class MetalProperty
{
    public static final FluidProperty<MetalProperty> METAL = new FluidProperty<>("metal");

    private final Metal metal;

    public MetalProperty(@Nonnull Metal metal)
    {
        this.metal = metal;
    }

    @Nonnull
    public Metal getMetal()
    {
        return metal;
    }
}
