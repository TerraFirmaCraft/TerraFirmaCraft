package net.dries007.tfc.world.biome;

import net.dries007.tfc.world.river.Flow;

public interface RiverSource
{
    Flow getRiverFlow(int quartX, int quartZ);
}
