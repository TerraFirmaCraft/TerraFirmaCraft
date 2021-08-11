/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.GrassPathBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.common.blocks.TFCBlocks;


import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        if (!state.canSurvive(context.getLevel(), context.getClickedPos()))
        {
            return Block.pushEntitiesUp(state, getDirt(), context.getLevel(), context.getClickedPos());
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        worldIn.setBlockAndUpdate(pos, Block.pushEntitiesUp(state, getDirt(), worldIn, pos));
    }

    @Override
    public BlockState getDirt()
    {
        return dirtBlock.get().defaultBlockState();
    }
}