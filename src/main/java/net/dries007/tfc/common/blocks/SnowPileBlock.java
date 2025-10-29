/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.util.Helpers;

/**
 * This block is a snow layer block that hides / covers a block underneath
 * When it melts, it will transform into the underlying block, with one level of snow active
 */
public class SnowPileBlock extends SnowLayerBlock implements IForgeBlockExtension, EntityBlockExtension
{
    /**
     * Checks if a snow pile is valid at a given location. Does not return true if a snow pile is invalid but a single snow layer is.
     *
     * @return {@code true} if a snow pile could exist at a given location, possibly absorbing the block and block above.
     */
    public static boolean canPlaceSnowPile(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.CAN_BE_SNOW_PILED) && TFCBlocks.SNOW_PILE.get().defaultBlockState().canSurvive(level, pos);
    }

    /**
     * Places a snow pile at the given location, possibly absorbing the block above as well.
     */
    public static void placeSnowPile(LevelAccessor level, BlockPos pos, BlockState state, boolean byPlayer)
    {
        // Create a snow pile block, accounting for double piles.
        final BlockPos posAbove = pos.above();
        final BlockState aboveState = level.getBlockState(posAbove);
        final BlockState savedAboveState = Helpers.isBlock(aboveState.getBlock(), TFCTags.Blocks.CAN_BE_SNOW_PILED) ? aboveState : null;
        final BlockState snowPile = TFCBlocks.SNOW_PILE.get().defaultBlockState();

        level.setBlock(pos, snowPile, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        level.getBlockEntity(pos, TFCBlockEntities.PILE.get()).ifPresent(entity -> entity.setHiddenStates(state, savedAboveState, byPlayer));

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
        removePileOrSnow(level, pos, state, level.getBlockEntity(pos, TFCBlockEntities.PILE.get()).orElse(null));
    }

    /**
     * @param snowPile The snow pile block entity present in the world, possibly null.
     */
    public static void removePileOrSnow(LevelAccessor level, BlockPos pos, BlockState state, @Nullable PileBlockEntity snowPile)
    {
        final int layers = state.getValue(SnowLayerBlock.LAYERS);
        if (layers > 1)
        {
            // Remove layers, but keep the snow block intact
            level.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1), Block.UPDATE_ALL);
        }
        else if (state.getBlock() == Blocks.SNOW)
        {
            // Remove a single snow layer block
            level.removeBlock(pos, false);
        }
        else if (snowPile != null)
        {
            // Otherwise, remove a snow pile, restoring the internal states
            final BlockPos above = pos.above();

            level.setBlock(pos, snowPile.getInternalState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            if (snowPile.getAboveState() != null && level.isEmptyBlock(above))
            {
                level.setBlock(above, snowPile.getAboveState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            }

            // Update neighbors shapes from the bottom block (this is important to get grass blocks to adjust to snowy/non-snowy states)
            snowPile.getInternalState().updateNeighbourShapes(level, pos, Block.UPDATE_CLIENTS);
            level.getBlockState(above).updateNeighbourShapes(level, above, Block.UPDATE_CLIENTS);

            // Block ticks after both blocks are placed
            level.blockUpdated(pos, snowPile.getInternalState().getBlock());
            if (snowPile.getAboveState() != null)
            {
                level.blockUpdated(above, snowPile.getAboveState().getBlock());
            }
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
        @Nullable final PileBlockEntity snowPile =
            level.getBlockEntity(pos, TFCBlockEntities.PILE.get()).orElse(null); // Store the blockentity before it is removed
        super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        removePileOrSnow(level, pos, state, snowPile);

        return true; // Cause drops and other stuff to occur
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
}
