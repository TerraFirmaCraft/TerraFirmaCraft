package net.dries007.tfc.world.classic;

import net.dries007.tfc.Constants;

public class WorldGenSettings
{
    public int spawnFuzz = 250;

    public static WorldGenSettings fromString(String options)
    {
        return Constants.GSON.fromJson(options, WorldGenSettings.class);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldGenSettings settings = (WorldGenSettings) o;

        return spawnFuzz == settings.spawnFuzz;
    }

    @Override
    public int hashCode()
    {
        return spawnFuzz;
    }

    @Override
    public String toString()
    {
        return Constants.GSON.toJson(this);
    }
}
