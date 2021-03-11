package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Various extensions to {@link net.minecraft.world.gen.feature.RandomPatchFeature}
 */
public class TFCRandomPatchFeature extends Feature<TFCRandomPatchConfig>
{
    public TFCRandomPatchFeature(Codec<TFCRandomPatchConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, TFCRandomPatchConfig config)
    {
        BlockPos posAt;
        if (config.project)
        {
            posAt = world.getHeightmapPos(config.projectToOceanFloor ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG, pos);
        }
        else
        {
            posAt = pos;
        }

        int placed = 0;
        int tries = config.tries;
        if (config.useDensity)
        {
            // Scale between 50% - 150% of tries based on the adjusted forest density
            ChunkData data = ChunkData.get(world, posAt);
            tries *= (data.getAdjustedForestDensity() + 0.5f);
        }

        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final BlockState placementState = config.stateProvider.getState(random, posAt);
        for (int i = 0; i < tries; ++i)
        {
            mutablePos.setWithOffset(posAt, random.nextInt(config.xSpread + 1) - random.nextInt(config.xSpread + 1), random.nextInt(config.ySpread + 1) - random.nextInt(config.ySpread + 1), random.nextInt(config.zSpread + 1) - random.nextInt(config.zSpread + 1));

            // First check: is the state placeable at the current location
            if (placementState.canSurvive(world, mutablePos))
            {
                // Second check: is the below state passable with the white and black lists
                final BlockState stateBelow = world.getBlockState(mutablePos.below());
                if ((config.whitelist.isEmpty() || config.whitelist.contains(stateBelow.getBlock())) && !config.blacklist.contains(stateBelow))
                {
                    // Third check: is the position clear and valid
                    if ((config.canReplaceAir && world.isEmptyBlock(mutablePos)) ||
                        (config.canReplaceWater && world.isWaterAt(mutablePos)) ||
                        (config.canReplaceSurfaceWater && world.isWaterAt(mutablePos) && world.isEmptyBlock(mutablePos.above())))
                    {
                        config.blockPlacer.place(world, mutablePos, placementState, random);
                        placed++;
                    }
                }
            }
        }
        return placed > 0;
    }
}
