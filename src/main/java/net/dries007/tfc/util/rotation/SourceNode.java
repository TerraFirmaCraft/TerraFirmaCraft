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
 * A convenience class to describe a source node, which always has a non-null rotation, and can access it for modifications through {@link #rotation()}
 */
public abstract class SourceNode extends Node
{
    protected Rotation.Tickable  rotation;

    protected SourceNode(BlockPos pos, EnumSet<Direction> connections, Rotation.Tickable rotation)
    {
        super(pos, connections);
        this.rotation = rotation;
    }

    @NotNull
    @Override
    public abstract Rotation.Tickable rotation(Direction exitDirection);

    @NotNull
    @Override
    public Rotation.Tickable rotation()
    {
        return rotation;
    }
}
