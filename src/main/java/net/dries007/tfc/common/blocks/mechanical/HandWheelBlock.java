package net.dries007.tfc.common.blocks.mechanical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class HandWheelBlock extends DeviceBlock
{
    public static final BooleanProperty HAS_WHEEL = TFCBlockStateProperties.HAS_WHEEL;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape[] SHAPE = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 0, 0, 16, 16, 4));

    private static final VoxelShape[] SHAPE_WITH_WHEEL = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 0, 0, 0, 16, 16, 4),
        Helpers.rotateShape(dir, 3, 3, 3, 13, 13, 13)
    ));

    public HandWheelBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(HAS_WHEEL, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.HAND_WHEEL.get()).map(wheel -> {
            final ItemStack held = player.getItemInHand(hand);
            if (Helpers.isItem(held, TFCTags.Items.HAND_WHEEL))
            {
                return wheel.getCapability(Capabilities.ITEM).map(cap -> {
                    final ItemStack current = cap.getStackInSlot(0);
                    if (current.isEmpty() && cap.isItemValid(0, held))
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, cap.insertItem(0, held.split(1), false));
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                    else if (!current.isEmpty() && player.isShiftKeyDown() && held.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, cap.extractItem(0, 1, false));
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                    return InteractionResult.PASS;
                }).orElse(InteractionResult.PASS);
            }
            else
            {
                if (!player.isShiftKeyDown())
                {
                    wheel.addRotation(40);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Direction direction = context.getHorizontalDirection();
        final boolean isShifting = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return defaultBlockState().setValue(FACING, isShifting ? direction : direction.getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final int idx = state.getValue(FACING).get2DDataValue();
        return state.getValue(HAS_WHEEL) ? SHAPE_WITH_WHEEL[idx] : SHAPE[idx];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HAS_WHEEL, FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
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
