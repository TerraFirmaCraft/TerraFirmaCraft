package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

public class RandomPatchDensityFeature extends Feature<BlockClusterFeatureConfig>
{
    public RandomPatchDensityFeature(Codec<BlockClusterFeatureConfig> codec)
    {
        super(codec);
    }

    //unused: project, canReplace
    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        BlockState blockstate = config.stateProvider.getState(rand, pos);
        BlockPos blockpos = world.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        ChunkData data = ChunkData.get(world, mutablePos);
        float density = 0.7f * (data.getForestDensity() + 0.5f);
        ForestType type = data.getForestType();
        if (type != ForestType.NORMAL && type != ForestType.SPARSE)
            density += 0.3f;
        int tries = Math.min((int) (config.tries * density), 128);
        for (int j = 0; j < tries; ++j)
        {
            mutablePos.setWithOffset(blockpos, rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), rand.nextInt(config.yspread + 1) - rand.nextInt(config.yspread + 1), rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
            BlockPos below = mutablePos.below();
            BlockState state = world.getBlockState(below);
            if ((world.isEmptyBlock(mutablePos) && blockstate.canSurvive(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(state.getBlock())) && !config.blacklist.contains(state)))
            {
                config.blockPlacer.place(world, mutablePos, blockstate.setValue(TFCBlockStateProperties.AGE_3, rand.nextInt(3) + 1), rand); //randomize age
                ++i;
            }
        }
        return i > 0;
    }
}
