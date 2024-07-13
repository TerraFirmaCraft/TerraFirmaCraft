/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.BloomBlockEntity;

public class BloomBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    public static final VoxelShape[] SHAPE_BY_LAYER = {
        Shapes.empty(),
        box(0, 0, 0, 16, 2, 16),
        box(0, 0, 0, 16, 4, 16),
        box(0, 0, 0, 16, 6, 16),
        box(0, 0, 0, 16, 8, 16),
        box(0, 0, 0, 16, 10, 16),
        box(0, 0, 0, 16, 12, 16),
        box(0, 0, 0, 16, 14, 16),
        box(0, 0, 0, 16, 16, 16)
    };

    public BloomBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LAYERS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LAYERS);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(level, pos, state, player);

        if (player.isCreative())
        {
            return level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        if (level.getBlockEntity(pos) instanceof BloomBlockEntity bloom)
        {
            return bloom.dropBloom();
        }

        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }
}
