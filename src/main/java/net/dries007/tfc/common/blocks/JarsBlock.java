/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.JarsBlockEntity;
import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.util.Helpers;

public class JarsBlock extends BottomSupportedDeviceBlock implements IHighlightHandler
{
    public static final BooleanProperty ITEM_0 = TFCBlockStateProperties.ITEM_0;
    public static final BooleanProperty ITEM_1 = TFCBlockStateProperties.ITEM_1;
    public static final BooleanProperty ITEM_2 = TFCBlockStateProperties.ITEM_2;
    public static final BooleanProperty ITEM_3 = TFCBlockStateProperties.ITEM_3;

    public static final BooleanProperty[] ITEM_PROPERTIES = {ITEM_0, ITEM_1, ITEM_2, ITEM_3};

    public static final VoxelShape SHAPE_1 = Shapes.or(box(10, 6, 10, 13, 7, 13), box(9, 0, 9, 14, 6, 14));
    public static final VoxelShape SHAPE_2 = Shapes.or(box(2, 6, 10, 5, 7, 13), box(1, 0, 9, 6, 6, 14));
    public static final VoxelShape SHAPE_3 = Shapes.or(box(10, 6, 2, 13, 7, 5), box(9, 0, 1, 14, 6, 6));
    public static final VoxelShape SHAPE_4 = Shapes.or(box(2, 6, 2, 5, 7, 5), box(1, 0, 1, 6, 6, 6));

    public static final VoxelShape[] SHAPES = {SHAPE_1, SHAPE_2, SHAPE_3, SHAPE_4};
    public static final AABB[] BOUNDS = Util.make(() -> {
        final AABB[] aabb = new AABB[4];
        for (int i = 0; i < 4; i++)
        {
            aabb[i] = SHAPES[i].bounds().inflate(0.01);
        }
        return aabb;
    });

    protected static Map<BlockState, VoxelShape> makeShapes(ImmutableList<BlockState> possibleStates)
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

    public static BlockState updateStateValues(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (level.getBlockEntity(pos) instanceof JarsBlockEntity jars)
        {
            for (int i = 0; i < 4; i++)
            {
                state = state.setValue(ITEM_PROPERTIES[i], !jars.getInventory().getStackInSlot(i).isEmpty());
            }
        }
        return state;
    }

    public static boolean isEmptyContents(BlockState state)
    {
        if (state.getBlock() instanceof JarsBlock block && block.isPersistentWithNoItems())
        {
            return false;
        }
        for (int i = 0; i < 4; i++)
        {
            if (state.getValue(ITEM_PROPERTIES[i])) return false;
        }
        return true;
    }

    private final Map<BlockState, VoxelShape> cachedShapes;

    public JarsBlock(ExtendedProperties properties)
    {
        this(properties, true);
    }

    public JarsBlock(ExtendedProperties properties, boolean buildCache)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE_1);
        registerDefaultState(getStateDefinition().any().setValue(ITEM_0, true).setValue(ITEM_1, true).setValue(ITEM_2, true).setValue(ITEM_3, true));
        this.cachedShapes = buildCache ? makeShapes(getStateDefinition().getPossibleStates()) : new HashMap<>();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final BlockState newShape = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (newShape.isAir())
            return newShape;
        final BlockState updateState = updateStateValues(level, pos, state);
        return isEmptyContents(updateState) ? Blocks.AIR.defaultBlockState() : updateState;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return cachedShapes.get(state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof JarsBlockEntity jars)
        {
            return jars.use(player, player.getItemInHand(hand), hitResult)
                ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean drawHighlight(Level level, BlockPos pos, Player player, BlockHitResult rayTrace, PoseStack stack, MultiBufferSource buffers, Vec3 rendererPosition)
    {
        if (!Helpers.isItem(player.getItemInHand(InteractionHand.MAIN_HAND), TFCTags.Items.JARS) && !Helpers.isItem(player.getItemInHand(InteractionHand.OFF_HAND), TFCTags.Items.JARS))
        {
            return true;
        }
        final int slot = PlacedItemBlockEntity.getSlotSelected(rayTrace);
        IHighlightHandler.drawBox(stack, SHAPES[slot], buffers, pos, rendererPosition, 1f, 0f, 0f, 1f);
        return BOUNDS[slot].move(pos).contains(rayTrace.getLocation());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(ITEM_0, ITEM_1, ITEM_2, ITEM_3));
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected boolean isPersistentWithNoItems()
    {
        return false;
    }
}
