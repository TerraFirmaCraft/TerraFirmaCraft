/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.coral.TFCDeadCoralWallFanBlock;
import net.dries007.tfc.common.fluids.TFCFluids;

public abstract class TFCCoralFeature extends Feature<NoFeatureConfig>
{
    public TFCCoralFeature(Codec<NoFeatureConfig> codec_)
    {
        super(codec_);
    }

    public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        BlockState coralBlockState = BlockTags.CORAL_BLOCKS.getRandomElement(rand).getDefaultState();
        return placeFeature(reader, rand, pos, coralBlockState);
    }

    protected abstract boolean placeFeature(IWorld world, Random rand, BlockPos pos, BlockState state);

    protected boolean placeCoralBlock(IWorld world, Random rand, BlockPos pos, BlockState coralBlockState)
    {
        BlockPos abovePos = pos.up();
        BlockState blockstate = world.getBlockState(pos);
        if ((blockstate.isIn(TFCBlocks.SALT_WATER.get()) || blockstate.isIn(TFCTags.Blocks.CORALS)) && world.getBlockState(abovePos).isIn(TFCBlocks.SALT_WATER.get()))
        {
            world.setBlockState(pos, coralBlockState, 3);
            if (rand.nextFloat() < 0.25F)
            {
                world.setBlockState(abovePos, salty(TFCTags.Blocks.CORALS.getRandomElement(rand).getDefaultState()), 2);
            }
            else if (rand.nextFloat() < 0.05F)
            {
                world.setBlockState(abovePos, salty(TFCBlocks.SEA_PICKLE.get().getDefaultState().with(SeaPickleBlock.PICKLES, rand.nextInt(4) + 1)), 2);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (rand.nextFloat() < 0.2F)
                {
                    BlockPos relativePos = pos.offset(direction);
                    if (world.getBlockState(relativePos).isIn(Blocks.WATER))
                    {
                        BlockState wallCoralState = salty(TFCTags.Blocks.WALL_CORALS.getRandomElement(rand).getDefaultState()).with(TFCDeadCoralWallFanBlock.FACING, direction);
                        world.setBlockState(relativePos, wallCoralState, 2);
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private BlockState salty(BlockState state)
    {
        return state.with(TFCBlockStateProperties.SALT_WATER, TFCBlockStateProperties.SALT_WATER.keyFor(TFCFluids.SALT_WATER.getSource()));
    }
}
