package net.dries007.tfc.world.river;

import net.dries007.tfc.world.region.RiverEdge;

/**
 *
 * @param edge
 * @param flow
 * @param distSq Square distance, in blocks, to this river edge.
 * @param widthSq Square width, in blocks, of this river edge. The raw value is clamped between [8, 18] so the square value is between [64, 324]
 */
public record RiverInfo(RiverEdge edge, Flow flow, float distSq, float widthSq)
{
    /**
     * @return A normalized distance to this river. 0 = at the center of the river, 1 = at the edge of the river (~roughly).
     */
    public float normDistSq()
    {
        return distSq / widthSq;
    }
}
