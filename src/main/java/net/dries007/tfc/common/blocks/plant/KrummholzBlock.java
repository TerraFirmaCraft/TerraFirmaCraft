/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.util.Helpers;

public class KrummholzBlock extends ExtendedBlock
{
    public static void updateFreezingInColumn(LevelAccessor level, BlockPos pos, boolean frozen)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockState state = level.getBlockState(pos);
        cursor.set(pos);
        while (state.getBlock() instanceof KrummholzBlock)
        {
            if (state.getValue(SNOWY) != frozen)
            {
                level.setBlock(cursor, state.cycle(SNOWY), 2);
            }
            cursor.move(0, -1, 0);
            state = level.getBlockState(cursor);
        }
    }

    public static final VoxelShape NORMAL_SHAPE = box(6, 0, 6, 10, 16, 10);
    public static final VoxelShape TIP_SHAPE = box(6.5, 0, 6.5, 9.5, 12, 9.5);
    public static final VoxelShape SNOW_SHAPE = box(0, 0, 0, 16, 2, 16);
    public static final VoxelShape NORMAL_WITH_SNOW_SHAPE = Shapes.or(NORMAL_SHAPE, SNOW_SHAPE);
    public static final VoxelShape TIP_WITH_SNOW_SHAPE = Shapes.or(TIP_SHAPE, SNOW_SHAPE);

    public static final BooleanProperty TIP = TFCBlockStateProperties.TIP;
    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;
    public static final BooleanProperty BOTTOM = TFCBlockStateProperties.BOTTOM;

    public KrummholzBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TIP, false).setValue(SNOWY, false).setValue(BOTTOM, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.getItem() == Items.SNOW && !state.getValue(SNOWY))
        {
            level.setBlockAndUpdate(pos, state.cycle(SNOWY));
            stack.shrink(1);
            Helpers.playPlaceSound(player, level, pos, SoundType.SNOW);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !state.canSurvive(level, pos))
        {
            level.scheduleTick(pos, this, 1);
        }
        else if (facing == Direction.DOWN)
        {
            return state.setValue(BOTTOM, facingState.getBlock() != this);
        }
        else if (facing == Direction.UP)
        {
            return state.setValue(TIP, facingState.getBlock() != this);
        }
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (!state.canSurvive(level, pos))
        {
            level.destroyBlock(pos, true);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(TIP, true).setValue(BOTTOM, context.getLevel().getBlockState(context.getClickedPos().below()).getBlock() != this);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockState below = level.getBlockState(pos.below());
        return below.getBlock() == this || BottomSupportedDeviceBlock.canSurvive(level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final boolean addSnow = state.getValue(BOTTOM) && state.getValue(SNOWY);
        return state.getValue(TIP) ? addSnow ? TIP_WITH_SNOW_SHAPE : TIP_SHAPE : addSnow ? NORMAL_WITH_SNOW_SHAPE : NORMAL_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(TIP, SNOWY, BOTTOM));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos)
    {
        return false;
    }
}
