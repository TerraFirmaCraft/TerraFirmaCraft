package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.Tags;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class LooseRocksFeature extends Feature<NoFeatureConfig>
{
    public LooseRocksFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.ROCKS);
        final Rock rock = data.getRockData().getRock(pos.getX(), pos.getY(), pos.getZ());
        final BlockState state = rock.getBlock(Rock.BlockType.LOOSE).defaultBlockState();
        place(worldIn, state, pos, rand);
        return true;
    }

    private void place(ISeedReader world, BlockState state, BlockPos pos, Random rand)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 21; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(16) - rand.nextInt(16), rand.nextInt(2) - rand.nextInt(2), rand.nextInt(16) - rand.nextInt(16));
            if ((world.isEmptyBlock(mutablePos) || world.isWaterAt(mutablePos)) && state.canSurvive(world, mutablePos))
            {
                int rocks = 1;
                final int randInt = rand.nextInt(100);
                if (world.getBlockState(mutablePos.below()).is(Tags.Blocks.STONE))
                    rocks += 50;
                if (world.getBlockState(mutablePos.below()).is(Tags.Blocks.GRAVEL))
                    rocks += 20;
                if (randInt > 87)
                {
                    rocks++;
                    if (randInt < 97)
                        rocks++;
                }
                setBlock(world, mutablePos, state.setValue(TFCBlockStateProperties.COUNT_1_3, rocks)
                    .setValue(HorizontalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(rand))
                    .setValue(TFCBlockStateProperties.WATER, TFCBlockStateProperties.WATER.keyFor(world.getFluidState(mutablePos).getType())));
            }
        }
    }
}
