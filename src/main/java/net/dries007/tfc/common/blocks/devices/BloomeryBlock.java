package net.dries007.tfc.common.blocks.devices;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MultiBlock;
import org.jetbrains.annotations.Nullable;

public class BloomeryBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    //todo: fix open shapes (i think closed are correct)
    public static final VoxelShape OPEN_NORTH_SHAPE = Shapes.or(
        box(0D, 0D, 0D,16D, 16D, 8D),
        box(0D, 0D, 0D, 2D, 16D, 8D),
        box(14D, 0D, 0D, 16D, 16D, 8D)
    );
    public static final VoxelShape OPEN_SOUTH_SHAPE = Shapes.or(
        box(8D, 0D, 0D, 16D, 16D, 16D),
        box(8D, 0D, 0D, 16D, 16D, 2D),
        box(8D, 0D, 14D, 16D, 16D, 16D)
    );
    public static final VoxelShape OPEN_WEST_SHAPE = Shapes.or(
        box(0D, 0D, 8D, 16D, 16D, 16D),
        box(0D, 0D, 8D, 2D, 16D, 16D),
        box(14D, 0D, 8D, 16D, 16D, 16D)
    );
    public static final VoxelShape OPEN_EAST_SHAPE = Shapes.or(
        box(0D, 0D, 0D, 8D, 16D, 16D),
        box(0D, 0D, 0D, 8D, 16D, 2D),
        box(0D, 0D, 14D, 8D, 16D, 16D)
    );

    public static final VoxelShape CLOSED_NORTH_SHAPE = box(0D, 0D, 0D, 16D, 16D, 2D);
    public static final VoxelShape CLOSED_SOUTH_SHAPE = box(0D, 0D, 14D, 16D, 16D, 16D);
    public static final VoxelShape CLOSED_WEST_SHAPE = box(0D, 0D, 0D, 2D, 16D, 16D);
    public static final VoxelShape CLOSED_EAST_SHAPE = box(14D, 0D, 0D, 16D, 16D, 16D);

    private static final MultiBlock BLOOMERY_CHIMNEY; // Helper for determining how high the chimney is
    private static final MultiBlock[] BLOOMERY_BASE; // If one of those is true, bloomery is formed and can operate (has at least one chimney)
    private static final MultiBlock GATE_Z, GATE_X; // Determines if the gate can stay in place
    private static final Direction[] NORTH_SOUTH_DOWN = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.DOWN};
    private static final Direction[] EAST_WEST_DOWN = new Direction[]{Direction.EAST, Direction.WEST, Direction.DOWN};

    static
    {
        BiPredicate<LevelAccessor, BlockPos> stoneMatcher = (level, pos) -> level.getBlockState(pos).is(TFCTags.Blocks.BLOOMERY_INSULATION);// && level.getBlockState(pos).isCollisionShapeFullBlock(level, pos); //correct method to check full block?
        Predicate<BlockState> insideChimney = state -> state.getBlock() == TFCBlocks.MOLTEN.get() || state.getMaterial().isReplaceable();
        Predicate<BlockState> center = state -> state.is(TFCBlocks.CHARCOAL_PILE.get()) || state.is(TFCBlocks.BLOOM.get()) || state.getMaterial().isReplaceable();
        BlockPos origin = BlockPos.ZERO;

        // Bloomery center is the charcoal pile pos
        BLOOMERY_BASE = new MultiBlock[4];
        BLOOMERY_BASE[0] = new MultiBlock() // north - i'm not sure you can still get a unique int for each direction?
            .match(origin, center)
            .match(origin.below(), stoneMatcher)
            .match(origin.north(), state -> state.is(TFCBlocks.BLOOMERY.get()))
            .matchEachDirection(origin, stoneMatcher, new Direction[]{Direction.SOUTH, Direction.EAST, Direction.WEST}, 1)
            .matchEachDirection(origin.north(), stoneMatcher, EAST_WEST_DOWN, 1)
            .matchHorizontal(origin.above(), stoneMatcher, 1);

        BLOOMERY_BASE[1] = new MultiBlock() //south
            .match(origin, center)
            .match(origin.below(), stoneMatcher)
            .match(origin.south(), state -> state.is(TFCBlocks.BLOOMERY.get()))
            .matchEachDirection(origin, stoneMatcher, new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST}, 1)
            .matchEachDirection(origin.south(), stoneMatcher, EAST_WEST_DOWN, 1)
            .matchHorizontal(origin.above(), stoneMatcher, 1);

        BLOOMERY_BASE[2] = new MultiBlock() //west
            .match(origin, center)
            .match(origin.below(), stoneMatcher)
            .match(origin.west(), state -> state.is(TFCBlocks.BLOOMERY.get()))
            .matchEachDirection(origin, stoneMatcher, new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH}, 1)
            .matchEachDirection(origin.west(), stoneMatcher, NORTH_SOUTH_DOWN, 1)
            .matchHorizontal(origin.above(), stoneMatcher, 1);

        BLOOMERY_BASE[3] = new MultiBlock() //east
            .match(origin, center)
            .match(origin.below(), stoneMatcher)
            .match(origin.east(), state -> state.is(TFCBlocks.BLOOMERY.get()))
            .matchEachDirection(origin, stoneMatcher, new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH}, 1)
            .matchEachDirection(origin.east(), stoneMatcher, NORTH_SOUTH_DOWN, 1)
            .matchHorizontal(origin.above(), stoneMatcher, 1);

        BLOOMERY_CHIMNEY = new MultiBlock()
            .match(origin, insideChimney)
            .matchHorizontal(origin, stoneMatcher, 1);

        // Gate center is the bloomery gate block
        GATE_Z = new MultiBlock()
            .match(origin, state -> state.is(TFCBlocks.BLOOMERY.get()) || state.isAir())
            .matchEachDirection(origin, stoneMatcher, new Direction[]{Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN}, 1);

        GATE_X = new MultiBlock()
            .match(origin, state -> state.is(TFCBlocks.BLOOMERY.get()) || state.isAir())
            .matchEachDirection(origin, stoneMatcher, new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN}, 1);
    }

    //centerPos should be the internal block of the bloomery
    public static int getChimneyLevels(Level level, BlockPos centerPos)
    {
        for (int i = 1; i < 4; i++)
        {
            BlockPos center = centerPos.above(i);
            if (!BLOOMERY_CHIMNEY.test(level, center))
            {
                return i - 1;
            }
        }
        // Maximum levels
        return 3;
    }

    public static boolean canGateStayInPlace(Level level, BlockPos pos, Direction.Axis axis)
    {
        if (axis == Direction.Axis.X)
        {
            return GATE_X.test(level, pos);
        }
        else
        {
            return GATE_Z.test(level, pos);
        }
    }

    //if Directions don't have int indices any more, then...
    public static boolean isFormed(Level level, BlockPos centerPos, Direction facing)
    {
        return switch (facing)
            {
                case NORTH -> BLOOMERY_BASE[0].test(level, centerPos);
                case SOUTH -> BLOOMERY_BASE[1].test(level, centerPos);
                case WEST -> BLOOMERY_BASE[2].test(level, centerPos);
                case EAST -> BLOOMERY_BASE[3].test(level, centerPos);
                default -> false;
            };
    }

    public BloomeryBlock(ExtendedProperties properties)
    {
        super(properties);
        /* todo: deal with these from 1.12
         * setHarvestLevel("pickaxe", 0); -> requiresCorrectToolForDrops + json?
         * setHardness(20.0F);
         */
        registerDefaultState(getStateDefinition().any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false)
            .setValue(OPEN, false));
    }

    /* todo: find out if there's a modern version of following methods
     * getBoundingBox, getBlockFaceShape, getCollisionBoundingBox
     * collisionRayTrace, canPlaceBlockAt (canSurvive?)
     * and does TFC still have:
     * onBreakBlock
     */

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND)
        {
            Direction facing = state.getValue(FACING);
            BloomeryBlockEntity bloomery = Helpers.getBlockEntity(level, pos, BloomeryBlockEntity.class);
            if (bloomery != null)
            {
                LOGGER.info("Bloomery being used. Structure formed "+isFormed(level, bloomery.getInternalBlock(), facing)+" with "+ getChimneyLevels(level, Helpers.getBlockEntity(level, pos, BloomeryBlockEntity.class).getInternalBlock())+" levels. Bloomery lit is "+state.getValue(LIT));
                LOGGER.info("Bloomery inventory has inventory "+bloomery.printInventory());
            }
            if (!state.getValue(LIT))
            {
                state = state.cycle(OPEN);
                level.setBlockAndUpdate(pos, state);
                level.playSound(null, pos, SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction placeDirection = null;
        Direction[] nearestDirections = context.getNearestLookingDirections();
        if (canGateStayInPlace(context.getLevel(), context.getClickedPos(), Direction.Axis.X))
        {
            for (Direction d : nearestDirections)
            {
                if (d == Direction.EAST || d == Direction.WEST)
                {
                    placeDirection = d;
                    break;
                }
            }
        }
        else if (canGateStayInPlace(context.getLevel(), context.getClickedPos(), Direction.Axis.Z))
        {
            for (Direction d : nearestDirections)
            {
                if (d == Direction.NORTH || d == Direction.SOUTH)
                {
                    placeDirection = d;
                    break;
                }
            }
        }
        else
        {
            return Blocks.AIR.defaultBlockState();
        }
        return this.defaultBlockState().setValue(FACING, placeDirection == null ? Direction.NORTH : placeDirection.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING).add(LIT).add(OPEN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(OPEN))
        {
            return switch (state.getValue(FACING))
            {
                case NORTH -> OPEN_NORTH_SHAPE;
                case SOUTH -> OPEN_SOUTH_SHAPE;
                case WEST -> OPEN_EAST_SHAPE;
                case EAST -> OPEN_WEST_SHAPE;
                default -> throw new IllegalArgumentException("Bloomery has no facing direction");
            };
        }
        else
        {
            return switch (state.getValue(FACING))
            {
                case NORTH -> CLOSED_NORTH_SHAPE;
                case SOUTH -> CLOSED_SOUTH_SHAPE;
                case WEST -> CLOSED_WEST_SHAPE;
                case EAST -> CLOSED_EAST_SHAPE;
                default -> throw new IllegalArgumentException("Bloomery has no facing direction");
            };
        }
    }


}
