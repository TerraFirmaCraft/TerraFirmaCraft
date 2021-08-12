/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import javax.annotation.Nullable;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.event.ForgeEventFactory;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dries007.tfc.common.TFCTags;

public class FluidHelpers
{
    /**
     * Checks if a block state is empty other than a provided fluid
     *
     * @return true if the provided state is a source block of it's current fluid
     */
    public static boolean isAirOrEmptyFluid(BlockState state)
    {
        return state.isAir() || state.getBlock() == state.getFluidState().getType().defaultFluidState().createLegacyBlock().getBlock();
    }

    /**
     * Given a block state and a fluid, attempts to fill the block state with the fluid
     * Returns null if the provided combination cannot be filled
     *
     * @param state The state to fill
     * @param fluid The fluid to fill with
     * @return The fluid-logged state, or null if impossible
     */
    @Nullable
    public static BlockState fillWithFluid(BlockState state, Fluid fluid)
    {
        // If the state is already filled. Also handles cases where a block is unable to be filled and we're filling with empty fluid
        if (state.getFluidState().getType() == fluid)
        {
            return state;
        }

        final Block block = state.getBlock();
        if (block instanceof IFluidLoggable)
        {
            final FluidProperty property = ((IFluidLoggable) block).getFluidProperty();
            if (property.canContain(fluid))
            {
                return state.setValue(property, property.keyFor(fluid));
            }
        }
        else if (state.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            if (fluid == Fluids.WATER)
            {
                return state.setValue(BlockStateProperties.WATERLOGGED, true);
            }
            else if (fluid == Fluids.EMPTY)
            {
                return state.setValue(BlockStateProperties.WATERLOGGED, false);
            }
        }
        return null;
    }

    public static boolean isSame(FluidState state, Fluid expected)
    {
        return state.getType().isSame(expected);
    }

    public static boolean canMixFluids(Fluid left, Fluid right)
    {
        return canMixFluids(left) && canMixFluids(right);
    }

    /**
     * If the two fluids are allowed to be considered for mixing
     * This is more lenient than {@link FlowingFluid#isSame(Fluid)} but must assume a few things:
     * - only works with fluids which are an instance of {@link FlowingFluid} (should be all fluids)
     * - assumes that fluid source / flowing handling works like vanilla
     * - fluids must be added to the {@link net.dries007.tfc.common.TFCTags.Fluids#MIXABLE} tag
     *
     * @param fluid A fluid
     * @return true if the fluid should use fluid mixing mechanics
     */
    public static boolean canMixFluids(Fluid fluid)
    {
        return fluid instanceof FlowingFluid && TFCTags.Fluids.MIXABLE.contains(fluid);
    }

    /**
     * This is the main logic from {@link FlowingFluid#getNewLiquid(LevelReader, BlockPos, BlockState)}, but modified to support fluid mixing, and extracted into a static helper method to allow other {@link FlowingFluid} classes (namely, vanilla water) to be modified.
     *
     * @param self               The fluid instance this would've been called upon
     * @param worldIn            The world
     * @param pos                A position
     * @param blockStateIn       The current block state at that position
     * @param canConvertToSource The result of {@code self.canConvertToSource()} as it's protected
     * @param dropOff            The result of {@code self.getDropOff(worldIn)} as it's protected
     * @return The fluid state that should exist at that position
     * @see FlowingFluid#getNewLiquid(LevelReader, BlockPos, BlockState)
     */
    public static FluidState getNewFluidWithMixing(FlowingFluid self, LevelReader worldIn, BlockPos pos, BlockState blockStateIn, boolean canConvertToSource, int dropOff)
    {
        int maxAdjacentFluidAmount = 0; // The maximum height of fluids flowing into this block from the sides
        FlowingFluid maxAdjacentFluid = self;

        int adjacentSourceBlocks = 0; // How many adjacent source blocks that could convert this into a source block
        Object2IntArrayMap<FlowingFluid> adjacentSourceBlocksByFluid = new Object2IntArrayMap<>(2);

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos offsetPos = pos.relative(direction);
            BlockState offsetState = worldIn.getBlockState(offsetPos);
            FluidState offsetFluid = offsetState.getFluidState();

            // Look for adjacent fluids that are the same, for purposes of flow into this fluid
            // canPassThroughWall detects if a fluid state has a barrier - e.g. via a stair edge - that would prevent it from connecting to the current block.
            // todo: mixin accessor
            if (offsetFluid.getType() instanceof FlowingFluid && self.canPassThroughWall(direction, worldIn, pos, blockStateIn, offsetPos, offsetState))
            {
                if (offsetFluid.isSource() && ForgeEventFactory.canCreateFluidSource(worldIn, offsetPos, offsetState, canConvertToSource))
                {
                    adjacentSourceBlocks++;
                    adjacentSourceBlocksByFluid.mergeInt((FlowingFluid) offsetFluid.getType(), 1, Integer::sum);
                }
                // Also record the maximum adjacent fluid, breaking ties with the current fluid
                if (offsetFluid.getAmount() > maxAdjacentFluidAmount || (offsetFluid.getAmount() == maxAdjacentFluidAmount && self.isSame(offsetFluid.getType())))
                {
                    maxAdjacentFluidAmount = offsetFluid.getAmount();
                    maxAdjacentFluid = (FlowingFluid) offsetFluid.getType();
                }
            }
        }

        if (adjacentSourceBlocks >= 2)
        {
            // There are two adjacent source blocks (although potentially of different kinds) - check if the below block is also a source, or if it's a solid block
            // If true, then this block should be converted to a source block as well
            BlockState belowState = worldIn.getBlockState(pos.below());
            FluidState belowFluid = belowState.getFluidState();

            if (belowFluid.isSource() && belowFluid.getType() instanceof FlowingFluid && adjacentSourceBlocksByFluid.getInt(belowFluid.getType()) >= 2)
            {
                // Try and create a source block of the same type as the below
                return ((FlowingFluid) belowFluid.getType()).getSource(false);
            }
            else if (belowState.getMaterial().isSolid())
            {
                // This could potentially form fluid blocks from multiple blocks. It can only override the current source if there's three adjacent equal sources, or form a source if this is the same as three adjacent sources
                FlowingFluid maximumAdjacentSourceFluid = self;
                int maximumAdjacentSourceBlocks = 0;
                for (Object2IntMap.Entry<FlowingFluid> entry : adjacentSourceBlocksByFluid.object2IntEntrySet())
                {
                    if (entry.getIntValue() > maximumAdjacentSourceBlocks || entry.getKey() == self)
                    {
                        maximumAdjacentSourceBlocks = entry.getIntValue();
                        maximumAdjacentSourceFluid = entry.getKey();
                    }
                }

                // Three adjacent (if not same), or two (if same)
                if (maximumAdjacentSourceBlocks >= 3 || (maximumAdjacentSourceBlocks >= 2 && self.isSame(maximumAdjacentSourceFluid)))
                {
                    return maximumAdjacentSourceFluid.getSource(false);
                }
            }
        }

        // At this point, we haven't been able to convert into a source block
        // Check the block above to see if that is flowing downwards into this one (creating a level 8, falling, flowing block)
        // A fluid above, flowing down, will always replace an existing fluid block
        BlockPos abovePos = pos.above();
        BlockState aboveState = worldIn.getBlockState(abovePos);
        FluidState aboveFluid = aboveState.getFluidState();
        // todo: mixin
        if (!aboveFluid.isEmpty() && aboveFluid.getType() instanceof FlowingFluid && self.canPassThroughWall(Direction.UP, worldIn, pos, blockStateIn, abovePos, aboveState))
        {
            return ((FlowingFluid) aboveFluid.getType()).getFlowing(8, true);
        }
        else
        {
            // Nothing above that can flow downwards, so use the highest adjacent fluid amount, after subtracting the drop off (1 for water, 2 for lava)
            int selfFluidAmount = maxAdjacentFluidAmount - dropOff;
            if (selfFluidAmount <= 0)
            {
                // No flow amount into this block
                return Fluids.EMPTY.defaultFluidState();
            }
            // Cause the maximum adjacent fluid to flow into this block
            return maxAdjacentFluid.getFlowing(selfFluidAmount, false);
        }
    }
}
