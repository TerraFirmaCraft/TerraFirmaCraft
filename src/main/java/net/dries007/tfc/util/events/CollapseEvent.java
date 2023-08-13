package net.dries007.tfc.util.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Fired when A collapse is added to a level's {@link net.dries007.tfc.util.tracker.WorldTracker}
 * This event is purely informational, it cannot change the collapse
 * This event is only fired on the server
 */
public class CollapseEvent extends Event
{

    private final Level level;
    private final BlockPos centerPos;
    private final List<BlockPos> nextPositions;
    private final double radiusSquared;

    public CollapseEvent(Level level, BlockPos centerPos, List<BlockPos> nextPositions, double radiusSquared)
    {
        this.level = level;
        this.centerPos = centerPos;
        this.nextPositions = nextPositions;
        this.radiusSquared = radiusSquared;
    }

    public Level getLevel()
    {
        return level;
    }

    public BlockPos getCenterPos()
    {
        return centerPos;
    }

    public List<BlockPos> getNextPositions()
    {
        return nextPositions;
    }

    public double getRadiusSquared()
    {
        return radiusSquared;
    }
}
