/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;

public class BloomBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

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

        ItemStack stack = level.getBlockEntity(pos, TFCBlockEntities.BLOOM.get()).map(bloom -> bloom.dropBloom(state)).orElse(ItemStack.EMPTY);

        int layers = state.getValue(LAYERS) - 1;
        if (layers == 0)
        {
            return level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        boolean placeResult = level.setBlock(pos, state.setValue(LAYERS, layers), level.isClientSide ? 11 : 3);
        level.getBlockEntity(pos, TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> bloom.setBloom(stack));
        return placeResult;
    }
}
