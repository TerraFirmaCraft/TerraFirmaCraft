/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

/**
 * This is a separate class from {@link Fluid} to avoid subclassing.
 * From LexManos:
 * > yes thats the point, that if you share a name you use whatever is registered
 * > you are not special and do not need your special functionality
 * > IF you do NOT want to work well with others and you have to  be a special special snowflake, namespace your shit.
 * So in order to keep TFC working well with other mods, we shall use whatever fluids are registered, but we still need to map them to properties
 */
public class FluidWrapper
{
    private final Fluid fluid;
    private final boolean isDefault;

    public FluidWrapper(@Nonnull Fluid fluid, boolean isDefault)
    {
        this.fluid = fluid;
        this.isDefault = isDefault;
    }

    @Nonnull
    public Fluid get()
    {
        return fluid;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public interface Factory
    {
        @Nonnull
        FluidWrapper create(@Nonnull Fluid defaultFluid, boolean isDefault);
    }
}
