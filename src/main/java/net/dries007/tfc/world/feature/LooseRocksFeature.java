package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.Tags;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
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

    @SuppressWarnings("deprecation")
    private void place(ISeedReader world, BlockState state, BlockPos pos, Random rand)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 21; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(16) - rand.nextInt(16), rand.nextInt(2) - rand.nextInt(2), rand.nextInt(16) - rand.nextInt(16));

            final BlockState stateAt = world.getBlockState(mutablePos);
            final FluidProperty fluidProperty = TFCBlockStateProperties.WATER;
            if ((stateAt.isAir() || fluidProperty.canContain(stateAt.getFluidState().getType())) && state.canSurvive(world, mutablePos))
            {
                setBlock(world, mutablePos, state.setValue(TFCBlockStateProperties.COUNT_1_3, 1 + rand.nextInt(2))
                    .setValue(HorizontalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(rand))
                    .setValue(fluidProperty, fluidProperty.keyFor(world.getFluidState(mutablePos).getType())));
            }
        }
    }
}
