/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.EnumMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MultiBlock;

public class BloomeryBlock extends DeviceBlock implements EntityBlockExtension
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final VoxelShape OPEN_NORTH_SHAPE = Shapes.or(
        box(0D, 15D, 0D, 16D, 16D, 2D),
        box(0D, 0D, 0D, 16D, 1D, 2D),
        box(0D, 1D, 0D, 1D, 15D, 1D),
        box(15D, 1D, 0D, 16D, 15D, 1D),
        box(0D, 1D, 1D, 2D, 15D, 8D),
        box(14D, 1D, 1D, 16D, 15D, 8D)
    );
    public static final VoxelShape OPEN_SOUTH_SHAPE = Shapes.or(
        box(0D, 15D, 14D, 16D, 16D, 16D),
        box(0D, 0D, 14D, 16D, 1D, 16D),
        box(0D, 1D, 15D, 1D, 15D, 16D),
        box(15D, 1D, 15D, 16D, 15D, 16D),
        box(0D, 1D, 8D, 2D, 15D, 15D),
        box(14D, 1D, 8D, 16D, 15D, 15D)
    );
    public static final VoxelShape OPEN_WEST_SHAPE = Shapes.or(
        box(0D, 15D, 0D, 2D, 16D, 16D),
        box(0D, 0D, 0D, 2D, 1D, 16D),
        box(0D, 1D, 0D, 1D, 15D, 1D),
        box(0D, 1D, 15D, 1D, 15D, 16D),
        box(1D, 1D, 0D, 8D, 15D, 2D),
        box(1D, 1D, 14D, 8D, 15D, 16D)
    );
    public static final VoxelShape OPEN_EAST_SHAPE = Shapes.or(
        box(14D, 15D, 0D, 16D, 16D, 16D),
        box(14D, 0D, 0D, 16D, 1D, 16D),
        box(15D, 1D, 0D, 16D, 15D, 1D),
        box(15D, 1D, 15D, 16D, 15D, 16D),
        box(8D, 1D, 0D, 15D, 15D, 2D),
        box(8D, 1D, 14D, 15D, 15D, 16D)
    );

    public static final VoxelShape CLOSED_NORTH_SHAPE = box(0D, 0D, 0D, 16D, 16D, 2D);
    public static final VoxelShape CLOSED_SOUTH_SHAPE = box(0D, 0D, 14D, 16D, 16D, 16D);
    public static final VoxelShape CLOSED_WEST_SHAPE = box(0D, 0D, 0D, 2D, 16D, 16D);
    public static final VoxelShape CLOSED_EAST_SHAPE = box(14D, 0D, 0D, 16D, 16D, 16D);

    private static final MultiBlock BLOOMERY_CHIMNEY; // Helper for determining how high the chimney is
    private static final EnumMap<Direction, MultiBlock> BASE_MULTIBLOCKS; // If one of those is true, bloomery is formed and can operate (has at least one chimney)
    private static final MultiBlock GATE_Z, GATE_X; // Determines if the gate can stay in place
    private static final Direction[] NORTH_SOUTH_DOWN = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.DOWN};
    private static final Direction[] EAST_WEST_DOWN = new Direction[] {Direction.EAST, Direction.WEST, Direction.DOWN};

    static
    {
        BiPredicate<LevelAccessor, BlockPos> stoneMatcher = (level, pos) -> isBloomeryInsulationBlock(level.getBlockState(pos));
        Predicate<BlockState> insideChimney = state -> state.getBlock() == TFCBlocks.MOLTEN.get() || state.isAir();
        Predicate<BlockState> center = state -> state.is(TFCBlocks.MOLTEN.get()) || state.is(TFCBlocks.BLOOM.get()) || state.isAir();
        BlockPos origin = BlockPos.ZERO;

        // Bloomery center is the charcoal pile pos
        BASE_MULTIBLOCKS = new EnumMap<>(Direction.class);
        final MultiBlock commonMultiblock = new MultiBlock()
            .match(origin, center)
            .match(origin.below(), stoneMatcher);

        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            BASE_MULTIBLOCKS.put(d, commonMultiblock.copy()
                .match(origin.relative(d), state -> Helpers.isBlock(state, TFCBlocks.BLOOMERY.get()))
                .matchEachDirection(origin, stoneMatcher, Direction.Plane.HORIZONTAL.stream().filter(direction -> direction != d).toArray(Direction[]::new), 1)
                .matchEachDirection(origin.relative(d), stoneMatcher, d.getAxis() == Direction.Axis.Z ? EAST_WEST_DOWN : NORTH_SOUTH_DOWN, 1)
                .matchHorizontal(origin.above(), stoneMatcher, 1));
        }

        BLOOMERY_CHIMNEY = new MultiBlock()
            .match(origin, insideChimney)
            .matchHorizontal(origin, stoneMatcher, 1);

        // Gate center is the bloomery gate block
        GATE_Z = new MultiBlock()
            .match(origin, state -> state.is(TFCBlocks.BLOOMERY.get()) || state.isAir())
            .matchEachDirection(origin, stoneMatcher, new Direction[] {Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN}, 1);

        GATE_X = new MultiBlock()
            .match(origin, state -> state.is(TFCBlocks.BLOOMERY.get()) || state.isAir())
            .matchEachDirection(origin, stoneMatcher, new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN}, 1);
    }

    public static boolean isBloomeryInsulationBlock(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.BLOOMERY_INSULATION);
    }

    /**
     * @param centerPos should be the internal block of the bloomery
     */
    public static int getChimneyLevels(Level level, BlockPos centerPos)
    {
        for (int i = 1; i < 1 + TFCConfig.SERVER.bloomeryMaxChimneyHeight.get(); i++)
        {
            BlockPos center = centerPos.above(i);
            if (!BLOOMERY_CHIMNEY.test(level, center))
            {
                return i - 1;
            }
        }
        return TFCConfig.SERVER.bloomeryMaxChimneyHeight.get();
    }

    public static boolean canGateStayInPlace(LevelAccessor level, BlockPos pos, Direction.Axis axis)
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

    public static boolean isFormed(Level level, BlockPos centerPos, Direction facing)
    {
        return facing.getAxis() != Direction.Axis.Y && BASE_MULTIBLOCKS.get(facing).test(level, centerPos);
    }

    public BloomeryBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(OPEN, false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (!state.getValue(LIT)) return;
        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();
        if (random.nextDouble() < 0.1)
        {
            level.playLocalSound(x, y, z, TFCSounds.BLOOMERY_CRACKLE.get(), SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
        }
        level.addParticle(ParticleTypes.SMALL_FLAME, x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0, 0, 0);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!state.getValue(LIT))
        {
            state = state.cycle(OPEN);
            level.setBlockAndUpdate(pos, state);
            Helpers.playSound(level, pos, state.getValue(OPEN) ? SoundEvents.FENCE_GATE_OPEN : SoundEvents.FENCE_GATE_CLOSE);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        return canGateStayInPlace(level, pos, state.getValue(FACING).getAxis()) ? state : Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction placeDirection = null;
        for (Direction d : context.getNearestLookingDirections())
        {
            if (d.getAxis() != Direction.Axis.Y && canGateStayInPlace(context.getLevel(), context.getClickedPos(), d.getAxis()))
            {
                placeDirection = d;
                break;
            }
        }

        if (placeDirection == null)
        {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, placeDirection.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING).add(LIT).add(OPEN));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(OPEN))
        {
            return switch (state.getValue(FACING))
                {
                    case NORTH -> OPEN_NORTH_SHAPE;
                    case SOUTH -> OPEN_SOUTH_SHAPE;
                    case WEST -> OPEN_WEST_SHAPE;
                    case EAST -> OPEN_EAST_SHAPE;
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

    @Override
    protected void beforeRemove(InventoryBlockEntity<?> entity)
    {
        super.beforeRemove(entity);
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
