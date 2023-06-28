/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverFractal;
import net.dries007.tfc.world.river.RiverHelpers;

public final class RiverEdge
{
    private static final int MAX_AFFECTING_GRID_DISTANCE = 1 + Mth.ceil(1.5f * AddRiversAndLakes.RIVER_LENGTH);
    private final RiverFractal.Vertex sourceVertex, drainVertex;
    private final MidpointFractal fractal;

    final int minPartX, minPartZ, maxPartX, maxPartZ;

    // River-wide drain/source properties
    private boolean source;
    private @Nullable RiverEdge drainEdge;

    public int width;
    public int downstreamWidth;

    public RiverEdge(RiverFractal.Edge edge, RandomSource random)
    {
        this.sourceVertex = edge.source();
        this.drainVertex = edge.drain();
        this.fractal = edge.fractal(random, 4);

        final int centerGridX = Math.round(0.5f * (edge.source().x() + edge.drain().x()));
        final int centerGridZ = Math.round(0.5f * (edge.source().y() + edge.drain().y()));

        this.minPartX = Units.gridToPart(centerGridX - MAX_AFFECTING_GRID_DISTANCE);
        this.minPartZ = Units.gridToPart(centerGridZ - MAX_AFFECTING_GRID_DISTANCE);
        this.maxPartX = Units.gridToPart(centerGridX + MAX_AFFECTING_GRID_DISTANCE);
        this.maxPartZ = Units.gridToPart(centerGridZ + MAX_AFFECTING_GRID_DISTANCE);
    }

    public RiverFractal.Vertex source() { return sourceVertex; }
    public RiverFractal.Vertex drain() { return drainVertex; }

    public MidpointFractal fractal() { return fractal; }

    /**
     * @return the interpolated width, with a given reference grid position, in grid coordinates.
     */
    public float widthSq(float exactGridX, float exactGridZ)
    {
        // Use a simple lerp, source = 0, drain = 1
        // This is not exactly correct, since the interpolation will be based on
        final float lerpFac = RiverHelpers.projectAlongLine(
            source().x(), source().y(),
            drain().y(), drain().y(),
            exactGridX, exactGridZ
        );

        final float realWidth = Helpers.lerp(lerpFac, width, downstreamWidth);

        return realWidth * realWidth;
    }

    public boolean isSource()
    {
        return source;
    }

    public boolean isDrain()
    {
        return drainEdge == null;
    }

    public void setSource(boolean source)
    {
        this.source = source;
    }

    public void setDrainEdge(@Nullable RiverEdge drainEdge)
    {
        this.drainEdge = drainEdge;
    }
}
