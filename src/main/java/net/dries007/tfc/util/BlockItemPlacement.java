/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collections;
import java.util.function.Supplier;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
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
        Player player = context.getPlayer();
        CollisionContext selectionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (stateToPlace.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(stateToPlace, context.getClickedPos(), selectionContext);
    }

    /**
     * Copy pasta from {@link BlockItem#updateBlockStateFromTag(BlockPos, World, ItemStack, BlockState)}
     */
    @SuppressWarnings("ALL")
    public static BlockState updateBlockStateFromTag(BlockPos pos, Level world, ItemStack stack, BlockState state)
    {
        BlockState newState = state;
        CompoundTag nbt = stack.getTag();
        if (nbt != null)
        {
            CompoundTag blockStateNbt = nbt.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> container = state.getBlock().getStateDefinition();

            for (String propertyKey : blockStateNbt.getAllKeys())
            {
                Property<?> property = container.getProperty(propertyKey);
                if (property != null)
                {
                    String s1 = blockStateNbt.get(propertyKey).getAsString();
                    newState = updateState(newState, property, s1);
                }
            }
        }

        if (newState != state)
        {
            world.setBlock(pos, newState, 2);
        }
        return newState;
    }

    /**
     * Copy pasta from {@link BlockItem#updateState(BlockState, Property, String)}
     */
    private static <T extends Comparable<T>> BlockState updateState(BlockState state, Property<T> property, String value)
    {
        return property.getValue(value).map(valueIn -> state.setValue(property, valueIn)).orElse(state);
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
     * Copy paste from {@link ItemStack#useOn(UseOnContext)}
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
     * Copy pasta from {@link BlockItem#place(BlockPlaceContext)}
     */
    public InteractionResult place(BlockPlaceContext context)
    {
        if (!context.canPlace())
        {
            return InteractionResult.FAIL;
        }
        else
        {
            BlockState placementState = getPlacementState(context);
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
                BlockPos pos = context.getClickedPos();
                Level world = context.getLevel();
                Player player = context.getPlayer();
                ItemStack stack = context.getItemInHand();
                BlockState placedState = world.getBlockState(pos);
                Block placedBlock = placedState.getBlock();
                if (placedBlock == placementState.getBlock())
                {
                    placedState = updateBlockStateFromTag(pos, world, stack, placedState);
                    BlockItem.updateCustomBlockEntityTag(world, player, pos, stack);
                    placedBlock.setPlacedBy(world, pos, placedState, player, stack);
                    if (player instanceof ServerPlayer)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, stack);
                    }
                }

                SoundType placementSound = placedState.getSoundType(world, pos, player);
                world.playSound(player, pos, placedState.getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                if (player == null || !player.getAbilities().instabuild)
                {
                    stack.shrink(1);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context)
    {
        BlockState placementState = block.get().getStateForPlacement(context);
        return placementState != null && canPlace(context, placementState) ? placementState : null;
    }
}
