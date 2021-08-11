/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

public record Plate(float x, float z, float driftX, float driftZ, float elevation, boolean oceanic)
{
    @Override
    public int hashCode()
    {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other instanceof Plate plate)
        {
            return this.x == plate.x && this.z == plate.z;
        }
        return false;
    }
}
