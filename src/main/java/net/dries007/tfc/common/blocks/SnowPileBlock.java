/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

/**
 * This block is a snow layer block that hides / covers a block underneath
 * When it melts, it will transform into the underlying block, with one level of snow active
 */
public class SnowPileBlock extends SnowLayerBlock implements IForgeBlockExtension, EntityBlockExtension
{
    /**
     * Checks if a snow pile is valid at a given location. Does not return true if a snow pile is invalid but a single snow layer is.
     * @return {@code true} if a snow pile could exist at a given location, possibly absorbing the block and block above.
     */
    public static boolean canPlaceSnowPile(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return TFCTags.Blocks.CAN_BE_SNOW_PILED.contains(state.getBlock()) && TFCBlocks.SNOW_PILE.get().defaultBlockState().canSurvive(level, pos);
    }

    /**
     * Places a snow pile at the given location, possibly absorbing the block above as well.
     */
    public static void placeSnowPile(LevelAccessor level, BlockPos pos, BlockState state)
    {
        // Create a snow pile block, accounting for double piles.
        final BlockPos posAbove = pos.above();
        final BlockState aboveState = level.getBlockState(posAbove);
        final BlockState savedAboveState = TFCTags.Blocks.CAN_BE_SNOW_PILED.contains(aboveState.getBlock()) ? aboveState : null;
        final BlockState snowPile = TFCBlocks.SNOW_PILE.get().defaultBlockState();

        level.setBlock(pos, snowPile, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        level.getBlockEntity(pos, TFCBlockEntities.PILE.get()).ifPresent(entity -> entity.setHiddenStates(state, savedAboveState));

        if (savedAboveState != null)
        {
            Helpers.removeBlock(level, posAbove, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }

        // Then cause block updates
        level.blockUpdated(pos, TFCBlocks.SNOW_PILE.get());
        if (savedAboveState != null)
        {
            level.blockUpdated(posAbove, Blocks.AIR);
        }

        // And update grass with the snowy property
        final BlockPos posBelow = pos.below();
        level.setBlock(posBelow, Helpers.setProperty(level.getBlockState(posBelow), SnowyDirtBlock.SNOWY, true), 2);
    }

    public static void removePileOrSnow(LevelAccessor level, BlockPos pos, BlockState state)
    {
        removePileOrSnow(level, pos, state, false);
    }

    public static void removePileOrSnow(LevelAccessor level, BlockPos pos, BlockState state, boolean removeAllLayers)
    {
        final int layers = state.getValue(SnowLayerBlock.LAYERS);
        if (layers > 1 && !removeAllLayers)
        {
            // Remove one layer
            level.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1), Block.UPDATE_ALL);
        }
        else if (state.getBlock() == Blocks.SNOW)
        {
            // Remove a single snow layer block
            level.removeBlock(pos, false);
        }
        else
        {
            // Otherwise, remove a snow pile, restoring the internal states
            level.getBlockEntity(pos, TFCBlockEntities.PILE.get()).ifPresent(pile -> {
                if (!level.isClientSide())
                {
                    level.setBlock(pos, pile.getInternalState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    if (pile.getAboveState() != null)
                    {
                        level.setBlock(pos.above(), pile.getAboveState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    }

                    // Update neighbors shapes from the bottom block (this is important to get grass blocks to adjust to snowy/non-snowy states)
                    pile.getInternalState().updateNeighbourShapes(level, pos, Block.UPDATE_CLIENTS);

                    // Block ticks after both blocks are placed
                    level.blockUpdated(pos, pile.getInternalState().getBlock());
                    if (pile.getAboveState() != null)
                    {
                        level.blockUpdated(pos.above(), pile.getAboveState().getBlock());
                    }
                }
            });
        }
    }

    private final ExtendedProperties properties;

    public SnowPileBlock(ExtendedProperties properties)
    {
        super(properties.properties());

        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    /**
     * This allows two things:
     * - Snow piles are removed one layer at a time, same as snow blocks (modified via mixin)
     * - Once removed enough, they convert to the underlying block state.
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(level, pos, state, player);
        removePileOrSnow(level, pos, state);
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context)
    {
        // Handle clicking on a snow pile with snow layers - increment the layer count
        // We have to handle the getStateForPlacement in a mixin to snow layer block
        if (context.getItemInHand().getItem() == Blocks.SNOW.asItem() && state.getValue(LAYERS) < 8)
        {
            if (context.replacingClickedOnBlock())
            {
                return context.getClickedFace() == Direction.UP;
            }
            else
            {
                return true;
            }
        }
        return false; // Don't allow replacement of single layers with anything (unlike vanilla snow)
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        return new ItemStack(Blocks.SNOW);
    }
}
