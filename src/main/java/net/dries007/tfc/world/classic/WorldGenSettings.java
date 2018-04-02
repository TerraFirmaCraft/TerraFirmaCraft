package net.dries007.tfc.world.classic;

import net.dries007.tfc.Constants;

public class WorldGenSettings
{
    public int spawnFuzz = 250;
    public boolean flatBedrock = false;

    public static WorldGenSettings fromString(String options)
    {
        return Constants.GSON.fromJson(options, WorldGenSettings.class);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldGenSettings that = (WorldGenSettings) o;

        if (spawnFuzz != that.spawnFuzz) return false;
        return flatBedrock == that.flatBedrock;
    }

    @Override
    public int hashCode()
    {
        int result = spawnFuzz;
        result = 31 * result + (flatBedrock ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return Constants.GSON.toJson(this);
    }
}
