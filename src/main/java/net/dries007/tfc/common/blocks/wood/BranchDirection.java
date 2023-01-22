/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

/**
 * A directionality state for tree-like logs. The direction points towards the root of the tree. Cutting down a tree can then cut down anything which is connected, via pointing *to* the block that was chopped.
 *
 * @see #connected(BlockPos, BlockPos)
 */
public enum BranchDirection implements StringRepresentable
{
    // No direction connects to any adjacent block
    NONE,

    // Those with a direction only connect to blocks in the offset of their direction
    NORTH_WEST(Direction.NORTH, Direction.WEST),
    NORTH(Direction.NORTH, null),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    WEST(null, Direction.WEST),
    EAST(null, Direction.EAST),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    SOUTH(Direction.SOUTH, null),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST),

    DOWN_NORTH_WEST(Direction.DOWN, Direction.NORTH, Direction.WEST),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH, null),
    DOWN_NORTH_EAST(Direction.DOWN, Direction.NORTH, Direction.EAST),
    DOWN_WEST(Direction.DOWN, null, Direction.WEST),
    DOWN(Direction.DOWN, null, null),
    DOWN_EAST(Direction.DOWN, null, Direction.EAST),
    DOWN_SOUTH_WEST(Direction.DOWN, Direction.SOUTH, Direction.WEST),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH, null),
    DOWN_SOUTH_EAST(Direction.DOWN, Direction.SOUTH, Direction.EAST),

    TRUNK_NORTH_WEST(Direction.DOWN, Direction.NORTH, Direction.WEST),
    TRUNK_NORTH_EAST(Direction.DOWN, Direction.NORTH, Direction.EAST),
    TRUNK_SOUTH_WEST(Direction.DOWN, Direction.SOUTH, Direction.WEST),
    TRUNK_SOUTH_EAST(Direction.DOWN, Direction.SOUTH, Direction.EAST);

    private final @Nullable Direction dx, dy, dz;
    private final String serializedName;

    BranchDirection()
    {
        this(null, null, null);
    }

    BranchDirection(@Nullable Direction dz, @Nullable Direction dx)
    {
        this(null, dz, dx);
    }

    BranchDirection(@Nullable Direction dy, @Nullable Direction dz, @Nullable Direction dx)
    {
        assert dx == null || dx.getAxis() == Direction.Axis.X;
        assert dy == null || dy.getAxis() == Direction.Axis.Y;
        assert dz == null || dz.getAxis() == Direction.Axis.Z;

        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.serializedName = name().toLowerCase(Locale.ROOT);
    }

    /**
     * @param root The root position.
     * @param branch The branch position, which has the current branch direction.
     * @return {@code true} if the log at {@code branch}, with the branch direction {@code this}, is connected to {@code root}.
     */
    public boolean connected(BlockPos root, BlockPos branch)
    {
        if (this == NONE)
        {
            return true;
        }
        return (dx == null ? root.getX() == branch.getX() : root.getX() == branch.getX() + dx.getStepX())
            && (dy == null ? root.getY() == branch.getY() : root.getY() == branch.getY() + dy.getStepY())
            && (dz == null ? root.getZ() == branch.getZ() : root.getZ() == branch.getZ() + dz.getStepZ());
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }
}
