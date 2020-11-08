package net.dries007.tfc.world.carver;

import java.util.BitSet;

import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;

/**
 * Marker interface for carvers which need to be given the world seed
 *
 * This is dumb but vanilla's carver nonsense is absolute trash, and given the reworks coming in 1.17 let's just stick with trash for now as long we make it base line configurable.
 */
public interface IContextCarver
{
    void setContext(WorldGenRegion world, BitSet airCarvingMask, BitSet liquidCarvingMask);
}
