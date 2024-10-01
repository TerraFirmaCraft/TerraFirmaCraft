/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * A sink node is a node that only has one possible connection, and is not a source. By virtue of this fact, it is impossible for the node itself to become invalid. This is the node meant to be used on block entities that implement {@link net.dries007.tfc.common.blockentities.rotation.RotationSinkBlockEntity}
 */
@Deprecated
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
