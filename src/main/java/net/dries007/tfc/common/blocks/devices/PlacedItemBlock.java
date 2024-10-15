/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class PlacedItemBlock extends DeviceBlock implements IForgeBlockExtension, EntityBlockExtension
{
    /**
     * @return If the given {@code state} has all {@code ITEM_[n]} properties set to {@code false}. This is used to detect
     * if the block is empty, and must be removed (or in the case of a shelf, cannot have items placed underneath it).
     */
    public static boolean isEmptyContents(BlockState state)
    {
        for (BooleanProperty property : ITEM_PROPERTIES)
            if (state.getValue(property))
                return false;
        return true;
    }

    /**
     * @return If the given {@code state} has all {@code ITEM_[n]} properties set to {@code true}. This is required in order
     * for a large item to be held.
     */
    public static boolean isFullContents(BlockState state)
    {
        for (BooleanProperty property : ITEM_PROPERTIES)
            if (!state.getValue(property))
                return false;
        return true;
    }

    /**
     * Pos refers to below block, state refers to the placed item state.
     * @param pos The position below the placed item
     * @param state The current state
     * @return The new placed item state, after updating for change in the shape of the below block
     */
    public static BlockState updateStateValues(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final VoxelShape shapeBelow = level.getBlockState(pos).getBlockSupportShape(level, pos).getFaceShape(Direction.UP);
        for (int slot = 0; slot < 4; slot++)
        {
            state = state.setValue(ITEM_PROPERTIES[slot], !Shapes.joinIsNotEmpty(shapeBelow, SHAPES[slot], BooleanOp.ONLY_SECOND));
        }
        return state;
    }

    public static final BooleanProperty ITEM_0 = TFCBlockStateProperties.ITEM_0;
    public static final BooleanProperty ITEM_1 = TFCBlockStateProperties.ITEM_1;
    public static final BooleanProperty ITEM_2 = TFCBlockStateProperties.ITEM_2;
    public static final BooleanProperty ITEM_3 = TFCBlockStateProperties.ITEM_3;

    public static final BooleanProperty[] ITEM_PROPERTIES = new BooleanProperty[] {ITEM_0, ITEM_1, ITEM_2, ITEM_3};

    private static final VoxelShape SHAPE_0 = box(8, 0, 8, 16, 1, 16); // xz
    private static final VoxelShape SHAPE_1 = box(0, 0, 8, 8, 1, 16); // z
    private static final VoxelShape SHAPE_2 = box(8, 0, 0, 16, 1, 8); // x
    private static final VoxelShape SHAPE_3 = box(0, 0, 0, 8, 1, 8);

    private static final VoxelShape[] SHAPES = new VoxelShape[] {SHAPE_0, SHAPE_1, SHAPE_2, SHAPE_3};

    private static Map<BlockState, VoxelShape> makeShapes(ImmutableList<BlockState> possibleStates, VoxelShape emptyShape)
    {
        final ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : possibleStates)
        {
            VoxelShape shape = emptyShape;
            for (int i = 0; i < 4; i++)
            {
                if (state.getValue(ITEM_PROPERTIES[i]))
                {
                    shape = Shapes.or(shape, SHAPES[i]);
                }
            }
            builder.put(state, shape);
        }
        return builder.build();
    }

    private final boolean persistentWhenEmpty;
    private final Map<BlockState, VoxelShape> cachedShapes;

    public PlacedItemBlock(ExtendedProperties properties)
    {
        this(properties, Shapes.empty(), false);
    }

    protected PlacedItemBlock(ExtendedProperties properties, VoxelShape emptyShape, boolean persistentWhenEmpty)
    {
        super(properties, InventoryRemoveBehavior.DROP);

        this.cachedShapes = makeShapes(getStateDefinition().getPossibleStates(), emptyShape);
        this.persistentWhenEmpty = persistentWhenEmpty;

        registerDefaultState(getStateDefinition().any()
            .setValue(ITEM_0, true)
            .setValue(ITEM_1, true)
            .setValue(ITEM_2, true)
            .setValue(ITEM_3, true));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final BlockState updateState = updateStateValues(level, pos.below(), state);
        if (!persistentWhenEmpty && isEmptyContents(updateState))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return updateState;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        // By default, this properly handles when a block is replaced with another block (dumping all contents)
        // However, we also need to handle when the state updates, and the inventory slots are no longer available.
        // So, rather than calling `beforeRemove` -> `ejectInventory` in the super method, we call `ejectIfNeeded`.
        //
        // N.B. Use the extension block entity, since we want both shelves and placed items to use this functionality
        level.getBlockEntity(pos, getExtendedProperties().<PlacedItemBlockEntity>blockEntity())
            .ifPresent(entity -> entity.ejectInventoryIfNeeded(newState));

        // Default super() behavior
        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity()))
        {
            level.removeBlockEntity(pos);
        }
    }

    @Override
    protected void beforeRemove(InventoryBlockEntity<?> entity)
    {
        // No-op - we don't want to call ejectInventory()
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final PlacedItemBlockEntity placedItem = level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).orElse(null);
        if (placedItem != null)
        {
            final ItemStack held = player.getItemInHand(hand);
            if (Helpers.isItem(held.getItem(), TFCTags.Items.PIT_KILN_STRAW) && !held.isEmpty() && PitKilnBlockEntity.isValid(level, pos))
            {
                if (!level.isClientSide())
                {
                    PlacedItemBlockEntity.convertPlacedItemToPitKiln(level, pos, held.split(1));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return placedItem.onRightClick(player, held, hitResult)
                ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return cachedShapes.get(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ITEM_0, ITEM_1, ITEM_2, ITEM_3);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult result, LevelReader level, BlockPos pos, Player player)
    {
        if (result instanceof BlockHitResult blockResult)
        {
            return level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get())
                .map(placedItem -> placedItem.getCloneItemStack(state, blockResult))
                .orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
