/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

public final class FluidProperty<T>
{
    private final String name;

    public FluidProperty(String name)
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof FluidProperty && ((FluidProperty) obj).name.equals(this.name);
    }
}
