/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Map;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.PlantRegrowth;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistrySoilVariant;

public class ConnectedGrassBlock extends Block implements IGrassBlock
{
    // Used to determine connected textures
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    private static final Map<Direction, BooleanProperty> PROPERTIES = ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.WEST, WEST, Direction.SOUTH, SOUTH);

    private final Supplier<? extends Block> dirt;
    @Nullable
    private final Supplier<? extends Block> path;
    @Nullable
    private final Supplier<? extends Block> farmland;

    public ConnectedGrassBlock(Properties properties, Supplier<? extends Block> dirt, @Nullable Supplier<? extends Block> path, @Nullable Supplier<? extends Block> farmland)
    {
        super(properties.hasPostProcess(TFCBlocks::always));

        this.dirt = dirt;
        this.path = path;
        this.farmland = farmland;

        registerDefaultState(stateDefinition.any().setValue(SOUTH, false).setValue(EAST, false).setValue(NORTH, false).setValue(WEST, false).setValue(SNOWY, false));
    }

    ConnectedGrassBlock(Properties properties, SoilBlockType dirtType, RegistrySoilVariant variant)
    {
        this(properties, variant.getBlock(dirtType), variant.getBlock(SoilBlockType.GRASS_PATH), variant.getBlock(SoilBlockType.FARMLAND));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP)
        {
            return stateIn.setValue(SNOWY, Helpers.isBlock(facingState, TFCTags.Blocks.SNOW));
        }
        else if (facing != Direction.DOWN)
        {
            return updateStateFromDirection(level, currentPos, stateIn, facing);
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            scheduleTick(level, pos.relative(direction).above());
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            scheduleTick(level, pos.relative(direction).above());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void scheduleTick(Level level, BlockPos pos)
    {
        final Block block = level.getBlockState(pos).getBlock();
        if (block instanceof IGrassBlock)
        {
            level.scheduleTick(pos, block, 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (!canBeGrass(state, level, pos))
        {
            if (level.isAreaLoaded(pos, 3))
            {
                // Turn to not-grass
                level.setBlockAndUpdate(pos, getDirt());
            }
        }
        else
        {
            final BlockPos.MutableBlockPos posAt = pos.mutable();
            posAt.move(0, 1, 0);
            if (level.getMaxLocalRawBrightness(posAt) >= 9)
            {
                for (int i = 0; i < 4; ++i)
                {
                    posAt.setWithOffset(pos, random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    final BlockState stateAt = level.getBlockState(posAt);
                    if (stateAt.getBlock() instanceof IDirtBlock dirt)
                    {
                        // Spread grass to others
                        final BlockState grassState = dirt.getGrass();
                        if (canPropagate(grassState, level, posAt))
                        {
                            level.setBlockAndUpdate(posAt, updateStateFromNeighbors(level, posAt, grassState));
                        }
                    }
                }
            }
            PlantRegrowth.placeRisingRock(level, pos.above(), random);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        level.setBlock(pos, updateStateFromNeighbors(level, pos, state), 2);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState stateUp = context.getLevel().getBlockState(context.getClickedPos().above());
        return updateStateFromNeighbors(context.getLevel(), context.getClickedPos(), defaultBlockState()).setValue(SNOWY, Helpers.isBlock(stateUp, Blocks.SNOW_BLOCK) || Helpers.isBlock(stateUp, Blocks.SNOW));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST, SNOWY);
    }

    @Override
    public BlockState getDirt()
    {
        return dirt.get().defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility action, boolean simulate)
    {
        if (context.getItemInHand().canPerformAction(action))
        {
            if (action == ItemAbilities.SHOVEL_FLATTEN && TFCConfig.SERVER.enableGrassPathCreation.get() && path != null)
            {
                return path.get().defaultBlockState();
            }
            if (action == ItemAbilities.HOE_TILL && farmland != null && TFCConfig.SERVER.enableFarmlandCreation.get() && HoeItem.onlyIfAirAbove(context))
            {
                return farmland.get().defaultBlockState();
            }
        }
        return null;
    }

    /**
     * Update the state of a grass block from all horizontal directions
     *
     * @param worldIn The world
     * @param pos     The position of the grass block
     * @param state   The initial state
     * @return The updated state
     */
    protected BlockState updateStateFromNeighbors(BlockGetter worldIn, BlockPos pos, BlockState state)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            state = updateStateFromDirection(worldIn, pos, state, direction);
        }
        return state;
    }

    /**
     * Update the state of a grass block from the provided direction
     *
     * @param worldIn   The world
     * @param pos       The position of the grass block
     * @param stateIn   The state of the grass block
     * @param direction The direction in which to look for adjacent, diagonal grass blocks
     * @return The updated state
     */
    protected BlockState updateStateFromDirection(BlockGetter worldIn, BlockPos pos, BlockState stateIn, Direction direction)
    {
        return stateIn.setValue(PROPERTIES.get(direction), worldIn.getBlockState(pos.relative(direction).below()).getBlock() instanceof IGrassBlock);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return DirectionPropertyBlock.rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return DirectionPropertyBlock.mirror(state, mirror);
    }
}