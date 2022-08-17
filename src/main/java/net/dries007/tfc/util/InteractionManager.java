/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
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
    private static final IndirectHashCollection<Item, Entry> CACHE = IndirectHashCollection.create(e -> Arrays.stream(e.item.getItems()).map(ItemStack::getItem).toList(), () -> ACTIONS);

    // Public API

    /**
     * Register an interaction for a block item placement. Will only target blocks using the selected item.
     *
     * This method is safe to call during parallel mod loading.
     */
    public static void register(BlockItemPlacement wrapper)
    {
        register(new Entry(wrapper, Ingredient.of(wrapper.getItem()), true, false));
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     *
     * @see #register(Ingredient, boolean, boolean, OnItemUseAction)
     */
    public static void register(Ingredient item, OnItemUseAction action)
    {
        register(item, false, action);
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     *
     * @see #register(Ingredient, boolean, boolean, OnItemUseAction)
     */
    public static void register(Ingredient item, boolean targetAir, OnItemUseAction action)
    {
        register(item, true, targetAir, action);
    }

    /**
     * Register an interaction. This method is safe to call during parallel mod loading.
     *
     * @param item         The items this action should apply to
     * @param targetBlocks if this action should trigger when targeting a block with the item
     * @param targetAir    if this action should trigger when targeting air with the item
     * @param action       The action to run.
     */
    public static void register(Ingredient item, boolean targetBlocks, boolean targetAir, OnItemUseAction action)
    {
        register(new Entry(action, item, targetBlocks, targetAir));
    }


    /**
     * Registers TFC's interactions.
     */
    public static void registerDefaultInteractions()
    {
        register(Ingredient.of(TFCTags.Items.THATCH_BED_HIDES), false, (stack, context) -> {
            final Level level = context.getLevel();
            final Player player = context.getPlayer();
            if (!level.isClientSide() && player != null)
            {
                final BlockPos basePos = context.getClickedPos();
                final BlockState baseState = level.getBlockState(basePos);
                final Direction facing = context.getHorizontalDirection();
                final BlockState bed = TFCBlocks.THATCH_BED.get().defaultBlockState();
                for (Direction direction : new Direction[] {facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise()})
                {
                    final BlockPos headPos = basePos.relative(direction, 1);
                    final BlockState headState = level.getBlockState(headPos);
                    if (Helpers.isBlock(baseState, TFCTags.Blocks.THATCH_BED_THATCH) && Helpers.isBlock(headState, TFCTags.Blocks.THATCH_BED_THATCH))
                    {
                        final BlockPos playerPos = player.blockPosition();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            level.setBlock(basePos, bed.setValue(ThatchBedBlock.PART, BedPart.FOOT).setValue(ThatchBedBlock.FACING, direction), 18);
                            level.setBlock(headPos, bed.setValue(ThatchBedBlock.PART, BedPart.HEAD).setValue(ThatchBedBlock.FACING, direction.getOpposite()), 18);
                            level.getBlockEntity(headPos, TFCBlockEntities.THATCH_BED.get()).ifPresent(entity -> entity.setBed(headState, baseState, stack.split(1)));
                            return InteractionResult.SUCCESS;
                        }

                    }
                }
            }
            return InteractionResult.FAIL;
        });

        register(Ingredient.of(TFCTags.Items.STARTS_FIRES_WITH_DURABILITY), false, (stack, context) -> {
            final Player player = context.getPlayer();
            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            if (player != null && StartFireEvent.startFire(level, pos, level.getBlockState(pos), context.getClickedFace(), player, stack))
            {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                if (!player.isCreative())
                {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        register(Ingredient.of(TFCTags.Items.STARTS_FIRES_WITH_ITEMS), false, (stack, context) -> {
            final Player playerEntity = context.getPlayer();
            if (playerEntity instanceof final ServerPlayer player)
            {
                final Level level = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                if (!player.isCreative())
                    stack.shrink(1);
                if (StartFireEvent.startFire(level, pos, level.getBlockState(pos), context.getClickedFace(), player, stack))
                    return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        });

        register(Ingredient.of(Items.SNOW), false, (stack, context) -> {
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

        register(Ingredient.of(Items.CHARCOAL), false, (stack, context) -> {
            Player player = context.getPlayer();
            if (player != null && !player.getAbilities().mayBuild)
            {
                return InteractionResult.PASS;
            }
            else
            {
                final Level level = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                final BlockState stateAt = level.getBlockState(pos);
                final Block pile = TFCBlocks.CHARCOAL_PILE.get();
                if (player != null && (player.blockPosition().equals(pos) || (player.blockPosition().equals(pos.above()) && Helpers.isBlock(stateAt, pile) && stateAt.getValue(CharcoalPileBlock.LAYERS) == 8)))
                {
                    return InteractionResult.FAIL;
                }
                if (Helpers.isBlock(stateAt, pile))
                {
                    int layers = stateAt.getValue(CharcoalPileBlock.LAYERS);
                    if (layers != 8)
                    {
                        stack.shrink(1);
                        level.setBlockAndUpdate(pos, stateAt.setValue(CharcoalPileBlock.LAYERS, layers + 1));
                        Helpers.playSound(level, pos, TFCSounds.CHARCOAL.getPlaceSound());
                        return InteractionResult.SUCCESS;
                    }
                }
                if (level.isEmptyBlock(pos.above()) && stateAt.isFaceSturdy(level, pos, Direction.UP))
                {
                    stack.shrink(1);
                    level.setBlockAndUpdate(pos.above(), pile.defaultBlockState());
                    Helpers.playSound(level, pos, TFCSounds.CHARCOAL.getPlaceSound());
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
        register(Ingredient.of(TFCTags.Items.LOG_PILE_LOGS), false, (stack, context) -> {
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
                        .flatMap(entity -> entity.getCapability(Capabilities.ITEM).map(t -> t))
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

                            // if we placed instead, insert logs at the RELATIVE position using the mutated stack
                            final InteractionResult result = logPilePlacement.onItemUse(stack, context);
                            if (result.consumesAction())
                            {
                                // shrinking is handled by the item placement
                                Helpers.insertOne(level, relativePos, TFCBlockEntities.LOG_PILE.get(), insertStack);
                            }
                            return result;
                        }).orElse(InteractionResult.PASS);
                }
                else if (!level.getBlockState(relativePos.below()).isAir())
                {
                    // when placing against a non-pile block
                    final ItemStack stackBefore = stack.copy();
                    final InteractionResult result = logPilePlacement.onItemUse(stack, context); // Consumes the item if successful
                    if (result.consumesAction())
                    {
                        Helpers.insertOne(level, relativePos, TFCBlockEntities.LOG_PILE.get(), stackBefore);
                    }
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

        register(Ingredient.of(TFCTags.Items.SCRAPABLE), false, (stack, context) -> {
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
                        .map(entity -> entity.getCapability(Capabilities.ITEM).map(cap -> {
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
        register(Ingredient.of(TFCTags.Items.CLAY_KNAPPING), true, createKnappingInteraction((stack, player) -> stack.getCount() >= 5, TFCContainerProviders.CLAY_KNAPPING));
        register(Ingredient.of(TFCTags.Items.FIRE_CLAY_KNAPPING), true, createKnappingInteraction((stack, player) -> stack.getCount() >= 5, TFCContainerProviders.FIRE_CLAY_KNAPPING));
        register(Ingredient.of(TFCTags.Items.LEATHER_KNAPPING), true, createKnappingInteraction((stack, player) -> player.getInventory().contains(TFCTags.Items.KNIVES), TFCContainerProviders.LEATHER_KNAPPING));
        register(Ingredient.of(TFCTags.Items.ROCK_KNAPPING), false, true, createKnappingInteraction((stack, player) -> stack.getCount() >= 2, TFCContainerProviders.ROCK_KNAPPING)); // Don't target blocks for rock knapping, since rock items want to be able to be placed

        // Piles (Ingots + Sheets)
        // Shift + Click = Add to pile (either on the targeted pile, or create a new one)
        // Removal (Non-Shift Click) is handled by the respective pile block
        final BlockItemPlacement ingotPilePlacement = new BlockItemPlacement(() -> Items.AIR, TFCBlocks.INGOT_PILE);
        register(Ingredient.of(TFCTags.Items.PILEABLE_INGOTS), false, (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.isShiftKeyDown())
            {
                final Level level = context.getLevel();
                final Direction direction = context.getClickedFace();
                final BlockPos posClicked = context.getClickedPos();
                final BlockState stateClicked = level.getBlockState(posClicked);
                final BlockPos relativePos = posClicked.relative(direction);

                if (Helpers.isBlock(stateClicked, TFCBlocks.INGOT_PILE.get()))
                {
                    // We clicked on an ingot pile, so attempt to add to the pile
                    final int currentIngots = stateClicked.getValue(IngotPileBlock.COUNT);
                    if (currentIngots < 64)
                    {
                        final ItemStack insertStack = stack.split(1);

                        level.playSound(null, posClicked, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 0.7f, 0.9f + 0.2f * level.getRandom().nextFloat());
                        level.setBlock(posClicked, stateClicked.setValue(IngotPileBlock.COUNT, currentIngots + 1), Block.UPDATE_CLIENTS);
                        level.getBlockEntity(posClicked, TFCBlockEntities.INGOT_PILE.get()).ifPresent(pile -> pile.addIngot(insertStack));
                        return InteractionResult.SUCCESS;
                    }
                    else
                    {
                        // Iterate upwards until we find a non-full ingot pile in the stack
                        BlockPos topPos = posClicked;
                        BlockState topState;
                        do
                        {
                            topPos = topPos.above();
                            topState = level.getBlockState(topPos);
                        } while (Helpers.isBlock(topState, TFCBlocks.INGOT_PILE.get()) && topState.getValue(IngotPileBlock.COUNT) == 64);

                        if (Helpers.isBlock(topState, TFCBlocks.INGOT_PILE.get()))
                        {
                            // We must be at a non-full ingot pile, so we want to place another ingot on this pile instead
                            final ItemStack insertStack = stack.split(1);
                            final int topIngots = topState.getValue(IngotPileBlock.COUNT);

                            level.playSound(null, topPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 0.7f, 0.9f + 0.2f * level.getRandom().nextFloat());
                            level.setBlock(topPos, topState.setValue(IngotPileBlock.COUNT, topIngots + 1), Block.UPDATE_CLIENTS);
                            level.getBlockEntity(topPos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(topPile -> topPile.addIngot(insertStack));
                            return InteractionResult.SUCCESS;
                        }
                        else if (topState.isAir())
                        {
                            // We arrived at something that *isn't* an ingot pile, and we want to try and place another ingot on top
                            // We check for air, as we may have run into something solid - don't place anything if that's the case
                            final ItemStack stackBefore = stack.copy();
                            final BlockPos topOfIngotPilePos = topPos.below();
                            final UseOnContext topOfIngotPileContext = new UseOnContext(player, context.getHand(), new BlockHitResult(Vec3.ZERO, Direction.UP, topOfIngotPilePos, false));
                            final InteractionResult result = ingotPilePlacement.onItemUse(stack, topOfIngotPileContext);
                            if (result.consumesAction())
                            {
                                // Shrinking is already handled by the placement onItemUse() call, we just need to insert the stack
                                stackBefore.setCount(1);
                                level.getBlockEntity(topPos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(topPile -> topPile.addIngot(stackBefore));
                            }
                            return InteractionResult.SUCCESS;
                        }
                        return InteractionResult.FAIL;
                    }
                }
                else
                {
                    // We clicked on a non-ingot pile, so we want to try and place an ingot pile at the current location.
                    final ItemStack stackBefore = stack.copy();
                    final InteractionResult result = ingotPilePlacement.onItemUse(stack, context);
                    if (result.consumesAction())
                    {
                        // Shrinking is already handled by the placement onItemUse() call, we just need to insert the stack
                        stackBefore.setCount(1);
                        level.getBlockEntity(relativePos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(pile -> pile.addIngot(stackBefore));
                    }
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

        register(Ingredient.of(TFCTags.Items.PILEABLE_SHEETS), false, (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.isShiftKeyDown())
            {
                final Level level = context.getLevel();
                final Direction clickedFace = context.getClickedFace(); // i.e. click on UP
                final Direction sheetFace = clickedFace.getOpposite(); // i.e. place on DOWN

                final BlockPos clickedPos = context.getClickedPos();
                final BlockPos relativePos = clickedPos.relative(clickedFace);

                final BlockState clickedState = level.getBlockState(clickedPos);
                final BlockState relativeState = level.getBlockState(relativePos);

                final BlockPlaceContext blockContext = new BlockPlaceContext(context);
                final BooleanProperty property = DirectionPropertyBlock.getProperty(sheetFace);

                if (blockContext.replacingClickedOnBlock())
                {
                    // Sheets are not allowed to place on replaceable blocks, as it is dependent on the face clicked - but when we click on a replaceable block, that face doesn't make sense.
                    return InteractionResult.FAIL;
                }

                // Sheets behave differently than ingots, because we need to check the targeted face if it's empty or not
                // We assume immediately that we want to target the relative pos and state
                if (Helpers.isBlock(relativeState, TFCBlocks.SHEET_PILE.get()))
                {
                    // We targeted a existing sheet pile, so we need to check if there's an empty space for it
                    if (!relativeState.getValue(property) && BlockItemPlacement.canPlace(blockContext, clickedState) && clickedState.isFaceSturdy(level, clickedPos, clickedFace))
                    {
                        // Add to an existing sheet pile
                        final ItemStack insertStack = stack.split(1);
                        SheetPileBlock.addSheet(level, relativePos, relativeState, sheetFace, insertStack);
                        return InteractionResult.SUCCESS;
                    }
                    else
                    {
                        // No space
                        return InteractionResult.FAIL;
                    }
                }
                // This is where we assert that we can only replace replaceable blocks
                else if (level.getBlockState(relativePos).canBeReplaced(blockContext))
                {
                    // Want to place a new sheet at the above location
                    final BlockState placingState = TFCBlocks.SHEET_PILE.get().defaultBlockState().setValue(property, true);
                    if (BlockItemPlacement.canPlace(blockContext, placingState) && clickedState.isFaceSturdy(level, clickedPos, clickedFace))
                    {
                        final ItemStack insertStack = stack.split(1);
                        SheetPileBlock.addSheet(level, relativePos, placingState, sheetFace, insertStack);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        register(Ingredient.of(TFCTags.Items.SALAD_BOWLS), true, (stack, context) -> {
            // Only open salads when shift key is down
            // Normally when consuming bowl food (like salads), you'll be holding right click down causing the salad gui to immediately open
            // That feels bad to use, so we require shift to open salads - better in the common case
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            {
                if (context.getPlayer() instanceof ServerPlayer player)
                {
                    NetworkHooks.openGui(player, TFCContainerProviders.SALAD);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    public static OnItemUseAction createKnappingInteraction(BiPredicate<ItemStack, Player> condition, ItemStackContainerProvider container)
    {
        return (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && condition.test(stack, player) && context.getClickedPos().equals(BlockPos.ZERO))
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
                if ((isTargetingAir ? entry.targetAir : entry.targetBlocks) && entry.item.test(stack))
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

    private record Entry(OnItemUseAction action, Ingredient item, boolean targetBlocks, boolean targetAir) {}
}
