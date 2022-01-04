package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;

public class WattleBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension
{
    private static final VoxelShape EAST_SHAPE = Block.box(0, 0, 0, 4, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(12, 0, 0, 16, 16, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 0, 16, 16, 4);
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 12, 16, 16, 16);

    private static final VoxelShape EAST_INNER_SHAPE = Block.box(0, 0, 4, 4, 16, 12);
    private static final VoxelShape WEST_INNER_SHAPE = Block.box(12, 0, 4, 16, 16, 12);
    private static final VoxelShape SOUTH_INNER_SHAPE = Block.box(4, 0, 0, 12, 16, 4);
    private static final VoxelShape NORTH_INNER_SHAPE = Block.box(4, 0, 12, 12, 16, 16);

    private static final VoxelShape EAST_OUTER_SHAPE = Shapes.join(EAST_SHAPE, EAST_INNER_SHAPE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape WEST_OUTER_SHAPE = Shapes.join(WEST_SHAPE, WEST_INNER_SHAPE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape SOUTH_OUTER_SHAPE = Shapes.join(SOUTH_SHAPE, SOUTH_INNER_SHAPE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape NORTH_OUTER_SHAPE = Shapes.join(NORTH_SHAPE, NORTH_INNER_SHAPE, BooleanOp.ONLY_FIRST);

    private static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;

    private final ExtendedProperties properties;

    public WattleBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack item = player.getItemInHand(hand);
        int stage = state.getValue(STAGE);
        if (stage == 0 && item.is(TFCTags.Items.WATTLE_STICKS))
        {
            if (!level.isClientSide)
            {
                if (!player.isCreative()) item.shrink(1);
                level.setBlockAndUpdate(pos, state.setValue(STAGE, stage + 1));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (stage == 1 && item.is(TFCItems.DAUB.get()))
        {
            if (!level.isClientSide)
            {
                if (!player.isCreative()) item.shrink(1);
                level.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return canSurvive(state, level, currentPos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(STAGE) < 1)
        {
            return switch(state.getValue(FACING))
                {
                    case NORTH -> NORTH_OUTER_SHAPE;
                    case SOUTH -> SOUTH_OUTER_SHAPE;
                    case EAST -> EAST_OUTER_SHAPE;
                    case WEST -> WEST_OUTER_SHAPE;
                    default -> throw new IllegalArgumentException("Asked for non-horizontal property for a horizontal block");
                };
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, STAGE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
        return defaultBlockState().setValue(FACING, below.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? below.getValue(BlockStateProperties.HORIZONTAL_FACING) : context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch(state.getValue(FACING))
            {
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case EAST -> EAST_SHAPE;
                case WEST -> WEST_SHAPE;
                default -> throw new IllegalArgumentException("Asked for non-horizontal property for a horizontal block");
            };
    }

}
