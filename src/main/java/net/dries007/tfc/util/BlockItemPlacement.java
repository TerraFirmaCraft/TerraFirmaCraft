/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

/**
 * This is a fake {@link BlockItem} copy pasta for a vanilla item that we want to behave like a block item for a specific block.
 */
public class BlockItemPlacement implements InteractionManager.OnItemUseAction
{
    public static boolean placeBlock(BlockPlaceContext context, BlockState state)
    {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    public static boolean canPlace(BlockPlaceContext context, BlockState stateToPlace)
    {
        return canPlace(context, stateToPlace, context.getClickedPos());
    }

    public static boolean canPlace(BlockPlaceContext context, BlockState stateToPlace, BlockPos pos)
    {
        final Player player = context.getPlayer();
        final CollisionContext selectionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (stateToPlace.canSurvive(context.getLevel(), pos)) && context.getLevel().isUnobstructed(stateToPlace, pos, selectionContext);
    }

    /**
     * Copy pasta from {@link BlockItem#updateBlockStateFromTag}
     */
    public static BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state)
    {
        final BlockItemStateProperties properties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        if (properties.isEmpty())
        {
            return state;
        }
        else
        {
            BlockState newState = properties.apply(state);
            if (newState != state)
            {
                level.setBlock(pos, newState, 2);
            }
            return newState;
        }
    }

    private final Supplier<? extends Item> item;
    private final Supplier<? extends Block> block;

    public BlockItemPlacement(Supplier<? extends Item> item, Supplier<? extends Block> block)
    {
        this.item = item;
        this.block = block;
    }

    public Iterable<Item> getItems()
    {
        return Collections.singleton(item.get());
    }

    public Item getItem()
    {
        return item.get();
    }

    /**
     * Copy pasta from {@link ItemStack#useOn(UseOnContext)}
     */
    @Override
    public InteractionResult onItemUse(ItemStack stack, UseOnContext context)
    {
        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().mayBuild)
        {
            return InteractionResult.PASS;
        }
        else
        {
            Item item = getItem();
            InteractionResult result = place(new BlockPlaceContext(context));
            if (player != null && result.consumesAction())
            {
                player.awardStat(Stats.ITEM_USED.get(item));
            }
            return result;
        }
    }

    /**
     * Copy pasta from {@link BlockItem#place(BlockPlaceContext)}. Split off into {@link #postPlacement(BlockPlaceContext)} because it's helpful.
     */
    public InteractionResult place(BlockPlaceContext context)
    {
        if (!context.canPlace())
        {
            return InteractionResult.FAIL;
        }

        final BlockState placementState = getPlacementState(context);
        if (placementState == null)
        {
            return InteractionResult.FAIL;
        }
        else if (!placeBlock(context, placementState))
        {
            return InteractionResult.FAIL;
        }
        else
        {
            return postPlacement(context);
        }
    }

    public InteractionResult postPlacement(BlockPlaceContext context)
    {
        return postPlacement(context, context.getClickedPos());
    }

    /**
     * Handles consuming an item, playing the correct placement sound, and returning {@code SUCCESS}.
     */
    public InteractionResult postPlacement(BlockPlaceContext context, BlockPos pos)
    {
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final ItemStack stack = context.getItemInHand();
        final BlockState placedState = level.getBlockState(pos);
        final SoundType placementSound = placedState.getSoundType(level, pos, player);

        level.playSound(player, pos, placedState.getSoundType(level, pos, player).getPlaceSound(), SoundSource.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);

        if (player == null || !player.getAbilities().instabuild)
        {
            stack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context)
    {
        BlockState placementState = block.get().getStateForPlacement(context);
        return placementState != null && canPlace(context, placementState) ? placementState : null;
    }
}
