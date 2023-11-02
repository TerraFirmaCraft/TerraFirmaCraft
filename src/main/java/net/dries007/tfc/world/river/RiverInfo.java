/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import net.dries007.tfc.world.region.RiverEdge;

/**
 * @param distSq Square distance, in blocks, to this river edge.
 * @param widthSq Square width, in blocks, of this river edge. The raw value is clamped between [8, 18] so the square value is between [64, 324]
 */
public record RiverInfo(RiverEdge edge, Flow flow, double distSq, double widthSq)
{
    /**
     * @return A normalized distance to this river. 0 = at the center of the river, 1 = at the edge of the river (~roughly).
     */
    public double normDistSq()
    {
        return distSq / widthSq;
    }
}
