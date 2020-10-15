package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.provider.BiomeProvider;

/**
 * Marker for TFC biome providers
 */
public interface ITFCBiomeProvider
{
    int getSpawnDistance();

    /**
     * @return itself, or the underlying biome provider / source
     */
    default BiomeProvider biomeSource()
    {
        return (BiomeProvider) this;
    }
}
