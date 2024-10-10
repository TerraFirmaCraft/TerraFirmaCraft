/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.FluidPumpBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;


public class PumpBlockEntity extends TFCBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, PumpBlockEntity pump)
    {
        if (level.getGameTime() % 40 == 0)
        {
            final Direction face = state.getValue(FluidPumpBlock.FACING);
            final BlockPos outputPos = pos.relative(face);
            final BlockState outputState = level.getBlockState(outputPos);
            final @Nullable CrankshaftBlockEntity shaft = CrankshaftBlockEntity.getCrankShaftAt(level, pos, face.getOpposite());
            final @Nullable Fluid fluid = isRotating(shaft) ? searchForFluid(level, pos) : null;

            if (fluid != null)
            {
                final BlockState newState = FluidHelpers.fillWithFluid(outputState, fluid);
                if (newState != null && newState != outputState)
                {
                    level.setBlockAndUpdate(outputPos, newState);
                    level.scheduleTick(outputPos, fluid, fluid.getTickDelay(level));
                }

                if (newState == outputState)
                {
                    // The pump was already filled, so we try and expand the area
                    floodFill(level, pos, outputPos, fluid);
                }
            }
            else
            {
                removePlacedFluid(level, outputPos, outputState);
            }
        }
    }

    private static final int MAX_COST = 16;
    private static final int MAX_FILL = 32;

    private static boolean isRotating(@Nullable CrankshaftBlockEntity shaft)
    {
        if (shaft != null)
        {
            // todo: make pump work
            return false;
        }
        return false;
    }

    /**
     * Attempts to find the fluid that this pump can source, via traversing the network of pipes.
     * @return A fluid if found, otherwise {@code null}
     */
    @Nullable
    private static Fluid searchForFluid(Level level, BlockPos start)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Queue<Path> queue = new ArrayDeque<>();
        final Set<BlockPos> seen = new ObjectOpenHashSet<>(64);

        final BlockPos below = start.below();
        final BlockState stateBelow = level.getBlockState(below);

        if (!isPipe(stateBelow))
        {
            return null;
        }

        enqueueConnections(cursor, level, new Path(stateBelow, below, 1), seen, queue);

        while (!queue.isEmpty())
        {
            final Path prev = queue.poll();
            final Fluid fluid = enqueueConnections(cursor, level, prev, seen, queue);
            if (fluid != null)
            {
                return fluid;
            }
        }
        return null;
    }

    @Nullable
    private static Fluid enqueueConnections(BlockPos.MutableBlockPos cursor, Level level, Path prev, Set<BlockPos> seen, Queue<Path> queue)
    {
        for (final Direction direction : Helpers.DIRECTIONS)
        {
            cursor.setWithOffset(prev.pos, direction);

            if (!seen.contains(cursor))
            {
                final BlockState stateAdj = level.getBlockState(cursor);
                if (isPipe(stateAdj)) // If there are two adjacent pipes, we know they connect as per expected behavior, and so don't have to check the property
                {
                    if (prev.cost < MAX_COST)
                    {
                        final BlockPos posAdj = cursor.immutable();

                        queue.add(new Path(stateAdj, posAdj, 1 + prev.cost));
                        seen.add(posAdj);
                    }
                }
                else if (
                    prev.state.getValue(DirectionPropertyBlock.getProperty(direction)) && // The current pipe still connects in this direction (to nothing)
                    !prev.state.getFluidState().isEmpty() && // And the previous pipe was fluid-logged by something
                    prev.state.getFluidState().getType() == stateAdj.getFluidState().getType() && // And that's the same fluid as this is
                    FluidHelpers.isAirOrEmptyFluid(stateAdj) // And the current is an empty fluid block
                )
                {
                    // Then we can pull from this fluid
                    return stateAdj.getFluidState().getType();
                }
            }
        }
        return null;
    }

    /**
     * Flood fills additional fluid, up to {@link #MAX_FILL} blocks, out from the initial location. This is only done if we can prove the area is bounded, and on exactly one y-level.
     * Multiple pumps can be used to fill larger areas, or areas with depth.
     */
    private static void floodFill(Level level, BlockPos sourcePos, BlockPos pos, Fluid fluid)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Queue<BlockPos> queue = new ArrayDeque<>();
        final Set<BlockPos> seen = new ObjectOpenHashSet<>(MAX_FILL * 2);

        @Nullable BlockPos nextPos = null;
        int total = 0;

        queue.add(pos);

        seen.add(pos);
        seen.add(sourcePos);

        while (!queue.isEmpty())
        {
            final BlockPos prev = queue.poll();
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                cursor.setWithOffset(prev, direction);

                if (seen.contains(cursor))
                {
                    continue;
                }
                final BlockPos current = cursor.immutable();

                seen.add(current);

                final BlockState stateAt = level.getBlockState(cursor);
                if (stateAt.isAir() || isSourceOrFlowingFluidOf(stateAt, fluid))
                {
                    // This location is a valid candidate for a fluid
                    cursor.move(0, -1, 0);

                    final BlockState stateBelow = level.getBlockState(cursor);
                    if (stateBelow.getFluidState().isSourceOfType(fluid) || stateBelow.isFaceSturdy(level, cursor, Direction.UP))
                    {
                        // This location is bounded below
                        if (nextPos == null && (stateAt.isAir() || !stateAt.getFluidState().isSource()))
                        {
                            nextPos = current;
                        }

                        total++;
                        if (total >= MAX_FILL)
                        {
                            // The total space we would have to fill here is too large, abort
                            return;
                        }
                        queue.add(current);
                    }
                    else
                    {
                        // Unbounded (either a hole below, or non-fluid)
                        return;
                    }
                }
                else if (!stateAt.isFaceSturdy(level, cursor, direction.getOpposite()))
                {
                    // This location can't be replaced with a fluid, but can't contain it either, so we abort
                    return;
                }
            }
        }

        if (nextPos != null)
        {
            // The space must have been legal and bounded, and we found a next position we can fill this tick
            level.setBlockAndUpdate(nextPos, fluid.defaultFluidState().createLegacyBlock());
            level.scheduleTick(nextPos, fluid, fluid.getTickDelay(level));
        }
    }

    /**
     * Tries to remove fluid placed by this pump, unless we have evidence that the fluid placed was actually flood filled.
     * In which case those are now "permanent" sources, and will exist even if the pump is removed.
     */
    private static void removePlacedFluid(Level level, BlockPos outputPos, BlockState outputState)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            cursor.setWithOffset(outputPos, direction);
            if (level.getFluidState(cursor).isSourceOfType(outputState.getFluidState().getType()))
            {
                return;
            }
        }
        level.setBlockAndUpdate(outputPos, FluidHelpers.emptyFluidFrom(outputState));
    }

    private static boolean isSourceOrFlowingFluidOf(BlockState state, Fluid fluid)
    {
        return fluid instanceof FlowingFluid flowing ? flowing.isSame(state.getFluidState().getType()) : fluid == state.getFluidState().getType();
    }

    private static boolean isPipe(BlockState state)
    {
        return state.getBlock() == TFCBlocks.STEEL_PIPE.get();
    }


    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.PUMP.get(), pos, state);
    }

    protected PumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void onRemoved()
    {
        assert level != null;

        // Clear any fluid that the pump had created directly
        // We allow the pump to create new infinite sources via it's "filling" mechanic
        final Direction face = getBlockState().getValue(FluidPumpBlock.FACING);
        final BlockPos outputPos = worldPosition.relative(face);
        final BlockState outputState = level.getBlockState(outputPos);

        removePlacedFluid(level, outputPos, outputState);
    }

    private record Path(BlockState state, BlockPos pos, int cost) {}
}
