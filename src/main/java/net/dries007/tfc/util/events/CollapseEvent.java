/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

/**
 * Fired when A real collapse is added to a level's {@link net.dries007.tfc.util.tracker.WorldTracker} or when a fake collapse spawns its particles.
 * This event is purely informational, it cannot change the collapse.
 * This event is only fired on the server.
 * If the collapse is fake, {@link #getRadiusSquared()} will return 0.
 */
public class CollapseEvent extends Event
{
    private final Level level;
    private final BlockPos centerPos;
    private final List<BlockPos> nextPositions;
    private final double radiusSquared;
    private final boolean fake;

    public CollapseEvent(Level level, BlockPos centerPos, List<BlockPos> nextPositions, double radiusSquared, boolean fake)
    {
        this.level = level;
        this.centerPos = centerPos;
        this.nextPositions = nextPositions;
        this.radiusSquared = radiusSquared;
        this.fake = fake;
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

    public boolean isFake()
    {
        return fake;
    }
}
