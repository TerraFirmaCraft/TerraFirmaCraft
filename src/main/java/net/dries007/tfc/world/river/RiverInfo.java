package net.dries007.tfc.world.river;

import net.dries007.tfc.world.region.RiverEdge;

public record RiverInfo(RiverEdge edge, Flow flow, float distSq, float widthSq)
{
    public float normDistSq()
    {
        return distSq / widthSq;
    }
}
