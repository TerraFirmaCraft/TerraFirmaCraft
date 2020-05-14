package net.dries007.tfc.world.tracker;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for the capability attached to {@link net.minecraft.world.World}s
 *
 * Used in various places to record world-specific data, and perform later updates on world ticks
 */
public interface IWorldTracker
{
    void addLandslidePos(BlockPos pos);

    void addCollapseData(CollapseData collapse);

    void tick(World world);
}
