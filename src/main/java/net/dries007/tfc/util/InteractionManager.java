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

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.events.StartFireEvent;

/**
 * This exists due to problems in handling right click events
 * Forge provides a right click block event. This works for intercepting would-be calls to {@link net.minecraft.block.BlockState#use(World, PlayerEntity, Hand, BlockRayTraceResult)}
 * However, this cannot be used (maintaining vanilla behavior) for item usages, or calls to {@link net.minecraft.item.ItemStack#onItemUse(ItemUseContext, Function)}, as the priority of those two behaviors are very different (blocks take priority, cancelling the event with an item behavior forces the item to take priority
 *
 * This is in lieu of a system such as https://github.com/MinecraftForge/MinecraftForge/pull/6615
 */
public final class InteractionManager
{
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final List<Entry> ACTIONS = new ArrayList<>();
    private static final IndirectHashCollection<Item, Entry> CACHE = new IndirectHashCollection<>(wrapper -> wrapper.keyExtractor.get());

    @SuppressWarnings("deprecation")
    public static void setup()
    {
        register(TFCTags.Items.THATCH_BED_HIDES, (stack, context) -> {
            final World world = context.getLevel();
            final PlayerEntity player = context.getPlayer();
            if (!world.isClientSide() && player != null)
            {
                final BlockPos basePos = context.getClickedPos();
                final Direction facing = context.getHorizontalDirection();
                final BlockState bed = TFCBlocks.THATCH_BED.get().defaultBlockState();
                for (Direction direction : new Direction[] {facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise()})
                {
                    final BlockPos headPos = basePos.relative(direction, 1);
                    if (world.getBlockState(basePos).is(TFCTags.Blocks.THATCH_BED_THATCH) && world.getBlockState(headPos).is(TFCTags.Blocks.THATCH_BED_THATCH))
                    {
                        final BlockPos playerPos = player.blockPosition();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            world.setBlock(basePos, bed.setValue(ThatchBedBlock.PART, BedPart.FOOT).setValue(ThatchBedBlock.FACING, direction), 18);
                            world.setBlock(headPos, bed.setValue(ThatchBedBlock.PART, BedPart.HEAD).setValue(ThatchBedBlock.FACING, direction.getOpposite()), 18);
                            stack.shrink(1);
                            return ActionResultType.SUCCESS;
                        }

                    }
                }
            }
            return ActionResultType.FAIL;
        });

        register(TFCTags.Items.STARTS_FIRES_WITH_DURABILITY, (stack, context) -> {
            final PlayerEntity player = context.getPlayer();
            final World world = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            if (player != null && StartFireEvent.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), player, stack))
            {
                if (!player.isCreative())
                {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        });

        register(TFCTags.Items.STARTS_FIRES_WITH_ITEMS, (stack, context) -> {
            final PlayerEntity playerEntity = context.getPlayer();
            if (playerEntity instanceof ServerPlayerEntity)
            {
                final World world = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
                if (!player.isCreative())
                    stack.shrink(1);
                if (StartFireEvent.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), player, stack))
                    return ActionResultType.SUCCESS;
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
                final World world = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final BlockState stateAt = world.getBlockState(blockContext.getClickedPos());
                if (stateAt.is(TFCTags.Blocks.CAN_BE_SNOW_PILED))
                {
                    SnowPileBlock.convertToPile(world, pos, stateAt);
                    BlockState placedState = world.getBlockState(pos);
                    SoundType placementSound = placedState.getSoundType(world, pos, player);
                    world.playSound(player, pos, placedState.getSoundType(world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                    if (player == null || !player.abilities.instabuild)
                    {
                        stack.shrink(1);
                    }

                    ActionResultType result = ActionResultType.sidedSuccess(world.isClientSide);
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

        register(Items.CHARCOAL, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (player != null && !player.abilities.mayBuild)
            {
                return ActionResultType.PASS;
            }
            else
            {
                final World world = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final BlockState stateAt = world.getBlockState(pos);
                if (stateAt.is(TFCBlocks.CHARCOAL_PILE.get()))
                {
                    int layers = stateAt.getValue(CharcoalPileBlock.LAYERS);
                    if (layers != 8)
                    {
                        world.setBlockAndUpdate(pos, stateAt.setValue(CharcoalPileBlock.LAYERS, layers + 1));
                        Helpers.playSound(world, pos, TFCSounds.CHARCOAL_PILE_PLACE.get());
                        return ActionResultType.SUCCESS;
                    }
                }
                if (world.isEmptyBlock(pos.above()) && stateAt.isFaceSturdy(world, pos, Direction.UP))
                {
                    world.setBlockAndUpdate(pos.above(), TFCBlocks.CHARCOAL_PILE.get().defaultBlockState());
                    Helpers.playSound(world, pos, TFCSounds.CHARCOAL_PILE_PLACE.get());
                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.FAIL;
            }
        });

        // Log pile creation and insertion.
        // Note: sneaking will always bypass the log pile block onUse method - that is why we have to handle some insertion here.
        // - holding log, targeting block, shift click = place log pile
        // - holding log, targeting log pile, shift click = insert all
        // - holding log, targeting log pile, click normally = insert one
        final BlockItemPlacement logPilePlacement = new BlockItemPlacement(() -> Items.AIR, TFCBlocks.LOG_PILE);
        register(TFCTags.Items.LOG_PILE_LOGS, (stack, context) -> {
            final PlayerEntity player = context.getPlayer();
            if (player != null && player.isShiftKeyDown())
            {
                final World world = context.getLevel();
                final Direction direction = context.getClickedFace();
                final BlockPos posClicked = context.getClickedPos();
                final BlockState stateClicked = world.getBlockState(posClicked);
                final BlockPos relativePos = posClicked.relative(direction);

                // If we're targeting a log pile, we can do one of two insertion operations
                if (stateClicked.is(TFCBlocks.LOG_PILE.get()))
                {
                    final LogPileTileEntity te = Helpers.getTileEntity(world, posClicked, LogPileTileEntity.class);
                    return Helpers.getCapability(te, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(cap -> {
                        ItemStack insertStack = stack.copy();
                        insertStack = Helpers.insertAllSlots(cap, insertStack);
                        if (insertStack.getCount() < stack.getCount()) // Some logs were inserted
                        {
                            if (!world.isClientSide())
                            {
                                Helpers.playSound(world, relativePos, SoundEvents.WOOD_PLACE);
                                stack.setCount(insertStack.getCount());
                            }
                            return ActionResultType.SUCCESS;
                        }
                        return ActionResultType.FAIL;
                    }).orElse(ActionResultType.PASS);
                }
                else
                {
                    // Trying to place a log pile.
                    final ItemStack insertStack = stack.copy();
                    final ActionResultType result = logPilePlacement.onItemUse(stack, context);
                    if (result.consumesAction())
                    {
                        final LogPileTileEntity te = Helpers.getTileEntity(world, relativePos, LogPileTileEntity.class);
                        Helpers.getCapability(te, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                            insertStack.setCount(1);
                            cap.insertItem(0, insertStack, false);
                        });
                    }
                    return result;
                }
            }
            return ActionResultType.PASS;
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

        register(TFCTags.Items.CLAY_KNAPPING, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (stack.getCount() > 4)
            {
                if (player instanceof ServerPlayerEntity)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, TFCContainerProviders.CLAY_KNAPPING);
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        });

        register(TFCTags.Items.FIRE_CLAY_KNAPPING, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (stack.getCount() > 4)
            {
                if (player instanceof ServerPlayerEntity)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, TFCContainerProviders.FIRE_CLAY_KNAPPING);
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        });

        register(TFCTags.Items.LEATHER_KNAPPING, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (player != null && player.inventory.contains(TFCTags.Items.KNIVES))
            {
                if (player instanceof ServerPlayerEntity)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, TFCContainerProviders.LEATHER_KNAPPING);
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        });

        register(TFCTags.Items.ROCK_KNAPPING, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (stack.getCount() > 1)
            {
                if (player instanceof ServerPlayerEntity)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, TFCContainerProviders.ROCK_KNAPPING);
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        });
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
        ACTIONS.add(new Entry(action, stack -> stack.getItem().is(tag), tag::getValues));
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
                    return result == ActionResultType.PASS ? Optional.empty() : Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    public static void reload()
    {
        CACHE.reload(ACTIONS);
    }

    /**
     * Return {@link ActionResultType#PASS} to allow normal right click handling
     */
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
