/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

import net.dries007.tfc.config.TFCConfig;

public class TopPlantBlock extends AbstractTopPlantBlock
{
    private final Supplier<? extends Block> bodyBlock;

    public TopPlantBlock(AbstractBlock.Properties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties, direction, shape, false, 0);
        this.bodyBlock = bodyBlock;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (state.getValue(AGE) < 25 && ForgeHooks.onCropsGrowPre(worldIn, pos.relative(growthDirection), worldIn.getBlockState(pos.relative(growthDirection)), random.nextDouble() < TFCConfig.SERVER.plantGrowthChance.get()))
        {
            BlockPos blockpos = pos.relative(growthDirection);
            if (canGrowInto(worldIn.getBlockState(blockpos)))
            {
                worldIn.setBlockAndUpdate(blockpos, state.cycle(AGE));
                ForgeHooks.onCropsGrowPost(worldIn, blockpos, worldIn.getBlockState(blockpos));
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(Random rand)
    {
        return 0;
    }

    @Override
    protected boolean canGrowInto(BlockState state)
    {
        return PlantBlockHelper.isValidGrowthState(state);
    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.relative(growthDirection.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canAttachToBlock(block))
        {
            return false;
        }
        else
        {
            return block == getHeadBlock() || block == getBodyBlock() || blockstate.is(BlockTags.LEAVES) || blockstate.isFaceSturdy(worldIn, blockpos, growthDirection);
        }
    }

    @Override
    protected Block getBodyBlock()
    {
        return bodyBlock.get();
    }
}
