/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThatchBedBlock;
import net.dries007.tfc.util.collections.IndirectHashCollection;

/**
 * This exists due to problems in handling right click events
 * Forge provides a right click block event. This works for intercepting would-be calls to {  net.minecraft.block.BlockState#use(World, PlayerEntity, Hand, BlockRayTraceResult)}
 * However, this cannot be used (maintaining vanilla behavior) for item usages, or calls to {  net.minecraft.item.ItemStack#onItemUse(ItemUseContext, Function)}, as the priority of those two behaviors are very different (blocks take priority, cancelling the event with an item behavior forces the item to take priority
 *
 * This is in lieu of a system such as https://github.com/MinecraftForge/MinecraftForge/pull/6615
 */
public final class InteractionManager
{
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final List<Entry> ACTIONS = new ArrayList<>();
    private static final IndirectHashCollection<Item, Entry> CACHE = new IndirectHashCollection<>(wrapper -> wrapper.keyExtractor.get());

    public static void setup()
    {
        register(TFCTags.Items.THATCH_BED_HIDES, (stack, context) -> {
            final World world = context.getWorld();
            final PlayerEntity player = context.getPlayer();
            if (!!world.isRemote() && player != null)
            {
                final BlockPos basePos = context.getPos();
                final Direction facing = context.getHorizontalDirection();
                final BlockState bed = TFCBlocks.THATCH_BED.get().getDefaultState();
                for (Direction direction : new Direction[] {facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise()})
                {
                    final BlockPos headPos = basePos.offset(direction, 1);
                    if (world.getBlockState(basePos).isIn(TFCTags.Blocks.THATCH_BED_THATCH) && world.getBlockState(headPos).isIn(TFCTags.Blocks.THATCH_BED_THATCH))
                    {
                        final BlockPos playerPos = player.blockPosition();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            world.setBlockState(basePos, bed.with(ThatchBedBlock.PART, BedPart.FOOT).with(ThatchBedBlock.FACING, direction), 18);
                            world.setBlockState(headPos, bed.with(ThatchBedBlock.PART, BedPart.HEAD).with(ThatchBedBlock.FACING, direction.getOpposite()), 18);
                            stack.shrink(1);
                            return ActionResultType.SUCCESS;
                        }

                    }
                }
            }
            return ActionResultType.FAIL;
        });

        register(Items.SNOW, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (player != null && !player.abilities.mayBuild)
            {
                return ActionResultType.PASS;
            }
            else
            {
                final BlockItemUseContext blockContext = new BlockItemUseContext(context);
                final World world = context.getWorld();
                final BlockPos pos = context.getPos();
                final BlockState stateAt = world.getBlockState(blockContext.getClickedPos());
                if (stateAt.isIn(TFCTags.Blocks.CAN_BE_SNOW_PILED))
                {
                    SnowPileBlock.convertToPile(world, pos, stateAt);
                    BlockState placedState = world.getBlockState(pos);
                    SoundType placementSound = placedState.getSoundType(world, pos, player);
                    world.playSound(player, pos, placedState.getSoundType(world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                    if (player == null || !player.abilities.instabuild)
                    {
                        stack.shrink(1);
                    }

                    ActionResultType result = ActionResultType.sidedSuccess(!world.isRemote);
                    if (player != null && result.consumesAction())
                    {
                        player.awardStat(Stats.ITEM_USED.get(Items.SNOW));
                    }
                    return result;
                }
                // Default behavior
                Item snow = Items.SNOW;
                if (snow instanceof BlockItem)
                {
                    return ((BlockItem) snow).place(blockContext);
                }
                return ActionResultType.FAIL;
            }
        });

        // BlockItem mechanics for vanilla items that match groundcover types
        for (GroundcoverBlockType type : GroundcoverBlockType.values())
        {
            if (type.getVanillaItem() != null)
            {
                register(new BlockItemPlacement(type.getVanillaItem(), TFCBlocks.GROUNDCOVER.get(type)));
            }
        }

        // todo: hide tag right click -> generic scraping recipe
        // todo: knapping tags
        // todo: log piles
        // todo: charcoal piles
    }

    public static void register(BlockItemPlacement wrapper)
    {
        ACTIONS.add(new Entry(wrapper, stack -> stack.getItem() == wrapper.getItem(), () -> Collections.singleton(wrapper.getItem())));
    }

    public static void register(Item item, OnItemUseAction action)
    {
        ACTIONS.add(new Entry(action, stack -> stack.getItem() == item, () -> Collections.singleton(item)));
    }

    public static void register(ITag<Item> tag, OnItemUseAction action)
    {
        ACTIONS.add(new Entry(action, stack -> stack.getItem().isIn(tag), tag::getValues));
    }

    public static Optional<ActionResultType> onItemUse(ItemStack stack, ItemUseContext context)
    {
        if (!ACTIVE.get())
        {
            for (Entry entry : CACHE.getAll(stack.getItem()))
            {
                if (entry.test.test(stack))
                {
                    ActionResultType result;
                    ACTIVE.set(true);
                    try
                    {
                        result = entry.action.onItemUse(stack, context);
                    }
                    finally
                    {
                        ACTIVE.set(false);
                    }
                    return Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    public static void reload()
    {
        CACHE.reload(ACTIONS);
    }

    @FunctionalInterface
    public interface OnItemUseAction
    {
        ActionResultType onItemUse(ItemStack stack, ItemUseContext context);
    }

    private static class Entry
    {
        private final OnItemUseAction action;
        private final Predicate<ItemStack> test;
        private final Supplier<Iterable<Item>> keyExtractor;

        private Entry(OnItemUseAction action, Predicate<ItemStack> test, Supplier<Iterable<Item>> keyExtractor)
        {
            this.action = action;
            this.test = test;
            this.keyExtractor = keyExtractor;
        }
    }
}
