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
import net.dries007.tfc.world.river.River;
import net.dries007.tfc.world.river.RiverHelpers;

public final class RiverEdge
{
    public static final int MIN_WIDTH = 8;
    public static final int MAX_WIDTH = 24;

    private static final int MAX_AFFECTING_GRID_DISTANCE = 1 + Mth.ceil(1.5f * AddRiversAndLakes.RIVER_LENGTH);

    public int width;

    private final River.Vertex source, drain;
    private final MidpointFractal fractal;

    final int minPartX, minPartZ, maxPartX, maxPartZ;

    // River-wide drain/source properties
    private boolean sourceEdge; // `true` if this river has a source edge, `false` if it does not.
    private @Nullable RiverEdge drainEdge; // The drain edge of this river

    public RiverEdge(River.Edge edge, RandomSource random)
    {
        this.source = edge.source();
        this.drain = edge.drain();
        this.fractal = edge.fractal(random, 4);

        final int centerGridX = (int) Math.round(0.5f * (edge.source().x() + edge.drain().x()));
        final int centerGridZ = (int) Math.round(0.5f * (edge.source().y() + edge.drain().y()));

        this.minPartX = Units.gridToPart(centerGridX - MAX_AFFECTING_GRID_DISTANCE);
        this.minPartZ = Units.gridToPart(centerGridZ - MAX_AFFECTING_GRID_DISTANCE);
        this.maxPartX = Units.gridToPart(centerGridX + MAX_AFFECTING_GRID_DISTANCE);
        this.maxPartZ = Units.gridToPart(centerGridZ + MAX_AFFECTING_GRID_DISTANCE);

        this.sourceEdge = false;
        this.drainEdge = null;
    }

    public River.Vertex source()
    {
        return source;
    }

    /**
     * @return {@code true} if this river has a source edge.
     */
    public boolean sourceEdge()
    {
        return sourceEdge;
    }

    public River.Vertex drain()
    {
        return drain;
    }

    /**
     * @return The drain edge connected to this river, if one exists.
     */
    @Nullable
    public RiverEdge drainEdge()
    {
        return drainEdge;
    }

    public MidpointFractal fractal()
    {
        return fractal;
    }

    public int widthSq()
    {
        return width * width;
    }

    /**
     * @return the interpolated width, with a given reference grid position, in grid coordinates.
     */
    public double widthSq(double exactGridX, double exactGridZ)
    {
        // Use a simple lerp, source = 0, drain = 1
        // This is not exactly correct, since the interpolation will be based on
        final double lerpFac = RiverHelpers.projectAlongLine(
            source().x(), source().y(),
            drain().y(), drain().y(),
            exactGridX, exactGridZ
        );

        final double realWidth = Helpers.lerp(lerpFac, width, drainEdge == null ? width : drainEdge.width);

        return realWidth * realWidth;
    }

    /**
     * Links this edge to the provided drain edge via {@code this --> edge}.
     */
    public void linkToDrain(@Nullable RiverEdge edge)
    {
        this.drainEdge = edge;
        if (edge != null)
        {
            edge.sourceEdge = true;
        }
    }
}
