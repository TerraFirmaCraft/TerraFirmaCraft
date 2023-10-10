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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.mechanical.AxleBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class WindmillBlock extends DeviceBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_5;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public static final VoxelShape SHAPE_X = box(2, 2, 0, 14, 14, 16);
    public static final VoxelShape SHAPE_Z = Helpers.rotateShape(Direction.WEST, 2, 2, 0, 14, 14, 16);

    public WindmillBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof WindmillBlockEntity windmill)
        {
            return windmill.getCapability(Capabilities.ITEM).map(inv -> {
                final ItemStack held = player.getItemInHand(hand);
                final ItemStack insertStack = held.copyWithCount(1);
                for (int i = 0; i < inv.getSlots(); i++)
                {
                    if (inv.isItemValid(i, insertStack))
                    {
                        final ItemStack leftover = inv.insertItem(i, insertStack, false);
                        if (leftover.isEmpty())
                        {
                            if (!player.isCreative()) held.shrink(1);
                            windmill.updateBlockState();
                            Helpers.playPlaceSound(level, pos, getSoundType(state, level, pos, player));
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                }
                return InteractionResult.PASS;
            }).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final Direction dir = context.getHorizontalDirection();
        final BlockState relative = level.getBlockState(pos.relative(dir));
        if (relative.getBlock() instanceof AxleBlock && relative.getValue(AxleBlock.AXIS).isHorizontal())
        {
            return defaultBlockState().setValue(AXIS, relative.getValue(AxleBlock.AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        }
        return defaultBlockState().setValue(AXIS, dir.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS, STAGE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }
}
