/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collections;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

/**
 * This is a fake {@link BlockItem} copy pasta for a vanilla item that we want to behave like a block item for a specific block.
 */
public class BlockItemPlacement implements InteractionManager.OnItemUseAction
{
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
     * Copy paste from {@link ItemStack#useOn(ItemUseContext)}
     */
    @Override
    public ActionResultType onItemUse(ItemStack stack, ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.abilities.mayBuild)
        {
            return ActionResultType.PASS;
        }
        else
        {
            Item item = getItem();
            ActionResultType result = place(new BlockItemUseContext(context));
            if (player != null && result.consumesAction())
            {
                player.awardStat(Stats.ITEM_USED.get(item));
            }
            return result;
        }
    }

    /**
     * Copy pasta from {@link net.minecraft.item.BlockItem#place(BlockItemUseContext)}
     */
    public ActionResultType place(BlockItemUseContext context)
    {
        if (!context.canPlace())
        {
            return ActionResultType.FAIL;
        }
        else
        {
            BlockState placementState = getPlacementState(context);
            if (placementState == null)
            {
                return ActionResultType.FAIL;
            }
            else if (!this.placeBlock(context, placementState))
            {
                return ActionResultType.FAIL;
            }
            else
            {
                BlockPos pos = context.getClickedPos();
                World world = context.getLevel();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getItemInHand();
                BlockState placedState = world.getBlockState(pos);
                Block placedBlock = placedState.getBlock();
                if (placedBlock == placementState.getBlock())
                {
                    placedState = updateBlockStateFromTag(pos, world, stack, placedState);
                    BlockItem.updateCustomBlockEntityTag(world, player, pos, stack);
                    placedBlock.setPlacedBy(world, pos, placedState, player, stack);
                    if (player instanceof ServerPlayerEntity)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
                    }
                }

                SoundType placementSound = placedState.getSoundType(world, pos, player);
                world.playSound(player, pos, placedState.getSoundType(world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                if (player == null || !player.abilities.instabuild)
                {
                    stack.shrink(1);
                }

                return ActionResultType.sidedSuccess(world.isClientSide);
            }
        }
    }

    @Nullable
    protected BlockState getPlacementState(BlockItemUseContext context)
    {
        BlockState placementState = block.get().getStateForPlacement(context);
        return placementState != null && canPlace(context, placementState) ? placementState : null;
    }

    protected boolean placeBlock(BlockItemUseContext context, BlockState state)
    {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    protected boolean canPlace(BlockItemUseContext context, BlockState stateToPlace)
    {
        PlayerEntity player = context.getPlayer();
        ISelectionContext selectionContext = player == null ? ISelectionContext.empty() : ISelectionContext.of(player);
        return (stateToPlace.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(stateToPlace, context.getClickedPos(), selectionContext);
    }

    /**
     * Copy pasta from {@link BlockItem#updateBlockStateFromTag(BlockPos, World, ItemStack, BlockState)}
     */
    @SuppressWarnings("ALL")
    private BlockState updateBlockStateFromTag(BlockPos pos, World world, ItemStack stack, BlockState state)
    {
        BlockState newState = state;
        CompoundNBT nbt = stack.getTag();
        if (nbt != null)
        {
            CompoundNBT blockStateNbt = nbt.getCompound("BlockStateTag");
            StateContainer<Block, BlockState> container = state.getBlock().getStateDefinition();

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
}
