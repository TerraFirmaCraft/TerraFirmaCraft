/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class LogBlock extends RotatedPillarBlock implements IForgeBlockExtension
{
    public static final BooleanProperty NATURAL = TFCBlockStateProperties.NATURAL;

    @Nullable private final Supplier<? extends Block> stripped;
    private final ExtendedProperties properties;

    public LogBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> stripped)
    {
        super(properties.properties());
        this.stripped = stripped;
        this.properties = properties;

        registerDefaultState(defaultBlockState().setValue(NATURAL, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos)
    {
        // Modified from the super() method, including the Forge patch, to add the 2x hardness in natural state modifier.
        final float baseSpeed = (state.getValue(NATURAL) ? 2 : 1) * state.getDestroySpeed(level, pos);
        if (baseSpeed == -1.0F)
        {
            return 0.0F;
        }
        else
        {
            final int toolModifier = ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
            return player.getDigSpeed(state, pos) / baseSpeed / (float) toolModifier;
        }
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NATURAL));
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate)
    {
        if (context.getItemInHand().canPerformAction(action) && action == ToolActions.AXE_STRIP && stripped != null)
        {
            return Helpers.copyProperties(stripped.get().defaultBlockState(), state);
        }
        return null;
    }
}
