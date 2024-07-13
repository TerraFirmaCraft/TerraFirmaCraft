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
     * @return if there is, given the current state values, no way this block can be supported.
     */
    public static boolean isEmptyContents(BlockState state)
    {
        for (int i = 0; i < 4; i++)
        {
            if (state.getValue(ITEM_PROPERTIES[i])) return false;
        }
        return true;
    }

    public static final BooleanProperty ITEM_0 = TFCBlockStateProperties.ITEM_0;
    public static final BooleanProperty ITEM_1 = TFCBlockStateProperties.ITEM_1;
    public static final BooleanProperty ITEM_2 = TFCBlockStateProperties.ITEM_2;
    public static final BooleanProperty ITEM_3 = TFCBlockStateProperties.ITEM_3;

    public static final BooleanProperty[] ITEM_PROPERTIES = new BooleanProperty[] {ITEM_0, ITEM_1, ITEM_2, ITEM_3};

    private static final VoxelShape SHAPE_0 = box(0, 0, 0, 8.0D, 1.0D, 8.0D);
    private static final VoxelShape SHAPE_1 = box(8.0D, 0, 0, 16.0D, 1.0D, 8.0D); // x
    private static final VoxelShape SHAPE_2 = box(0, 0, 8.0D, 8.0D, 1.0D, 16.0D); // z
    private static final VoxelShape SHAPE_3 = box(8.0D, 0, 8.0D, 16.0D, 1.0D, 16.0D); // xz

    private static final VoxelShape[] SHAPES = new VoxelShape[] {SHAPE_0, SHAPE_1, SHAPE_2, SHAPE_3};

    /**
     * Pos refers to below block, state refers to the placed item state.
     */
    public static BlockState updateStateValues(LevelAccessor level, BlockPos pos, BlockState state)
    {
        for (int i = 0; i < 4; i++)
        {
            state = state.setValue(ITEM_PROPERTIES[i], isSlotSupportedOn(level, pos, level.getBlockState(pos), i));
        }
        return state;
    }

    /**
     * Pos and state refer to the below block
     */
    public static boolean isSlotSupportedOn(LevelAccessor level, BlockPos pos, BlockState state, int slot)
    {
        VoxelShape supportShape = state.getBlockSupportShape(level, pos).getFaceShape(Direction.UP);
        return !Shapes.joinIsNotEmpty(supportShape, SHAPES[slot], BooleanOp.ONLY_SECOND);
    }

    private static Map<BlockState, VoxelShape> makeShapes(ImmutableList<BlockState> possibleStates)
    {
        final ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : possibleStates)
        {
            VoxelShape shape = Shapes.empty();
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

    private final Map<BlockState, VoxelShape> cachedShapes;

    public PlacedItemBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(ITEM_0, true).setValue(ITEM_1, true).setValue(ITEM_2, true).setValue(ITEM_3, true));
        cachedShapes = makeShapes(getStateDefinition().getPossibleStates());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        BlockState updateState = updateStateValues(level, pos.below(), state);
        PlacedItemBlockEntity placedItem = level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).orElse(null);
        if (placedItem != null)
        {
            if (isEmptyContents(updateState))
            {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return updateState;
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
                return ItemInteractionResult.SUCCESS;
            }
            return placedItem.onRightClick(player, held, hitResult)
                ? ItemInteractionResult.SUCCESS
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
            return level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).map(placedItem -> placedItem.getCloneItemStack(state, blockResult)).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
