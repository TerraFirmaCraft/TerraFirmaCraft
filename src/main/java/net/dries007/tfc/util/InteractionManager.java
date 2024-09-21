/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThatchBedBlock;
import net.dries007.tfc.common.blocks.devices.DoubleIngotPileBlock;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.blocks.devices.LogPileBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.ingredients.KeyedIngredient;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.KnappingType;
import net.dries007.tfc.util.events.DouseFireEvent;
import net.dries007.tfc.util.events.StartFireEvent;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.*;

/**
 * This handles interactions with generic items and their {@code useItemOn()} behavior. We handle multiple different calls through here:
 * <ul>
 *     <li>Clicking on air uses {@link RightClickItem}, which checks if the interaction supports {@code targetAir}</li>
 *     <li>Using an item on a block intercepts {@link UseItemOnBlockEvent}, at the phase {@code ITEM_AFTER_BLOCK}</li>
 * </ul>
 */
public final class InteractionManager
{
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final List<CachedEntry> ACTIONS = new ArrayList<>();
    private static final List<FallbackEntry> FALLBACKS = new ArrayList<>();
    private static final IndirectHashCollection<Item, CachedEntry> CACHE = IndirectHashCollection.create(e -> e.input.keys(), () -> ACTIONS);

    // Public API

    public static void register(BlockItemPlacement placement)
    {
        register(Ingredient.of(placement.getItem()), Target.BLOCKS, placement);
    }

    public static void register(Ingredient item, OnItemUseAction action)
    {
        register(item, Target.BLOCKS, action);
    }

    public static void register(KeyedIngredient ingredient, OnItemUseAction action)
    {
        register(ingredient, Target.BLOCKS, action);
    }

    public static void register(Ingredient item, Target target, OnItemUseAction action)
    {
        register(KeyedIngredient.of(item), target, action);
    }

    private static synchronized void register(KeyedIngredient input, Target target, OnItemUseAction action)
    {
        ACTIONS.add(new CachedEntry(input, target, action));
    }

    public static synchronized void register(ItemAbility ability, Target target, OnItemUseAction action)
    {
        FALLBACKS.add(new FallbackEntry(ability, target, action));
    }


    /**
     * Registers TFC's interactions.
     */
    public static void registerDefaultInteractions()
    {
        register(Ingredient.of(TFCTags.Items.THATCH_BED_HIDES), (stack, context) -> {
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
                            level.setBlock(headPos, bed.setValue(ThatchBedBlock.PART, BedPart.HEAD).setValue(ThatchBedBlock.FACING, direction), 18);
                            level.getBlockEntity(headPos, TFCBlockEntities.THATCH_BED.get()).ifPresent(entity -> entity.setBed(headState, baseState, stack.split(1)));
                            return InteractionResult.SUCCESS;
                        }

                    }
                }
            }
            return InteractionResult.FAIL;
        });

        register(Ingredient.of(TFCTags.Items.STARTS_FIRES_WITH_DURABILITY), (stack, context) -> {
            final Player player = context.getPlayer();
            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            if (player != null && StartFireEvent.startFire(level, pos, level.getBlockState(pos), context.getClickedFace(), player, stack))
            {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                if (!player.isCreative())
                {
                    Helpers.damageItem(stack, player, context.getHand());
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        register(Ingredient.of(TFCTags.Items.STARTS_FIRES_WITH_ITEMS), (stack, context) -> {
            final Player playerEntity = context.getPlayer();
            if (playerEntity instanceof final ServerPlayer player)
            {
                final Level level = context.getLevel();
                final BlockPos pos = context.getClickedPos();
                if (StartFireEvent.startFire(level, pos, level.getBlockState(pos), context.getClickedFace(), player, stack))
                {
                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.FAIL;
        });

        register(Ingredient.of(Items.SNOW), (stack, context) -> {
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

        register(Ingredient.of(Items.CHARCOAL), (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.mayBuild())
            {
                final BlockPlaceContext blockContext = new BlockPlaceContext(context);
                final Level level = blockContext.getLevel();
                final BlockPos clickedPos = context.getClickedPos();
                final BlockPos offsetPos = blockContext.getClickedPos();
                final BlockState clickedState = level.getBlockState(clickedPos);
                final BlockState offsetState = level.getBlockState(offsetPos);
                final BlockItemPlacement placement = new BlockItemPlacement(Items.CHARCOAL, TFCBlocks.CHARCOAL_PILE);

                if (Helpers.isBlock(clickedState, TFCBlocks.CHARCOAL_PILE.get()) && clickedState.getValue(CharcoalPileBlock.LAYERS) < 8)
                {
                    // If you target the charcoal pile, and it's less than 8, we add to the charcoal pile
                    // We still have to check can place, since the stack grows in size
                    final BlockState placementState = clickedState.setValue(CharcoalPileBlock.LAYERS, clickedState.getValue(CharcoalPileBlock.LAYERS) + 1);
                    if (BlockItemPlacement.canPlace(blockContext, placementState, clickedPos))
                    {
                        level.setBlockAndUpdate(clickedPos, placementState);
                        return placement.postPlacement(blockContext, clickedPos);
                    }
                    return InteractionResult.FAIL; // No space
                }
                else if (Helpers.isBlock(offsetState, TFCBlocks.CHARCOAL_PILE.get()) && offsetState.getValue(CharcoalPileBlock.LAYERS) < 8)
                {
                    // Second, if we clicked on an offset position where we still have a charcoal block, we need to still add
                    // Same as the above branch, but using the offset position
                    final BlockState placementState = offsetState.setValue(CharcoalPileBlock.LAYERS, offsetState.getValue(CharcoalPileBlock.LAYERS) + 1);
                    if (BlockItemPlacement.canPlace(blockContext, placementState, offsetPos))
                    {
                        level.setBlockAndUpdate(offsetPos, placementState);
                        return placement.postPlacement(blockContext, offsetPos);
                    }
                    return InteractionResult.FAIL; // No space
                }
                else
                {
                    // Otherwise, we try normal block placement, which attempts to place a new charcoal pile at this location
                    return placement.place(blockContext);
                }
            }
            return InteractionResult.PASS;
        });

        // Log pile creation and insertion.
        // Note: sneaking will always bypass the log pile block onUse method - that is why we have to handle some insertion here.
        // - holding log, targeting block, shift click = place log pile
        // - holding log, targeting log pile, shift click = insert all
        // - holding log, targeting log pile, click normally = insert one
        final BlockItemPlacement logPilePlacement = new BlockItemPlacement(Items.AIR, TFCBlocks.LOG_PILE);
        register(Ingredient.of(TFCTags.Items.LOG_PILE_LOGS), (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.mayBuild() && player.isShiftKeyDown())
            {
                final Level level = context.getLevel();
                final Direction direction = context.getClickedFace();
                final BlockPos posClicked = context.getClickedPos();
                final BlockState stateClicked = level.getBlockState(posClicked);
                final BlockPos relativePos = posClicked.relative(direction);
                final Block blockClicked = stateClicked.getBlock();

                // If we're targeting a log pile, we can do one of two insertion operations
                if (Helpers.isBlock(stateClicked, TFCBlocks.LOG_PILE.get()))
                {

                    return level.getBlockEntity(posClicked, TFCBlockEntities.LOG_PILE.get())
                        .map(logPileBlockEntity -> {
                            if (!level.isClientSide())
                            {
                                LogPileBlock.insertAndPushUp(stack, stateClicked, level, posClicked, logPileBlockEntity, true);
                                return InteractionResult.SUCCESS;
                            }
                            return InteractionResult.SUCCESS;
                        }).orElse(InteractionResult.PASS);
                }
                else if (level.getBlockState(relativePos.below()).isFaceSturdy(level, relativePos.below(), Direction.UP))
                {
                    // when placing against a non-pile block
                    final ItemStack stackBefore = stack.copy();

                    // The block as set through onItemUse() might be set at either the clicked, or relative position.
                    // We need to construct this BlockPlaceContext before onItemUse is called, so it has the same value for the actual block placed pos
                    final BlockPos actualPlacedPos = new BlockPlaceContext(context).getClickedPos();
                    final InteractionResult result = logPilePlacement.onItemUse(stack, context); // Consumes the item if successful
                    if (result.consumesAction())
                    {
                        Helpers.insertOne(level, actualPlacedPos, TFCBlockEntities.LOG_PILE, stackBefore);
                    }
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

        register(KeyedIngredient.ofMatchingAnyRecipeInput(TFCRecipeTypes.SCRAPING, ScrapingRecipe.CACHE, ScrapingRecipe::getIngredient), (stack, context) -> {
            Level level = context.getLevel();
            ScrapingRecipe recipe = ScrapingRecipe.getRecipe(stack);
            if (recipe != null)
            {
                final BlockPos pos = context.getClickedPos();
                final BlockPos abovePos = pos.above();
                Player player = context.getPlayer();
                if (player != null && context.getClickedFace() == Direction.UP && Helpers.isBlock(level.getBlockState(pos), TFCTags.Blocks.SCRAPING_SURFACE) && level.getBlockState(abovePos).isAir())
                {
                    final BlockState state = TFCBlocks.SCRAPING.get().defaultBlockState();
                    level.setBlockAndUpdate(abovePos, state);
                    return level.getBlockEntity(abovePos, TFCBlockEntities.SCRAPING.get())
                        .map(entity -> {
                            final ItemStack insertStack = stack.split(1);
                            stack.setCount(stack.getCount() + entity.getInventory().insertItem(0, insertStack, false).getCount());
                            entity.updateDisplayCache();
                            level.sendBlockUpdated(abovePos, state, state, Block.UPDATE_CLIENTS);
                            return InteractionResult.SUCCESS;
                        }).orElse(InteractionResult.PASS);
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

        register(new BlockItemPlacement(Items.BOWL, TFCBlocks.WOODEN_BOWL));

        // Knapping
        final KeyedIngredient knapping = KeyedIngredient.of(
            stack -> KnappingType.get(stack) != null,
            () -> KnappingType.MANAGER.getValues()
                .stream()
                .flatMap(type -> RecipeHelpers.stream(type.inputItem().ingredient()))
                .toList()
        );
        register(knapping, Target.AIR, (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && context.getClickedPos().equals(BlockPos.ZERO))
            {
                final KnappingType type = KnappingType.get(player.getMainHandItem());
                if (type != null)
                {
                    if (player instanceof ServerPlayer serverPlayer)
                    {
                        final ItemStackContainerProvider provider = new ItemStackContainerProvider((stack1, hand, slot, playerInventory, windowId) -> KnappingContainer.create(stack1, type, hand, slot, playerInventory, windowId), Component.translatable("tfc.screen.knapping"));
                        provider.openScreen(serverPlayer, context.getHand(), buffer -> buffer.writeResourceLocation(KnappingType.MANAGER.getIdOrThrow(type)));
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        // Piles (Ingots + Sheets)
        // Shift + Click = Add to pile (either on the targeted pile, or create a new one)
        // Removal (Non-Shift Click) is handled by the respective pile block
        final BlockItemPlacement ingotPilePlacement = new BlockItemPlacement(Items.AIR, TFCBlocks.INGOT_PILE);
        final BlockItemPlacement doubleIngotPilePlacement = new BlockItemPlacement(Items.AIR, TFCBlocks.DOUBLE_INGOT_PILE);

        register(Ingredient.of(Tags.Items.INGOTS), (stack, context) -> doIngotPiling(ingotPilePlacement, stack, context, (IngotPileBlock) TFCBlocks.INGOT_PILE.get(), IngotPileBlock.COUNT, 64));
        register(Ingredient.of(TFCTags.Items.DOUBLE_INGOTS), (stack, context) -> doIngotPiling(doubleIngotPilePlacement, stack, context, (IngotPileBlock) TFCBlocks.DOUBLE_INGOT_PILE.get(), DoubleIngotPileBlock.DOUBLE_COUNT, 36));

        register(Ingredient.of(TFCTags.Items.SHEETS), (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.mayBuild() && player.isShiftKeyDown())
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
                    // We targeted an existing sheet pile, so we need to check if there's an empty space for it
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

        register(Ingredient.of(TFCTags.Items.SALAD_BOWLS), Target.BOTH, (stack, context) -> {
            // Only open salads when shift key is down
            // Normally when consuming bowl food (like salads), you'll be holding right click down causing the salad gui to immediately open
            // That feels bad to use, so we require shift to open salads - better in the common case
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            {
                if (context.getPlayer() instanceof ServerPlayer player)
                {
                    player.openMenu(TFCContainerProviders.SALAD);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        register(Ingredient.of(Items.EGG), Target.BOTH, (stack, context) -> {
            if (!TFCConfig.SERVER.enableVanillaEggThrowing.get())
            {
                // Prevent the original vanilla action (by returning non-pass), and consume it (by returning fail, since nothing occurred)
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        register(ItemAbilities.SHOVEL_DOUSE, Target.BLOCKS, (stack, context) -> DouseFireEvent.douse(context.getLevel(), context.getClickedPos(), context.getPlayer()) ? InteractionResult.SUCCESS : InteractionResult.PASS);
    }

    @NotNull
    private static InteractionResult doIngotPiling(BlockItemPlacement ingotPilePlacement, ItemStack stack, UseOnContext context, IngotPileBlock pileBlock, IntegerProperty countProperty, int maxIngots)
    {
        final Player player = context.getPlayer();
        if (player != null && player.mayBuild() && player.isShiftKeyDown())
        {
            final Level level = context.getLevel();
            final BlockPos posClicked = context.getClickedPos();
            final BlockState stateClicked = level.getBlockState(posClicked);

            if (Helpers.isBlock(stateClicked, pileBlock))
            {
                // We clicked on an ingot pile, so attempt to add to the pile
                final int currentIngots = stateClicked.getValue(countProperty);
                if (currentIngots < maxIngots)
                {
                    final ItemStack insertStack = stack.split(1);

                    Helpers.playPlaceSound(level, posClicked, stateClicked);
                    level.setBlock(posClicked, stateClicked.setValue(countProperty, currentIngots + 1), Block.UPDATE_CLIENTS);
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
                    } while (Helpers.isBlock(topState, pileBlock) && topState.getValue(countProperty) == maxIngots);

                    if (Helpers.isBlock(topState, pileBlock))
                    {
                        // We must be at a non-full ingot pile, so we want to place another ingot on this pile instead
                        final ItemStack insertStack = stack.split(1);
                        final int topIngots = topState.getValue(countProperty);

                        Helpers.playPlaceSound(level, topPos, topState);
                        level.setBlock(topPos, topState.setValue(countProperty, topIngots + 1), Block.UPDATE_CLIENTS);
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
                // Shrinking is already handled by the placement onItemUse() call, we just need to insert the stack
                final ItemStack stackBefore = stack.copyWithCount(1);

                // The block as set through onItemUse() might be set at either the clicked, or relative position.
                // We need to construct this BlockPlaceContext before onItemUse is called, so it has the same value for the actual block placed pos
                final BlockPos actualPlacedPos = new BlockPlaceContext(context).getClickedPos();
                final InteractionResult result = ingotPilePlacement.onItemUse(stack, context);
                if (result.consumesAction())
                {
                    level.getBlockEntity(actualPlacedPos, TFCBlockEntities.INGOT_PILE.get()).ifPresent(pile -> pile.addIngot(stackBefore));
                }
                return result;
            }
        }
        return InteractionResult.PASS;
    }

    public static Optional<InteractionResult> onItemUse(ItemStack stack, UseOnContext context, boolean isTargetingAir)
    {
        if (!ACTIVE.get())
        {
            for (CachedEntry entry : CACHE.getAll(stack.getItem()))
            {
                if (entry.target.isTarget(isTargetingAir) && entry.input.test(stack))
                {
                    final InteractionResult result = onItemUseWith(entry.action, stack, context);
                    return result == InteractionResult.PASS ? Optional.empty() : Optional.of(result);
                }
            }

            for (FallbackEntry entry : FALLBACKS)
            {
                if (entry.target.isTarget(isTargetingAir) && stack.canPerformAction(entry.ability))
                {
                    final InteractionResult result = onItemUseWith(entry.action, stack, context);
                    return result == InteractionResult.PASS ? Optional.empty() : Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    private static InteractionResult onItemUseWith(OnItemUseAction action, ItemStack stack, UseOnContext context)
    {
        InteractionResult result;
        ACTIVE.set(true);
        try
        {
            result = action.onItemUse(stack, context);
        }
        finally
        {
            ACTIVE.set(false);
        }
        return result;
    }

    /**
     * Return {@link InteractionResult#PASS} to allow normal right click handling
     */
    @FunctionalInterface
    public interface OnItemUseAction
    {
        InteractionResult onItemUse(ItemStack stack, UseOnContext context);
    }

    /**
     * Which targets an action accepts - blocks, air, or both
     */
    enum Target
    {
        AIR, BLOCKS, BOTH;

        public boolean isTarget(boolean isTargetingAir)
        {
            return this != (isTargetingAir ? BLOCKS : AIR);
        }
    }

    /**
     * An entry using a {@link KeyedIngredient} which is cached on a per-item basis, then only looked up for entries
     * matching that item.
     */
    record CachedEntry(
        KeyedIngredient input,
        Target target,
        OnItemUseAction action
    ) {}

    /**
     * An entry using a {@link ItemAbility} which is not cached, and looked up after other interactions
     * are complete, on every invocation.
     */
    record FallbackEntry(
        ItemAbility ability,
        Target target,
        OnItemUseAction action
    ) {}
}
