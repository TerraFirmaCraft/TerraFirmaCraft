/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class LogBlock extends ExtendedRotatedPillarBlock
{
    public static final EnumProperty<BranchDirection> BRANCH_DIRECTION = TFCBlockStateProperties.BRANCH_DIRECTION;

    @Nullable private final Supplier<? extends Block> stripped;

    public LogBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> stripped)
    {
        super(properties);
        this.stripped = stripped;

        registerDefaultState(defaultBlockState().setValue(BRANCH_DIRECTION, BranchDirection.NONE));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos)
    {
        return super.getDestroyProgress(state, player, level, pos)
            / (state.getValue(BRANCH_DIRECTION).natural() ? 2 : 1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(BRANCH_DIRECTION));
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility action, boolean simulate)
    {
        if (context.getItemInHand().canPerformAction(action) && action == ItemAbilities.AXE_STRIP && stripped != null)
        {
            return Helpers.copyProperties(stripped.get().defaultBlockState(), state);
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotatePillar(state, rotation).setValue(BRANCH_DIRECTION, state.getValue(BRANCH_DIRECTION).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.setValue(BRANCH_DIRECTION, state.getValue(BRANCH_DIRECTION).mirror(mirror));
    }
}
