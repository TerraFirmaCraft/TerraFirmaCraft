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
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.events.StartFireEvent;

/**
 * This exists due to problems in handling right click events
 * Forge provides a right click block event. This works for intercepting would-be calls to {@link BlockState#use(Level, Player, InteractionHand, BlockHitResult)}
 * However, this cannot be used (maintaining vanilla behavior) for item usages, or calls to {@link ItemStack#onItemUse(UseOnContext, Function)}, as the priority of those two behaviors are very different (blocks take priority, cancelling the event with an item behavior forces the item to take priority
 *
 * This is in lieu of a system such as https://github.com/MinecraftForge/MinecraftForge/pull/6615
 */
public final class InteractionManager
{
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final List<Entry> ACTIONS = new ArrayList<>();
    private static final IndirectHashCollection<Item, Entry> CACHE = new IndirectHashCollection<>(wrapper -> wrapper.keyExtractor.get());

    public static void registerDefaultInteractions()
    {
        register(TFCTags.Items.THATCH_BED_HIDES, false, (stack, context) -> {
            final Level world = context.getLevel();
            final Player player = context.getPlayer();
            if (!world.isClientSide() && player != null)
            {
                final BlockPos basePos = context.getClickedPos();
                final Direction facing = context.getHorizontalDirection();
                final BlockState bed = TFCBlocks.THATCH_BED.get().defaultBlockState();
                for (Direction direction : new Direction[] {facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise()})
                {
                    final BlockPos headPos = basePos.relative(direction, 1);
                    if (Helpers.isBlock(world.getBlockState(basePos), TFCTags.Blocks.THATCH_BED_THATCH) && Helpers.isBlock(world.getBlockState(headPos), TFCTags.Blocks.THATCH_BED_THATCH))
                    {
                        final BlockPos playerPos = player.blockPosition();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            world.setBlock(basePos, bed.setValue(ThatchBedBlock.PART, BedPart.FOOT).setValue(ThatchBedBlock.FACING, direction), 18);
                            world.setBlock(headPos, bed.setValue(ThatchBedBlock.PART, BedPart.HEAD).setValue(ThatchBedBlock.FACING, direction.getOpposite()), 18);
                            stack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }

                    }
                }
            }
            return InteractionResult.FAIL;
        });

        register(TFCTags.Items.STARTS_FIRES_WITH_DURABILITY, false, (stack, context) -> {
            final Player player = context.getPlayer();
            final Level world = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            if (player != null && StartFireEvent.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), player, stack))
            {
                world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                if (!player.isCreative())
                {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        register(TFCTags.Items.STARTS_FIRES_WITH_ITEMS, false, (stack, context) -> {
            final Player playerEntity = context.getPlayer();
            if (playerEntity instanceof final ServerPlayer player)
            {
                final Level world = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                if (!player.isCreative())
                    stack.shrink(1);
                if (StartFireEvent.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), player, stack))
                    return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        });

        register(Items.SNOW, false, (stack, context) -> {
            Player player = context.getPlayer();
            if (player != null && !player.getAbilities().mayBuild)
            {
                return InteractionResult.PASS;
            }
            else
            {
                final BlockPlaceContext blockContext = new BlockPlaceContext(context);
                final Level level = blockContext.getLevel();
                final BlockPos pos = blockContext.getClickedPos();
                final BlockState stateAt = level.getBlockState(blockContext.getClickedPos());
                if (SnowPileBlock.canPlaceSnowPile(level, pos, stateAt))
                {
                    SnowPileBlock.placeSnowPile(level, pos, stateAt, true);
                    final BlockState placedState = level.getBlockState(pos);
                    final SoundType placementSound = placedState.getSoundType(level, pos, player);
                    level.playSound(player, pos, placedState.getSoundType(level, pos, player).getPlaceSound(), SoundSource.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                    if (player == null || !player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                    }

                    InteractionResult result = InteractionResult.sidedSuccess(level.isClientSide);
                    if (player != null && result.consumesAction())
                    {
                        player.awardStat(Stats.ITEM_USED.get(Items.SNOW));
                    }
                    return result;
                }

                // Default behavior
                // Handles layering behavior of both snow piles and snow layers via the blocks replacement / getStateForPlacement
                if (Items.SNOW instanceof BlockItem blockItem)
                {
                    return blockItem.place(blockContext);
                }
                return InteractionResult.FAIL;
            }
        });

        register(Items.CHARCOAL, false, (stack, context) -> {
            Player player = context.getPlayer();
            if (player != null && !player.getAbilities().mayBuild)
            {
                return InteractionResult.PASS;
            }
            else
            {
                final Level world = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final BlockState stateAt = world.getBlockState(pos);
                if (player != null && (player.blockPosition().equals(pos) || player.blockPosition().equals(pos.above()))) return InteractionResult.FAIL;
                if (Helpers.isBlock(stateAt, TFCBlocks.CHARCOAL_PILE.get()))
                {
                    int layers = stateAt.getValue(CharcoalPileBlock.LAYERS);
                    if (layers != 8)
                    {
                        stack.shrink(1);
                        world.setBlockAndUpdate(pos, stateAt.setValue(CharcoalPileBlock.LAYERS, layers + 1));
                        Helpers.playSound(world, pos, TFCSounds.CHARCOAL_PILE_PLACE.get());
                        return InteractionResult.SUCCESS;
                    }
                }
                if (world.isEmptyBlock(pos.above()) && stateAt.isFaceSturdy(world, pos, Direction.UP))
                {
                    stack.shrink(1);
                    world.setBlockAndUpdate(pos.above(), TFCBlocks.CHARCOAL_PILE.get().defaultBlockState());
                    Helpers.playSound(world, pos, TFCSounds.CHARCOAL_PILE_PLACE.get());
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
        });

        // Log pile creation and insertion.
        // Note: sneaking will always bypass the log pile block onUse method - that is why we have to handle some insertion here.
        // - holding log, targeting block, shift click = place log pile
        // - holding log, targeting log pile, shift click = insert all
        // - holding log, targeting log pile, click normally = insert one
        final BlockItemPlacement logPilePlacement = new BlockItemPlacement(() -> Items.AIR, TFCBlocks.LOG_PILE);
        register(TFCTags.Items.LOG_PILE_LOGS, false, (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.isShiftKeyDown())
            {
                final Level level = context.getLevel();
                final Direction direction = context.getClickedFace();
                final BlockPos posClicked = context.getClickedPos();
                final BlockState stateClicked = level.getBlockState(posClicked);
                final BlockPos relativePos = posClicked.relative(direction);

                // If we're targeting a log pile, we can do one of two insertion operations
                if (Helpers.isBlock(stateClicked, TFCBlocks.LOG_PILE.get()))
                {
                    return level.getBlockEntity(posClicked, TFCBlockEntities.LOG_PILE.get())
                        .flatMap(entity -> entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(t -> t))
                        .map(cap -> {
                            ItemStack insertStack = stack.copy();
                            insertStack = Helpers.insertAllSlots(cap, insertStack);
                            if (insertStack.getCount() < stack.getCount()) // Some logs were inserted
                            {
                                if (!level.isClientSide())
                                {
                                    Helpers.playSound(level, relativePos, SoundEvents.WOOD_PLACE);
                                    stack.setCount(insertStack.getCount());
                                }
                                return InteractionResult.SUCCESS;
                            }

                            final InteractionResult result = logPilePlacement.onItemUse(stack, context);
                            if (result.consumesAction())
                            {
                                insertStack.setCount(1);
                                cap.insertItem(0, insertStack, false);
                            }
                            return result;
                        }).orElse(InteractionResult.PASS);
                }
                else if (!level.getBlockState(relativePos.below()).isAir())
                {
                    // Trying to place a log pile.
                    final ItemStack insertStack = stack.copy();
                    final InteractionResult result = logPilePlacement.onItemUse(stack, context);
                    if (result.consumesAction())
                    {
                        level.getBlockEntity(relativePos, TFCBlockEntities.LOG_PILE.get())
                            .flatMap(entity -> entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve())
                            .ifPresent(cap -> {
                                insertStack.setCount(1);
                                cap.insertItem(0, insertStack, false);
                            });
                    }
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

        register(TFCTags.Items.SCRAPABLE, false, (stack, context) -> {
            Level level = context.getLevel();
            ScrapingRecipe recipe = ScrapingRecipe.getRecipe(level, new ItemStackInventory(stack));
            if (recipe != null)
            {
                final BlockPos pos = context.getClickedPos();
                final BlockPos abovePos = pos.above();
                Player player = context.getPlayer();
                if (player != null && context.getClickedFace() == Direction.UP && Helpers.isBlock(level.getBlockState(pos), TFCTags.Blocks.SCRAPING_SURFACE) && level.getBlockState(abovePos).isAir())
                {
                    level.setBlockAndUpdate(abovePos, TFCBlocks.SCRAPING.get().defaultBlockState());
                    level.getBlockEntity(abovePos, TFCBlockEntities.SCRAPING.get())
                        .map(entity -> entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(cap -> {
                            if (!level.isClientSide)
                            {
                                ItemStack insertStack = stack.split(1);
                                stack.setCount(stack.getCount() + cap.insertItem(0, insertStack, false).getCount());
                                entity.setCachedItem(recipe.getResultItem().copy());
                            }
                            return InteractionResult.SUCCESS;
                        }).orElse(InteractionResult.PASS));
                }
            }
            return InteractionResult.PASS;
        });

        // BlockItem mechanics for vanilla items that match groundcover types
        for (GroundcoverBlockType type : GroundcoverBlockType.values())
        {
            if (type.getVanillaItem() != null)
            {
                register(new BlockItemPlacement(type.getVanillaItem(), TFCBlocks.GROUNDCOVER.get(type)));
            }
        }

        // Knapping
        register(TFCTags.Items.CLAY_KNAPPING, true, createKnappingInteraction((stack, player) -> stack.getCount() >= 5, TFCContainerProviders.CLAY_KNAPPING));
        register(TFCTags.Items.FIRE_CLAY_KNAPPING, true, createKnappingInteraction((stack, player) -> stack.getCount() >= 5, TFCContainerProviders.FIRE_CLAY_KNAPPING));
        register(TFCTags.Items.LEATHER_KNAPPING, true, createKnappingInteraction((stack, player) -> player.getInventory().contains(TFCTags.Items.KNIVES), TFCContainerProviders.LEATHER_KNAPPING));
        register(TFCTags.Items.ROCK_KNAPPING, true, createKnappingInteraction((stack, player) -> stack.getCount() >= 2, TFCContainerProviders.ROCK_KNAPPING));
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     */
    public static void register(BlockItemPlacement wrapper)
    {
        register(new Entry(wrapper, stack -> stack.getItem() == wrapper.getItem(), () -> Collections.singleton(wrapper.getItem()), false));
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     */
    public static void register(Item item, boolean targetAir, OnItemUseAction action)
    {
        register(new Entry(action, stack -> stack.getItem() == item, () -> Collections.singleton(item), targetAir));
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     */
    @SuppressWarnings("deprecation")
    public static void register(TagKey<Item> tag, boolean targetAir, OnItemUseAction action)
    {
        register(new Entry(action, stack -> Helpers.isItem(stack.getItem(), tag), () -> Helpers.getAllTagValues(tag, Registry.ITEM), targetAir));
    }

    public static OnItemUseAction createKnappingInteraction(BiPredicate<ItemStack, Player> condition, ItemStackContainerProvider container)
    {
        return (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && condition.test(stack, player))
            {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    NetworkHooks.openGui(serverPlayer, container.of(stack, context.getHand()), ItemStackContainerProvider.write(context.getHand()));
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        };
    }

    public static Optional<InteractionResult> onItemUse(ItemStack stack, UseOnContext context, boolean isTargetingAir)
    {
        if (!ACTIVE.get())
        {
            for (Entry entry : CACHE.getAll(stack.getItem()))
            {
                if ((entry.targetAir() || !isTargetingAir) && entry.test().test(stack))
                {
                    InteractionResult result;
                    ACTIVE.set(true);
                    try
                    {
                        result = entry.action().onItemUse(stack, context);
                    }
                    finally
                    {
                        ACTIVE.set(false);
                    }
                    return result == InteractionResult.PASS ? Optional.empty() : Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    public static void reloadCache()
    {
        CACHE.reload(ACTIONS);
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     */
    private static synchronized void register(Entry entry)
    {
        ACTIONS.add(entry);
    }

    /**
     * Return {@link InteractionResult#PASS} to allow normal right click handling
     */
    @FunctionalInterface
    public interface OnItemUseAction
    {
        InteractionResult onItemUse(ItemStack stack, UseOnContext context);
    }

    private record Entry(OnItemUseAction action, Predicate<ItemStack> test, Supplier<Iterable<Item>> keyExtractor, boolean targetAir) {}
}
