/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

public final class Plate
{
    private final float x;
    private final float z;
    private final float driftX;
    private final float driftZ;
    private final float elevation;
    private final boolean oceanic;

    public Plate(float x, float z, float driftX, float driftZ, float elevation, boolean oceanic)
    {
        this.x = x;
        this.z = z;
        this.driftX = driftX;
        this.driftZ = driftZ;
        this.elevation = elevation;
        this.oceanic = oceanic;
    }

    public float getX()
    {
        return x;
    }

    public float getZ()
    {
        return z;
    }

    public float getDriftX()
    {
        return driftX;
    }

    public float getDriftZ()
    {
        return driftZ;
    }

    public float getElevation()
    {
        return elevation;
    }

    public boolean isOceanic()
    {
        return oceanic;
    }

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
        if (other instanceof Plate)
        {
            Plate plate = (Plate) other;
            return this.x == plate.x && this.z == plate.z;
        }
        return false;
    }
}
