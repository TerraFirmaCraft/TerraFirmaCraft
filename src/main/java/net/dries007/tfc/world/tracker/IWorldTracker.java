package net.dries007.tfc.world.tracker;

import java.util.Collection;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for the capability attached to {@link net.minecraft.world.World}s
 *
 * Used in various places to record world-specific data, and perform later updates on world ticks
 */
public interface IWorldTracker
{
    /**
     * Marks a position to be checked for a landslide on the next world tick
     */
    void addLandslidePos(BlockPos pos);

    /**
     * Starts a collapse, which will propagate / continue over the next several iterations until finished
     */
    void addCollapseData(CollapseData collapse);

    /**
     * Marks a series of positions for immediate collapse checks. Similar to starting a collapse but from specific positions.
     */
    void addCollapsePositions(BlockPos centerPos, Collection<BlockPos> positions);

    void tick(World world);
}
