package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class CavePatchFeature extends Feature<BlockClusterFeatureConfig>
{
    public CavePatchFeature(Codec<BlockClusterFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        BlockState blockstate = config.stateProvider.getState(rand, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int j = 0; j < config.tries; ++j)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), 0, rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
            for (int k = 0; k < 7; k++)
            {
                mutablePos.move(0, -1, 0);
                if (!world.isEmptyBlock(mutablePos))
                {
                    break;
                }
            }
            BlockState belowState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            if (world.isEmptyBlock(mutablePos) && blockstate.canSurvive(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState))
            {
                config.blockPlacer.place(world, mutablePos, blockstate, rand); //randomize age
                ++i;
            }
        }
        return i > 0;
    }
}
