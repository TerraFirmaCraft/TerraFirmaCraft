package net.dries007.tfc.world.biome;

import net.dries007.tfc.world.BiomeNoiseSampler;

@FunctionalInterface
public interface AquiferLookahead
{
    double getHeight(BiomeNoiseSampler sampler, int x, int z);
}
