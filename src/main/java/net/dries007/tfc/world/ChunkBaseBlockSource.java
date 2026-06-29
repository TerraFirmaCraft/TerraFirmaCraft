/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.RockData;

public class ChunkBaseBlockSource
{
    private static int index(int x, int z)
    {
        return (x & 15) | ((z & 15) << 4);
    }

    private final RockData rockData;
    private final Sampler<BiomeExtension> biomeSampler;
    private final BlockState[] cachedFluidStates;

    private final BlockState freshWater = Blocks.WATER.defaultBlockState(), saltWater = TFCBlocks.SALT_WATER.get().defaultBlockState();

    public ChunkBaseBlockSource(RockData rockData, Sampler<BiomeExtension> biomeSampler)
    {
        this.rockData = rockData;
        this.biomeSampler = biomeSampler;
        this.cachedFluidStates = new BlockState[16 * 16];
    }

    /**
     * Getting saltwater from only the biome has issues at shores, where sometimes the original land biome will influence the water
     * on the edge of the ocean biome, but the shore biome will influence the water at the shore proper, leading to freshwater "rings"
     * around the shore. This can be addressed by only placing freshwater when the biome weight is sufficiently high, but that causes
     * rivers to be salty as they do not ever have high biome weights, so that is then special cased. With both of those checks, there is
     * still an edge case where if we only check the primary biome's weight, at intersections between 3 non-salty biomes saltwater will
     * generate because the highest weight is still low. Thus, before running those checks, we see if there are any nearby biomes that are salty.
     * Add a extra check to force salt water in spots that are sufficiently close to shore/ocean at sea level. This mostly fixes the issue of fresh
     * water occurring where water pools generate in land biomes but are connected to the ocean.
     */
    public void useAccurateBiome(int localX, int localZ, BiomeExtension biome, double weight, boolean couldBeSalty, boolean forceSaltWater)
    {
        cachedFluidStates[index(localX, localZ)] = forceSaltWater || (couldBeSalty && (biome.isSalty() || (weight <= 0.5 && biome != TFCBiomes.RIVER))) ? saltWater : freshWater;
    }

    public BlockState getBaseBlock(int blockX, int blockY, int blockZ)
    {
        return rockData.getRock(blockX, blockY, blockZ).raw().defaultBlockState();
    }

    public BlockState modifyFluid(BlockState fluidOrAir, int x, int z)
    {
        if (fluidOrAir == freshWater)
        {
            final int index = index(x, z);
            BlockState state = cachedFluidStates[index];
            if (state == null)
            {
                state = biomeSampler.get(x, z).isSalty() ? saltWater : freshWater;
                cachedFluidStates[index] = state;
            }
            return state;
        }
        return fluidOrAir;
    }
}
