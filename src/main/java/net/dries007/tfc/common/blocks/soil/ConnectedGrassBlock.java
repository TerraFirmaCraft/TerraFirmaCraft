/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;

@SuppressWarnings("deprecation")
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
    private final Supplier<? extends Block> grassPath;
    @Nullable
    private final Supplier<? extends Block> farmland;

    public ConnectedGrassBlock(Properties properties, SoilBlockType dirtType, SoilBlockType.Variant soilType)
    {
        this(properties, TFCBlocks.SOIL.get(dirtType).get(soilType), TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(soilType), TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(soilType));
    }

    public ConnectedGrassBlock(Properties properties, Supplier<? extends Block> dirt, @Nullable Supplier<? extends Block> grassPath, @Nullable Supplier<? extends Block> farmland)
    {
        super(properties.hasPostProcess(TFCBlocks::always));

        this.dirt = dirt;
        this.grassPath = grassPath;
        this.farmland = farmland;

        registerDefaultState(stateDefinition.any().setValue(SOUTH, false).setValue(EAST, false).setValue(NORTH, false).setValue(WEST, false).setValue(SNOWY, false));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP)
        {
            return stateIn.setValue(SNOWY, facingState.is(TFCTags.Blocks.SNOW));
        }
        else if (facing != Direction.DOWN)
        {
            return updateStateFromDirection(worldIn, currentPos, stateIn, facing);
        }
        return stateIn;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        worldIn.getBlockTicks().scheduleTick(pos, this, 0);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            worldIn.getBlockTicks().scheduleTick(pos.relative(direction).above(), this, 0);
        }
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            worldIn.getBlockTicks().scheduleTick(pos.relative(direction).above(), this, 0);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!canBeGrass(state, worldIn, pos))
        {
            if (worldIn.isAreaLoaded(pos, 3))
            {
                // Turn to not-grass
                worldIn.setBlockAndUpdate(pos, getDirt());
            }
        }
        else
        {
            if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9)
            {
                for (int i = 0; i < 4; ++i)
                {
                    BlockPos posAt = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    BlockState stateAt = worldIn.getBlockState(posAt);
                    if (stateAt.getBlock() instanceof IDirtBlock)
                    {
                        // Spread grass to others
                        BlockState grassState = ((IDirtBlock) stateAt.getBlock()).getGrass();
                        if (canPropagate(grassState, worldIn, posAt))
                        {
                            worldIn.setBlockAndUpdate(posAt, updateStateFromNeighbors(worldIn, posAt, grassState));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (worldIn.isAreaLoaded(pos, 2))
        {
            worldIn.setBlock(pos, updateStateFromNeighbors(worldIn, pos, state), 2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState stateUp = context.getLevel().getBlockState(context.getClickedPos().above());
        return updateStateFromNeighbors(context.getLevel(), context.getClickedPos(), defaultBlockState()).setValue(SNOWY, stateUp.is(Blocks.SNOW_BLOCK) || stateUp.is(Blocks.SNOW));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
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
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType)
    {
        if (toolType == ToolType.HOE && TFCConfig.SERVER.enableFarmlandCreation.get() && farmland != null)
        {
            return farmland.get().defaultBlockState();
        }
        else if (toolType == ToolType.SHOVEL && TFCConfig.SERVER.enableGrassPathCreation.get() && grassPath != null)
        {
            return grassPath.get().defaultBlockState();
        }
        return state;
    }

    /**
     * When a grass block changes (is placed or added), this is called to send updates to all diagonal neighbors to update their state from this one
     *
     * @param world The world
     * @param pos   The position of the changing grass block
     */
    protected void updateSurroundingGrassConnections(IWorld world, BlockPos pos)
    {
        if (world.isAreaLoaded(pos, 2))
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                BlockPos targetPos = pos.above().relative(direction);
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.getBlock() instanceof IGrassBlock)
                {
                    world.setBlock(targetPos, updateStateFromDirection(world, targetPos, targetState, direction.getOpposite()), 2);
                }
            }
        }
    }

    /**
     * Update the state of a grass block from all horizontal directions
     *
     * @param worldIn The world
     * @param pos     The position of the grass block
     * @param state   The initial state
     * @return The updated state
     */
    protected BlockState updateStateFromNeighbors(IBlockReader worldIn, BlockPos pos, BlockState state)
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
    protected BlockState updateStateFromDirection(IBlockReader worldIn, BlockPos pos, BlockState stateIn, Direction direction)
    {
        return stateIn.setValue(PROPERTIES.get(direction), worldIn.getBlockState(pos.relative(direction).below()).getBlock() instanceof IGrassBlock);
    }
}