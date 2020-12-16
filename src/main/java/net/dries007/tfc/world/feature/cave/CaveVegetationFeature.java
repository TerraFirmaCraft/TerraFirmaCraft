package net.dries007.tfc.world.feature.cave;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class CaveVegetationFeature extends Feature<CaveVegetationConfig>
{
    public CaveVegetationFeature(Codec<CaveVegetationConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, CaveVegetationConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 128; i++)
        {
            if (rand.nextFloat() < 0.8f)//mossy cobble
            {
                mutablePos.setWithOffset(pos, rand.nextInt(15) - rand.nextInt(15), -1 * rand.nextInt(2) - 1, rand.nextInt(15) - rand.nextInt(15));
                if (worldIn.isEmptyBlock(mutablePos))
                {
                    for (int j = 0; j < 7; j++)
                    {
                        mutablePos.move(0, -1, 0);
                        if (!worldIn.isEmptyBlock(mutablePos))
                        {
                            break;
                        }
                    }
                    BlockState generateState = config.getStateToGenerate(worldIn.getBlockState(mutablePos), rand);
                    if (generateState != null)
                    {
                        setBlock(worldIn, mutablePos, generateState);
                    }
                }
            }
            if (rand.nextFloat() < 0.003f)//extra springs
            {
                mutablePos.setWithOffset(pos, rand.nextInt(15) - rand.nextInt(15), 4 + rand.nextInt(7), rand.nextInt(15) - rand.nextInt(15));
                if (worldIn.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(Direction.UP);
                    if (worldIn.getBlockState(mutablePos).is(BlockTags.BASE_STONE_OVERWORLD))
                    {
                        setBlock(worldIn, mutablePos, Fluids.WATER.defaultFluidState().createLegacyBlock());
                        worldIn.getLiquidTicks().scheduleTick(mutablePos, Fluids.WATER, 0);
                    }
                }
            }
            if (rand.nextFloat() < 0.02f)//cobwebs
            {
                mutablePos.setWithOffset(pos, rand.nextInt(15) - rand.nextInt(15), 4 + rand.nextInt(7), rand.nextInt(15) - rand.nextInt(15));
                if (worldIn.getBlockState(mutablePos).is(BlockTags.BASE_STONE_OVERWORLD))
                {
                    mutablePos.move(Direction.DOWN);
                    if (worldIn.isEmptyBlock(mutablePos))
                    {
                        setBlock(worldIn, mutablePos, Blocks.COBWEB.defaultBlockState());
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    protected BlockState getStateToGenerate(BlockState stoneState, Random random, CaveVegetationConfig config)
    {
        return config.getStateToGenerate(stoneState, random);
    }
}
