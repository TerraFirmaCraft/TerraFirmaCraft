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
 * Axles have a limit of connectivity - after that length, they will stop rotating
 */
public class AxleNode extends Node
{
    /**
     * The maximum number of consecutive axles that can be connected together without breaking
     */
    private static final int MAX_AXLE_LENGTH = 5;

    public AxleNode(BlockPos pos, EnumSet<Direction> connections)
    {
        super(pos, connections);
    }

    @Override
    public Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
    {
        if (sourceRotation instanceof AxleRotation axleRotation)
        {
            // Increase the length of axles in this connection
            return new AxleRotation(sourceRotation, axleRotation.length + 1);
        }
        // Not connected to an axle, so this is a new rotation
        return new AxleRotation(sourceRotation, 1);
    }

    @Override
    public boolean update(long networkId, Direction sourceDirection, Rotation sourceRotation)
    {
        if (sourceRotation instanceof AxleRotation axleRotation && axleRotation.length == MAX_AXLE_LENGTH)
        {
            // Invalid connection - this axle cannot connect to an existing axle of max length
            onInvalidConnection();
            return false;
        }
        return super.update(networkId, sourceDirection, sourceRotation);
    }

    protected void onInvalidConnection() {}

    record AxleRotation(Rotation from, int length) implements Rotation
    {
        @Override
        public float angle(float partialTick)
        {
            return from.angle(partialTick);
        }

        @Override
        public float speed()
        {
            return from.speed();
        }

        @Override
        public Direction direction()
        {
            return from.direction();
        }
    }
}
