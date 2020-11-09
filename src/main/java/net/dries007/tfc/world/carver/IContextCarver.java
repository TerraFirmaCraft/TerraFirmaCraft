package net.dries007.tfc.world.carver;

import java.util.BitSet;

import net.minecraft.world.gen.WorldGenRegion;

import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Marker interface for carvers which need additional context
 * Carvers may require this to be called (and thus require TFC-compatible chunk generators) or throw errors upon generation.
 */
public interface IContextCarver
{
    void setContext(WorldGenRegion world, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, BitSet waterAdjacencyMask);
}
