/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.util.math.BlockPos;

public class CollapseData
{
    public final BlockPos pos;
    public final float chance;
    public final Direction direction;

    public CollapseData(BlockPos pos, float chance, Direction direction)
    {
        this.pos = pos;
        this.chance = chance;
        this.direction = direction;
    }

    public enum Direction
    {
        NORTH(.05f),
        SOUTH(.05f),
        EAST(.05f),
        WEST(.05f),
        NORTHEAST(.025f),
        SOUTHEAST(.025f),
        NORTHWEST(.025f),
        SOUTHWEST(.025f);

        public final float decrement;

        Direction(float decrement)
        {
            this.decrement = decrement;
        }

        public BlockPos offset(BlockPos pos)
        {
            switch (this)
            {
                case NORTH:
                    return pos.add(0, 0, -1);
                case SOUTH:
                    return pos.add(0, 0, 1);
                case EAST:
                    return pos.add(+1, 0, 0);
                case WEST:
                    return pos.add(-1, 0, 0);
                case NORTHEAST:
                    return pos.add(1, 0, 1);
                case SOUTHEAST:
                    return pos.add(1, 0, -1);
                case NORTHWEST:
                    return pos.add(-1, 0, +1);
                case SOUTHWEST:
                    return pos.add(-1, 0, -1);
            }
            return pos;
        }
    }
}
