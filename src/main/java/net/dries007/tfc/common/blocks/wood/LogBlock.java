/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class LogBlock extends RotatedPillarBlock
{
    public static final BooleanProperty NATURAL = TFCBlockStateProperties.NATURAL;

    @Nullable private final Supplier<? extends Block> stripped;

    public LogBlock(Properties properties, @Nullable Supplier<? extends Block> stripped)
    {
        super(properties);
        this.stripped = stripped;

        registerDefaultState(defaultBlockState().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NATURAL));
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction action)
    {
        if (stack.canPerformAction(action) && action == ToolActions.AXE_STRIP && stripped != null)
        {
            return Helpers.copyProperty(stripped.get().defaultBlockState(), state, AXIS);
        }
        return state;
    }
}
