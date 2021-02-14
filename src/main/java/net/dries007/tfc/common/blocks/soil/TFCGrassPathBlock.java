/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlocks;


public class TFCGrassPathBlock extends GrassPathBlock implements ISoilBlock
{
    private final Supplier<Block> dirtBlock;

    public TFCGrassPathBlock(Properties builder, SoilBlockType soil, SoilBlockType.Variant variant)
    {
        this(builder, TFCBlocks.SOIL.get(soil).get(variant));
    }

    protected TFCGrassPathBlock(Properties builder, Supplier<Block> dirtBlock)
    {
        super(builder);

        this.dirtBlock = dirtBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        if (!state.canBeReplacedByLeaves(context.getWorld(), context.getPos()))
        {
            return Block.pushEntitiesUp(state, getDirt(), context.getWorld(), context.getPos());
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        worldIn.setBlockAndUpdate(pos, Block.pushEntitiesUp(state, getDirt(), worldIn, pos));
    }

    @Override
    public BlockState getDirt()
    {
        return dirtBlock.get().getDefaultState();
    }
}