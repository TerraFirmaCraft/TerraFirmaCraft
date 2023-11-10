/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public abstract class SinkNode extends Node
{
    protected SinkNode(BlockPos pos, Direction direction)
    {
        super(pos, EnumSet.of(direction));
    }

    @Override
    public Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
    {
        throw new IllegalStateException("Should never query the rotation of a sink node");
    }
}
