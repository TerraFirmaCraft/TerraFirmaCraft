/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public abstract class RotationNode extends Node
{
    public static Direction readDirection(CompoundTag tag, String key)
    {
        return Helpers.DIRECTIONS[tag.getByte(key)];
    }

    public static void saveDirection(CompoundTag tag, String key, Direction direction)
    {
        tag.putByte(key, (byte) direction.ordinal());
    }

    protected final RotationOwner owner;
    protected boolean valid = true;

    private final float requiredTorque;

    protected RotationNode(RotationOwner owner, EnumSet<Direction> connections, float requiredTorque)
    {
        super(owner.getBlockPos(), connections);
        this.owner = owner;
        this.requiredTorque = requiredTorque;
    }

    /**
     * @param direction The direction, in <strong>outgoing</strong> convention, from this node that the rotation is queried.
     * @return The rotation direction (using a <strong>left hand rule</strong>), provided in the given direction, or {@code null}
     * if there is no rotation in that direction.
     */
    @Nullable
    @Contract(pure = true)
    abstract Direction rotation(Direction direction);

    /**
     * @param node The node we are updating from.
     * @param connection A connection, in <strong>outgoing</strong> convention, from {@code this} to {@code node}.
     * @param rotation The rotation emitted by {@code node}, in the direction towards {@code this}, that we are updating from.
     */
    @Contract("_, _, _, true -> true")
    abstract boolean updateFrom(RotationNode node, Direction connection, Direction rotation, boolean force);

    protected float requiredTorque()
    {
        return requiredTorque;
    }

    protected float providedTorque()
    {
        return 0;
    }

    protected float providedSpeed()
    {
        return 0;
    }

    public void loadFromTag(CompoundTag tag)
    {
        valid = tag.getBoolean("valid");
        connectToNetwork(tag.getLong("networkId"));
    }

    public void saveToTag(CompoundTag tag)
    {
        tag.putBoolean("valid", valid);
        tag.putLong("networkId", networkId());
    }

    public float convention()
    {
        return 1;
    }

    public static class Axle extends RotationNode
    {
        private final Direction.Axis axis;
        private Direction rotation = Direction.NORTH;

        public Axle(RotationOwner owner, Direction.Axis axis, float requiredTorque)
        {
            super(owner, Node.ofAxis(axis), requiredTorque);
            this.axis = axis;
        }

        @Override
        Direction rotation(Direction direction)
        {
            return rotation;
        }

        @Override
        boolean updateFrom(RotationNode node, Direction connection, Direction rotation, boolean force)
        {
            assert connection.getAxis() == axis;
            if (force)
            {
                this.rotation = rotation;
                this.owner.markForSync();
                return true;
            }
            return this.rotation == rotation;
        }

        @Override
        public void saveToTag(CompoundTag tag)
        {
            super.saveToTag(tag);
            saveDirection(tag, "rotation", rotation);
        }

        @Override
        public void loadFromTag(CompoundTag tag)
        {
            super.loadFromTag(tag);
            rotation = readDirection(tag, "rotation");
        }

        @Override
        public float convention()
        {
            return rotation.getAxisDirection().getStep();
        }

        @Override
        public String toString()
        {
            return "Axle[%s, axis=%s, rotation=%s]".formatted(toStringInternal(), axis, rotation);
        }
    }

    public final static class GearBox extends RotationNode
    {
        Direction sourceDirection = Direction.NORTH; // Outgoing convention
        Direction sourceRotation = Direction.NORTH;

        public GearBox(RotationOwner owner, EnumSet<Direction> connections, float requiredTorque)
        {
            super(owner, connections, requiredTorque);
        }

        @Override
        Direction rotation(Direction direction)
        {
            // If the same axis, then return either the source rotation, or opposite, if we're flipping sides
            if (direction == sourceDirection) return sourceRotation;
            if (direction == sourceDirection.getOpposite()) return sourceRotation.getOpposite();

            // If a different axis, then we need the *opposite* convention, but based on the output direction
            return sourceRotation == sourceDirection ? direction.getOpposite() : direction;
        }

        @Override
        boolean updateFrom(RotationNode node, Direction connection, Direction rotation, boolean force)
        {
            if (force)
            {
                sourceDirection = connection;
                sourceRotation = rotation;
                return true;
            }
            return rotation(connection) == rotation;
        }

        @Override
        public String toString()
        {
            return "GearBox[%s, source=%s, rotation=%s]".formatted(toStringInternal(), sourceDirection, sourceRotation);
        }
    }
}
