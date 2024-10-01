/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.EnumSet;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * A node in a rotation network. This has two primary additions compared to network nodes:
 * <h3>Torque</h3>
 * A node <strong>must</strong> provide a {@link #requiredTorque()}. This must be constant for the node. This represents how much torque
 * this node consumes from the network it is connected to.
 * <p>
 * A node <strong>may</strong> provide a {@link #providedSpeed()} and {@link #providedTorque()}. These represent a speed and torque provided
 * by a source of rotational energy. When any change is made to these values, an {@link Action#UPDATE_IN_NETWORK} update must be done to properly
 * recalculate the speed of the rotational network.
 * <h3>Rotation</h3>
 * Rotation nodes may have a notion of internal "state", for instance, an axle can be rotating in a positive or negative sign direction. Based
 * on their internal state, they may provide a rotation in the directions in {@link #connections()}. This must follow a basic structure:
 * <ul>
 *     <li>When a rotation node is created, it may default to any arbitrary internal state. For example, an axle may assume it's rotation
 *     is always in a positive convention.</li>
 *     <li>When a rotation node is updated upon connecting to a network, it may either accept or reject the connection. This could be, i.e.
 *     because the internal state is not compatible with the incoming rotation.</li>
 *     <li>The internal state must not be graph-dependent</li>
 * </ul>
 */
public abstract class RotationNode extends Node
{
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
     * Return the rotation leaving this node (using a <strong>left hand rule</strong>), in the provided direction. The direction
     * <strong>must</strong> be one exposed in the node's {@link #connections()}, if not this method is liable to throw or return
     * garbage rotation.
     *
     * @param direction The direction, in <strong>outgoing</strong> convention, from this node that the rotation is queried.
     * @return The rotation direction (using a <strong>left hand rule</strong>), provided in the given direction, or {@code null}
     * if there is no rotation in that direction.
     */
    @Contract(pure = true)
    public abstract Direction rotation(Direction direction);

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

    public void loadAdditional(CompoundTag tag)
    {
        valid = tag.getBoolean("valid");
    }

    public void loadAdditionalOnClient(CompoundTag tag)
    {
        connectToNetwork(tag.getLong("networkId"));
    }

    public void saveAdditional(CompoundTag tag)
    {
        saveAdditionalNoClient(tag);
        tag.putLong("networkId", networkId());
    }

    public void saveAdditionalNoClient(CompoundTag tag)
    {
        tag.putBoolean("valid", valid);
    }

    public void setRotationFromOutsideWorld()
    {
        // todo: need to connect this to a "fake" network, and handle that properly on client with a fixed rotation
    }

    public static class Axle extends RotationNode
    {
        private final Direction.Axis axis;
        private Direction rotation;

        public Axle(RotationOwner owner, Direction.Axis axis, float requiredTorque)
        {
            super(owner, NetworkHelpers.ofAxis(axis), requiredTorque);
            this.axis = axis;
            this.rotation = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE); // Default to positive convention
        }

        public Direction rotation()
        {
            return rotation;
        }

        @Override
        public Direction rotation(Direction direction)
        {
            // Axles connect only in their primary axis, and will report the same rotation (either positive or negative) in that axis
            assert direction.getAxis() == axis;
            return rotation;
        }

        @Override
        boolean updateFrom(RotationNode node, Direction connection, Direction rotation, boolean force)
        {
            assert connection.getAxis() == axis;
            assert rotation.getAxis() == axis;
            if (force)
            {
                this.rotation = rotation;
                this.owner.onUpdate();
                return true;
            }
            return this.rotation == rotation;
        }

        @Override
        public void saveAdditional(CompoundTag tag)
        {
            super.saveAdditional(tag);
            NetworkHelpers.saveAxis(tag, "sign", rotation);
        }

        @Override
        public void loadAdditional(CompoundTag tag)
        {
            super.loadAdditional(tag);
            rotation = NetworkHelpers.readAxis(tag, "sign", axis);
        }

        @Override
        public String toString()
        {
            return "Axle[%s, axis=%s, rotation=%s]".formatted(toStringInternal(), axis, rotation);
        }
    }

    /**
     * Gear Boxes represent a four-gear contraption. In this manner, {@link #connections()} may have up to two enabled
     * axies. The gearbox state is then represented by one direction. The encoding is as follows:
     * <ul>
     *     <li>The <strong>axis</strong> of the gearbox rotation represents the axis that is not present in {@link #connections()}. If multiple
     *     axies are not present, it may pick one arbitrarily, but consistently.</li>
     *     <li>The <strong>sign</strong> of the rotation indicates the sign of the rotation of the four remaining sides of the gearbox. Using
     *     a left-hand rule, the two remaining axies will have alternating signs of rotation relative to their emitted direction.</li>
     * </ul>
     */
    public final static class GearBox extends RotationNode
    {
        private static final Direction.Axis[] CYCLE = { Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X };

        Direction convention = Direction.NORTH;

        public GearBox(RotationOwner owner, EnumSet<Direction> connections, float requiredTorque)
        {
            super(owner, connections, requiredTorque);
            convention = Direction.fromAxisAndDirection(calculateConvention(), Direction.AxisDirection.POSITIVE);
        }

        @VisibleForTesting
        public Direction convention()
        {
            return convention;
        }

        /**
         * Must be called to adjust the internal state after any edits to the {@link #connections()} set.
         */
        public void updateConvention()
        {
            final Direction.Axis oldAxis = convention.getAxis();
            final Direction.Axis newAxis = calculateConvention();
            this.convention = NetworkHelpers.getDirection(newAxis, (convention.getAxisDirection() == Direction.AxisDirection.POSITIVE) ^ (oldAxis != newAxis));
        }

        private Direction.Axis calculateConvention()
        {
            if (!isConnectedInAxis(Direction.EAST)) return Direction.Axis.X;
            else if (!isConnectedInAxis(Direction.UP)) return Direction.Axis.Y;
            else
            {
                assert !isConnectedInAxis(Direction.SOUTH) : "GearBox cannot connect in all three axies!";
                return Direction.Axis.Z;
            }
        }

        private boolean isConnectedInAxis(Direction direction)
        {
            return connections().contains(direction) || connections().contains(direction.getOpposite());
        }

        @Override
        public Direction rotation(Direction direction)
        {
            assert direction.getAxis() != convention.getAxis() : "Axis of rotation cannot be convention axis";
            return NetworkHelpers.getDirection(direction.getAxis(), (CYCLE[direction.getAxis().ordinal()] == convention.getAxis()) ^ (direction.getAxisDirection() == convention.getAxisDirection()));
        }

        @Override
        boolean updateFrom(RotationNode node, Direction connection, Direction rotation, boolean force)
        {
            assert connection.getAxis() != convention.getAxis() : "Axis of rotation cannot be convention axis";
            final boolean connects = rotation(connection) == rotation;
            if (force)
            {
                // The easiest way to make this work is just to check the current emitted rotation, and if it's incorrect,
                // then invert the current convention. The convention should be valid as per the connections setup
                if (!connects) convention = convention.getOpposite();
                return true;
            }
            return rotation(connection) == rotation;
        }

        @Override
        public String toString()
        {
            return "GearBox[%s, convention=%s, rotation=[%s]]".formatted(
                toStringInternal(),
                convention,
                connections()
                    .stream()
                    .map(c -> rotation(c).getSerializedName())
                    .collect(Collectors.joining(", ")));
        }
    }
}
