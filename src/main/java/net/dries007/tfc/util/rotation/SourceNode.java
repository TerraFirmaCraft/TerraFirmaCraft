/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A convenience class to describe a source node, which always has a non-null rotation, and can access it for modifications through {@link #rotation()}.
 * This is marked as {@code abstract} to encourage implementors to override {@link #toString()} which can be invaluable for debugging purposes.
 */
public abstract class SourceNode extends Node
{
    protected final Rotation.Tickable rotation;

    protected SourceNode(BlockPos pos, EnumSet<Direction> connections, Direction rotationDirection, float speed)
    {
        super(pos, connections);
        this.rotation = Rotation.of(rotationDirection, speed);
    }

    /**
     * Updates this source node on addition to a new network.
     */
    public void updateSource(long networkId)
    {
        this.networkId = networkId;
    }

    @NotNull
    @Override
    public Rotation.Tickable rotation()
    {
        return rotation;
    }

    @NotNull
    @Override
    public final Rotation.Tickable rotation(Direction exitDirection)
    {
        return rotation;
    }

    @Override
    public final Rotation.Tickable rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
    {
        return rotation;
    }
}
