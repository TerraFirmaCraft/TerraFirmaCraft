package net.dries007.tfc.common.fluids;

import java.util.Map;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.dries007.tfc.mixin.fluid.FlowingFluidAccessor;

public abstract class MixingFluid extends ForgeFlowingFluid
{
    /**
     * @see net.minecraft.fluid.FlowingFluid#getCacheKey(BlockPos, BlockPos)
     */
    public static short getCacheKey(BlockPos from, BlockPos to)
    {
        int deltaX = to.getX() - from.getX();
        int deltaZ = to.getZ() - from.getZ();
        return (short) ((deltaX + 128 & 255) << 8 | deltaZ + 128 & 255);
    }

    protected MixingFluid(Properties properties)
    {
        super(properties);
    }

    /**
     * @param worldIn The world
     * @param pos     A position
     * @return The number of adjacent source blocks of this fluid
     * @see net.minecraft.fluid.FlowingFluid#sourceNeighborCount(IWorldReader, BlockPos)
     */
    public int sourceNeighborCount(IWorldReader worldIn, BlockPos pos)
    {
        int adjacentSources = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos adjacentPos = pos.relative(direction);
            FluidState adjacentFluid = worldIn.getFluidState(adjacentPos);
            if (isSourceBlockOfThisType(adjacentFluid))
            {
                adjacentSources++;
            }
        }
        return adjacentSources;
    }

    /**
     * This is used for flow checks
     * It is a candidate for overriding to make fluids behave better
     *
     * @param stateIn A fluid state
     * @return If the fluid state is a source block of this fluid
     * @see net.minecraft.fluid.FlowingFluid#isSourceBlockOfThisType(FluidState)
     */
    public boolean isSourceBlockOfThisType(FluidState stateIn)
    {
        return stateIn.getType().isSame(this) && stateIn.isSource();
    }

    /**
     * Copy pasta from {@link net.minecraft.fluid.FlowingFluid#spreadToSides(IWorld, BlockPos, FluidState, BlockState)}
     */
    public void spreadToSides(IWorld world, BlockPos pos, FluidState fluidState, BlockState blockState)
    {
        int adjacentAmount = fluidState.getAmount() - getDropOff(world);
        if (fluidState.getValue(FALLING))
        {
            // Falling indicates this fluid is being fed from above - this is then going to spread like a source block (8 - drop off)
            adjacentAmount = 7;
        }
        if (adjacentAmount > 0)
        {
            // Calculate where the fluid should spread based on each direction
            Map<Direction, FluidState> map = getSpread(world, pos, blockState);
            for (Map.Entry<Direction, FluidState> entry : map.entrySet())
            {
                Direction direction = entry.getKey();
                FluidState fluidstate = entry.getValue();
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = world.getBlockState(blockpos);
                if (canSpreadTo(world, pos, blockState, direction, blockpos, blockstate, world.getFluidState(blockpos), fluidstate.getType()))
                {
                    spreadTo(world, blockpos, blockstate, direction, fluidstate);
                }
            }
        }
    }

    public boolean isWaterHole(IBlockReader world, Fluid fluid, BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState)
    {
        if (!((FlowingFluidAccessor) this).invoke$canPassThroughWall(Direction.DOWN, world, pos, state, adjacentPos, adjacentState))
        {
            return false;
        }
        else
        {
            return adjacentState.getFluidState().getType().isSame(this) || this.canHoldFluid(world, adjacentPos, adjacentState, fluid);
        }
    }

    public boolean canPassThrough(IBlockReader world, Fluid fluid, BlockPos pos, BlockState state, Direction direction, BlockPos adjacentPos, BlockState adjacentState, FluidState otherFluid)
    {
        return !this.isSourceBlockOfThisType(otherFluid) && ((FlowingFluidAccessor) this).invoke$canPassThroughWall(direction, world, pos, state, adjacentPos, adjacentState) && this.canHoldFluid(world, adjacentPos, adjacentState, fluid);
    }

    public boolean canHoldFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        Block block = state.getBlock();
        if (block instanceof ILiquidContainer)
        {
            return ((ILiquidContainer) block).canPlaceLiquid(worldIn, pos, state, fluidIn);
        }
        else if (!(block instanceof DoorBlock) && !block.is(BlockTags.SIGNS) && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN)
        {
            Material material = state.getMaterial();
            if (material != Material.PORTAL && material != Material.STRUCTURAL_AIR && material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT)
            {
                return !material.blocksMotion();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void spread(IWorld worldIn, BlockPos pos, FluidState stateIn)
    {
        // Only spread if the current state has actual fluid
        if (!stateIn.isEmpty())
        {
            BlockState blockStateAt = worldIn.getBlockState(pos);
            BlockPos posBelow = pos.below();
            BlockState blockStateBelow = worldIn.getBlockState(posBelow);

            // First, try and flow downwards. Calculate a fluid state directly below this one
            FluidState fluidstate = getNewLiquid(worldIn, posBelow, blockStateBelow);

            // This checks if the block border is passable, and that the below fluid state returns true to being replaced with this fluid state
            if (canSpreadTo(worldIn, pos, blockStateAt, Direction.DOWN, posBelow, blockStateBelow, worldIn.getFluidState(posBelow), fluidstate.getType()))
            {
                // Try and spread directly below
                spreadTo(worldIn, posBelow, blockStateBelow, Direction.DOWN, fluidstate);

                // Count the number of adjacent blocks horizontally
                // A fluid (regardless of source vs. flowing) will always spread to the sides when there are three or more neighboring source blocks
                // This, notably, happens regardless of if the source blocks are passable
                if (this.sourceNeighborCount(worldIn, pos) >= 3)
                {
                    spreadToSides(worldIn, pos, stateIn, blockStateAt);
                }
            }
            else if (stateIn.isSource() || !this.isWaterHole(worldIn, fluidstate.getType(), pos, blockStateAt, posBelow, blockStateBelow))
            {
                // Source blocks, if they can't spread downwards, will always spread sideways (this happens one tick after they spread downwards)
                // Flowing blocks will only spread sideways if they can't fall downwards, either to a water hole, or directly falling down (the above if chain)
                spreadToSides(worldIn, pos, stateIn, blockStateAt);
            }
        }
    }

    /**
     * Modified to use mixing mechanics, in such a way that can be exposed to other subclasses
     *
     * @see FluidHelpers#getNewFluidWithMixing(FlowingFluid, IWorldReader, BlockPos, BlockState, boolean, int)
     */
    @Override
    protected FluidState getNewLiquid(IWorldReader worldIn, BlockPos pos, BlockState blockStateIn)
    {
        return FluidHelpers.getNewFluidWithMixing(this, worldIn, pos, blockStateIn, canConvertToSource(), getDropOff(worldIn));
    }

    /**
     * This is the recursive helper method for {@link net.minecraft.fluid.FlowingFluid#getSpread(IWorldReader, BlockPos, BlockState)}
     */
    @Override
    public int getSlopeDistance(IWorldReader world, BlockPos pos, int currentDistance, Direction directionFrom, BlockState state, BlockPos posFrom, Short2ObjectMap<Pair<BlockState, FluidState>> nearbyStates, Short2BooleanMap nearbyHoles)
    {
        int minimumDistance = 1000;
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            // Iterate through all directions, which are not the incoming direction
            if (direction != directionFrom)
            {
                // Use a cache of nearby block and fluid states
                BlockPos adjacentPos = pos.relative(direction);
                short relativeKey = getCacheKey(posFrom, adjacentPos);
                Pair<BlockState, FluidState> pair = nearbyStates.computeIfAbsent(relativeKey, key -> {
                    BlockState cachedState = world.getBlockState(adjacentPos);
                    return Pair.of(cachedState, cachedState.getFluidState());
                });

                // The current state and fluid at the adjacent position
                BlockState adjacentState = pair.getFirst();
                FluidState adjacentFluid = pair.getSecond();

                // If we can pass into the adjacent position
                if (this.canPassThrough(world, getFlowing(), pos, state, direction, adjacentPos, adjacentState, adjacentFluid))
                {
                    boolean canDropDown = nearbyHoles.computeIfAbsent(relativeKey, (int3_) -> {
                        BlockPos adjacentBelowPos = adjacentPos.below();
                        BlockState adjacentBelowState = world.getBlockState(adjacentBelowPos);
                        return this.isWaterHole(world, getFlowing(), adjacentPos, adjacentState, adjacentBelowPos, adjacentBelowState);
                    });

                    if (canDropDown)
                    {
                        // Found a location where we can move downwards - return as this is the shortest distance
                        return currentDistance;
                    }

                    // Only check up to the maximum slope find distance
                    if (currentDistance < getSlopeFindDistance(world))
                    {
                        // Recursively look for other slopes
                        int nextSlopeDistance = getSlopeDistance(world, adjacentPos, currentDistance + 1, direction.getOpposite(), adjacentState, posFrom, nearbyStates, nearbyHoles);
                        if (nextSlopeDistance < minimumDistance)
                        {
                            // Found a minimum distance shorter than the current one
                            // In this case, we don't need to store what the fluid state would have to be, as all we care about in the main non-recursive method is the direct adjacent states
                            minimumDistance = nextSlopeDistance;
                        }
                    }
                }
            }
        }
        return minimumDistance;
    }

    @Override
    public Map<Direction, FluidState> getSpread(IWorldReader world, BlockPos pos, BlockState blockState)
    {
        int minimumFlowDistance = 1000;
        Map<Direction, FluidState> adjacentFluidStates = Maps.newEnumMap(Direction.class);
        Short2ObjectMap<Pair<BlockState, FluidState>> nearbyStates = new Short2ObjectOpenHashMap<>();
        Short2BooleanMap nearbyHoles = new Short2BooleanOpenHashMap();

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            // For each adjacent position
            BlockPos adjacentPos = pos.relative(direction);

            // Use a cache of nearby block and fluid states
            // The relative key is based on the distance to the origin relative to this block.
            short relativeKey = getCacheKey(pos, adjacentPos);
            Pair<BlockState, FluidState> nearbyStatePair = nearbyStates.computeIfAbsent(relativeKey, key -> {
                BlockState cachedState = world.getBlockState(adjacentPos);
                return Pair.of(cachedState, cachedState.getFluidState());
            });

            BlockState adjacentState = nearbyStatePair.getFirst();
            FluidState adjacentFluid = nearbyStatePair.getSecond();

            // Compute the fluid that should exist at the adjacent position
            FluidState newAdjacentFluid = getNewLiquid(world, adjacentPos, adjacentState);

            // If we can pass into the adjacent position
            if (this.canPassThrough(world, newAdjacentFluid.getType(), pos, blockState, direction, adjacentPos, adjacentState, adjacentFluid))
            {
                BlockPos adjacentBelowPos = adjacentPos.below();
                boolean canDropDown = nearbyHoles.computeIfAbsent(relativeKey, key -> {
                    BlockState adjacentBelowState = world.getBlockState(adjacentBelowPos);
                    return this.isWaterHole(world, getFlowing(), adjacentPos, adjacentState, adjacentBelowPos, adjacentBelowState);
                });
                int flowDistance;
                if (canDropDown)
                {
                    flowDistance = 0;
                }
                else
                {
                    flowDistance = getSlopeDistance(world, adjacentPos, 1, direction.getOpposite(), adjacentState, pos, nearbyStates, nearbyHoles);
                }

                // Found a shorter path than the existing one - clear all previous
                if (flowDistance < minimumFlowDistance)
                {
                    adjacentFluidStates.clear();
                }

                // This direction has a shortest path which is reachable from the current shortest distance - record it
                // If multiple paths are all reachable, all will be entered into the map
                if (flowDistance <= minimumFlowDistance)
                {
                    adjacentFluidStates.put(direction, newAdjacentFluid);
                    minimumFlowDistance = flowDistance;
                }
            }
        }

        return adjacentFluidStates;
    }

    @Override
    public void tick(World worldIn, BlockPos pos, FluidState state)
    {
        if (!state.isSource())
        {
            // Flowing fluid ticks
            // Inside this statement, we know we're in a non-waterlogged block, as flowing fluids cannot be waterlogged.
            FluidState fluidAt = getNewLiquid(worldIn, pos, worldIn.getBlockState(pos));
            int spreadDelay = getSpreadDelay(worldIn, pos, state, fluidAt);
            if (fluidAt.isEmpty())
            {
                // The current state should have no fluid in it - set the block to empty, and then call spread with an empty fluid (mojang why?)
                state = fluidAt;
                worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            else if (!fluidAt.equals(state)) // equals() is not overridden, so this is just a state object equality check
            {
                // The new fluid state is not the same as the existing one. In vanilla, this could happen if the state height was changed, or if it was converted into a source block.
                // In tfc, this may also happen when a fluid with a larger amount replaces this one.
                state = fluidAt;
                BlockState blockstate = fluidAt.createLegacyBlock();
                worldIn.setBlock(pos, blockstate, 2);
                worldIn.getLiquidTicks().scheduleTick(pos, fluidAt.getType(), spreadDelay);
                worldIn.updateNeighborsAt(pos, blockstate.getBlock());
            }
        }
        spread(worldIn, pos, state);
    }

    public static class Flowing extends MixingFluid
    {
        public Flowing(Properties properties)
        {
            super(properties);
        }

        public boolean isSource(FluidState state)
        {
            return false;
        }

        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder.add(LEVEL));
        }
    }

    public static class Source extends MixingFluid
    {
        public Source(Properties properties)
        {
            super(properties);
        }

        public boolean isSource(FluidState state)
        {
            return true;
        }

        public int getAmount(FluidState state)
        {
            return 8;
        }
    }
}
