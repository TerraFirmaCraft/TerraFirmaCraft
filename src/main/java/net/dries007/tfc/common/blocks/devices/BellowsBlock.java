/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.BellowsBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class BellowsBlock extends DeviceBlock
{
    private static VoxelShape createShapeFor(Direction direction, float extension)
    {
        return Shapes.or(makeFrontShape(direction), makeMiddleShape(direction, extension), makeEndShape(direction, extension));
    }

    private static VoxelShape makeFrontShape(Direction direction)
    {
        return Helpers.rotateShape(direction, 0, 0, 0, 16, 16, 2);
    }

    private static VoxelShape makeMiddleShape(Direction direction, float extension)
    {
        return Helpers.rotateShape(direction, 2, 2, 2, 14, 14, extension * 16);
    }

    private static VoxelShape makeEndShape(Direction direction, float extension)
    {
        return Helpers.rotateShape(direction, 0, 0, extension * 16, 16, 16, extension * 16 + 2);
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape[] COMPLETE_SHAPES = Helpers.computeHorizontalShapes(d -> createShapeFor(d, 0.875f));

    private final ExtendedProperties properties;

    public BellowsBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
        this.properties = properties;

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final Direction direction = state.getValue(FACING);
        return level.getBlockEntity(pos, TFCBlockEntities.BELLOWS.get()).map(bellows -> {
            final float ext = 1f - bellows.getExtensionLength(1f);
            return ext == 0.875f ? COMPLETE_SHAPES[direction.get2DDataValue()] : createShapeFor(direction, ext);
        }).orElse(COMPLETE_SHAPES[direction.get2DDataValue()]);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        state.updateNeighbourShapes(level, pos, 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        // When placing, you're typically either placing it naturally, or placing it against a device like a blast furnace, which has a GUI (which means you have to use shift)
        // In the first case, we can assume you're placing it facing towards yourself - it doesn't really matter which.
        // In the second case, you're generally trying to place it facing *into* the thing you're holding shift to place on, which means we want it to face away from you
        final Direction direction = context.getHorizontalDirection();
        final boolean isShifting = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return defaultBlockState().setValue(FACING, isShifting ? direction : direction.getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        level.scheduleTick(pos, this, 2);
        return level.getBlockEntity(pos, TFCBlockEntities.BELLOWS.get())
            .map(BellowsBlockEntity::onRightClick)
            .orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
