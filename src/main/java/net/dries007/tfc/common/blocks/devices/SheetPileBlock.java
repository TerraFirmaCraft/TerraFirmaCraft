/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

/**
 * A collection of arbitrary metal sheets in a single block
 * Each side contains it's own sheet, which is identified by the face of the block which the side is on - so the UP property identifies the sheet on the TOP of the sheet pile block.
 *
 * todo: this could use {@link net.dries007.tfc.client.IHighlightHandler} for better viewing of which face is targeted, when placing, or breaking. Is it really necessary though?
 */
public class SheetPileBlock extends ExtendedBlock implements EntityBlockExtension, DirectionPropertyBlock
{
    public static final IntegerProperty NORTH_INDEX = IntegerProperty.create("north_index", 2, 5);
    public static final IntegerProperty SOUTH_INDEX = IntegerProperty.create("south_index", 2, 5);
    public static final IntegerProperty EAST_INDEX = IntegerProperty.create("east_index", 2, 5);
    public static final IntegerProperty WEST_INDEX = IntegerProperty.create("west_index", 2, 5);

    private static final BiMap<Direction, IntegerProperty> INDEX_BY_DIRECTION = ImmutableBiMap.<Direction, IntegerProperty>builder()
        .put(Direction.NORTH, NORTH_INDEX)
        .put(Direction.SOUTH, SOUTH_INDEX)
        .put(Direction.EAST, EAST_INDEX)
        .put(Direction.WEST, WEST_INDEX)
        .build();

    public static final Map<BooleanProperty, VoxelShape> SHAPES = new ImmutableMap.Builder<BooleanProperty, VoxelShape>()
        .put(NORTH, box(0, 0, 0, 16, 16, 1))
        .put(SOUTH, box(0, 0, 15, 16, 16, 16))
        .put(EAST, box(15, 0, 0, 16, 16, 16))
        .put(WEST, box(0, 0, 0, 1, 16, 16))
        .put(UP, box(0, 15, 0, 16, 16, 16))
        .put(DOWN, box(0, 0, 0, 16, 1, 16))
        .build();

    public static int faceToIndex(BlockState state, Direction face) {
        if (face == Direction.UP | face == Direction.DOWN) {
            return face.ordinal();
        } else {
            return state.getValue(INDEX_BY_DIRECTION.get(face));
        }
    }

    public static void removeSheet(Level level, BlockPos pos, BlockState state, Direction face, @Nullable Player player, boolean doDrops) {
        final BlockState newState = state.setValue(PROPERTY_BY_DIRECTION.get(face), false);

        level.playSound(null, pos, SoundEvents.METAL_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + 0.2f * level.getRandom().nextFloat());
        if (doDrops && (player == null || !player.isCreative()))
        {
            level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).ifPresent(pile -> {
                final ItemStack stack = pile.removeSheet(faceToIndex(state, face));
                popResourceFromFace(level, pos, face, stack);
            });
        }

        if (isEmpty(newState))
        {
            level.destroyBlock(pos, false);
        }
        else
        {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        }
    }

    public static void addSheet(LevelAccessor level, BlockPos pos, BlockState state, Direction face, ItemStack stack)
    {
        final BlockState newState = state.setValue(PROPERTY_BY_DIRECTION.get(face), true);

        level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
        level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).ifPresent(pile -> pile.addSheet(faceToIndex(state, face), stack));

        final SoundType placementSound = state.getSoundType(level, pos, null);
        level.playSound(null, pos, state.getSoundType(level, pos, null).getPlaceSound(), SoundSource.BLOCKS, (placementSound.getVolume() + 1.0f) / 2.0f, placementSound.getPitch() * 0.8f);
    }

    /**
     * @return The targeted face, if we can find one, or the first non-empty face, if we can find one, or {@code null}, if the block is empty.
     */
    @Nullable
    public static Direction getTargetedFace(Level level, BlockState state, Player player)
    {
        final BlockHitResult result = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.NONE);
        if (result.getType() == HitResult.Type.BLOCK)
        {
            final Vec3 hit = result.getLocation();
            @Nullable Direction firstDirection = null;
            for (Map.Entry<BooleanProperty, VoxelShape> entry : SHAPES.entrySet())
            {
                final BooleanProperty property = entry.getKey();
                if (state.getValue(property))
                {
                    if (firstDirection == null)
                    {
                        firstDirection = DirectionPropertyBlock.getDirection(property);
                    }
                    if (entry.getValue().bounds().move(result.getBlockPos()).inflate(0.01d).contains(hit))
                    {
                        return DirectionPropertyBlock.getDirection(property);
                    }
                }
            }
            return firstDirection;
        }
        return null;
    }

    public static VoxelShape getShapeForSingleFace(Direction direction)
    {
        return SHAPES.get(DirectionPropertyBlock.getProperty(direction));
    }

    public static int countSheets(BlockState state, Predicate<Direction> onlyTheseDirections)
    {
        int count = 0;
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (onlyTheseDirections.test(direction) && state.getValue(DirectionPropertyBlock.getProperty(direction)))
            {
                count++;
            }
        }
        return count;
    }

    public static boolean isEmpty(BlockState state)
    {
        for (BooleanProperty property : PROPERTIES)
        {
            if (state.getValue(property))
            {
                return false;
            }
        }
        return true;
    }

    private final Map<BlockState, VoxelShape> shapeCache;

    public SheetPileBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(DirectionPropertyBlock.setAllDirections(getStateDefinition().any(), false)
            .setValue(NORTH_INDEX, Direction.NORTH.ordinal())
            .setValue(SOUTH_INDEX, Direction.SOUTH.ordinal())
            .setValue(EAST_INDEX, Direction.EAST.ordinal())
            .setValue(WEST_INDEX, Direction.WEST.ordinal())
        );
        shapeCache = DirectionPropertyBlock.makeShapeCache(getStateDefinition(), SHAPES::get);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos)
    {
        if (!neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite()))
        {
            level.scheduleTick(currentPos, this, 0);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction)))
            {
                final BlockPos adjacentPos = pos.relative(direction);
                final BlockState adjacentState = level.getBlockState(adjacentPos);
                if (!adjacentState.isFaceSturdy(level, adjacentPos, direction.getOpposite()))
                {
                    // Neighbor state is not sturdy, so pop off
                    removeSheet(level, pos, state, direction, null, true);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction)))
            {
                final BlockPos adjacentPos = pos.relative(direction);
                final BlockState adjacentState = level.getBlockState(adjacentPos);
                if (!adjacentState.isFaceSturdy(level, adjacentPos, direction.getOpposite()))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        if (level instanceof Level realLevel)
        {
            final Direction targetFace = getTargetedFace(realLevel, state, player);
            if (targetFace != null)
            {
                return level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get())
                    .map(pile -> pile.getSheet(faceToIndex(state, targetFace)))
                    .orElse(ItemStack.EMPTY);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Destroys the block, including setting it to air. Called on both sides, and regardless of if a player has the correct tool to drop the block.
     * We have to manually check the harvest check here to see if we should drop anything.
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        final boolean canActuallyHarvest = state.canHarvestBlock(level, pos, player);
        final Direction targetFace = getTargetedFace(level, state, player);

        playerWillDestroy(level, pos, state, player);

        if (targetFace == null)
        {
            level.destroyBlock(pos, false);
        }
        else
        {
            removeSheet(level, pos, state, targetFace, player, canActuallyHarvest);
        }

        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(PROPERTIES).add(NORTH_INDEX).add(SOUTH_INDEX).add(EAST_INDEX).add(WEST_INDEX));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeCache.get(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH))
                .setValue(EAST, state.getValue(WEST))
                .setValue(SOUTH, state.getValue(NORTH))
                .setValue(WEST, state.getValue(EAST))
                .setValue(NORTH_INDEX, state.getValue(SOUTH_INDEX))
                .setValue(EAST_INDEX, state.getValue(WEST_INDEX))
                .setValue(SOUTH_INDEX, state.getValue(NORTH_INDEX))
                .setValue(WEST_INDEX, state.getValue(EAST_INDEX));
            case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST))
                .setValue(EAST, state.getValue(SOUTH))
                .setValue(SOUTH, state.getValue(WEST))
                .setValue(WEST, state.getValue(NORTH))
                .setValue(NORTH_INDEX, state.getValue(EAST_INDEX))
                .setValue(EAST_INDEX, state.getValue(SOUTH_INDEX))
                .setValue(SOUTH_INDEX, state.getValue(WEST_INDEX))
                .setValue(WEST_INDEX, state.getValue(NORTH_INDEX));
            case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST))
                .setValue(EAST, state.getValue(NORTH))
                .setValue(SOUTH, state.getValue(EAST))
                .setValue(WEST, state.getValue(SOUTH))
                .setValue(NORTH_INDEX, state.getValue(WEST_INDEX))
                .setValue(EAST_INDEX, state.getValue(NORTH_INDEX))
                .setValue(SOUTH_INDEX, state.getValue(EAST_INDEX))
                .setValue(WEST_INDEX, state.getValue(SOUTH_INDEX));
            default -> state;
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH))
                .setValue(SOUTH, state.getValue(NORTH))
                .setValue(NORTH_INDEX, state.getValue(SOUTH_INDEX))
                .setValue(SOUTH_INDEX, state.getValue(NORTH_INDEX));
            case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST))
                .setValue(WEST, state.getValue(EAST))
                .setValue(EAST_INDEX, state.getValue(WEST_INDEX))
                .setValue(WEST_INDEX, state.getValue(EAST_INDEX));
            default -> super.mirror(state, mirror);
        };
    }
}
