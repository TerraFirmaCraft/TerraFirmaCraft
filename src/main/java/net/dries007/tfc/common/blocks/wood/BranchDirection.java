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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

/**
 * A directionality state for tree-like logs. The direction points towards the root of the tree. Cutting down a tree can then cut down anything which is connected, via pointing *to* the block that was chopped.
 * <p>
 * If you update the order these are declared in, you also <strong>must</strong> update the order of {@link #MIRROR} and {@link #ROTATE}!
 *
 * @see #connected(BlockPos, BlockPos)
 */
public enum BranchDirection implements StringRepresentable
{
    // Disconnected - this is the state for player-placed logs
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
    // N.B. that 'Down' is typically the block used at the base / root of a tree
    // It will 'connect' usually, to a piece of dirt, but that's fine.
    // We don't need a separate state for connected (not NONE) but having incoming connections this way
    DOWN(Direction.DOWN, null, null),
    DOWN_EAST(Direction.DOWN, null, Direction.EAST),
    DOWN_SOUTH_WEST(Direction.DOWN, Direction.SOUTH, Direction.WEST),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH, null),
    DOWN_SOUTH_EAST(Direction.DOWN, Direction.SOUTH, Direction.EAST),

    // Up directions are rare, but necessary for some trees.
    UP_NORTH_WEST(Direction.UP, Direction.NORTH, Direction.WEST),
    UP_NORTH(Direction.UP, Direction.NORTH, null),
    UP_NORTH_EAST(Direction.UP, Direction.NORTH, Direction.EAST),
    UP_WEST(Direction.UP, null, Direction.WEST),
    UP(Direction.UP, null, null),
    UP_EAST(Direction.UP, null, Direction.EAST),
    UP_SOUTH_WEST(Direction.UP, Direction.SOUTH, Direction.WEST),
    UP_SOUTH(Direction.UP, Direction.SOUTH, null),
    UP_SOUTH_EAST(Direction.UP, Direction.SOUTH, Direction.EAST),

    // Trunk positions are intended for the trunk of a 2x2 tree, as they have two unique properties:
    // 1. When removing a trunk block, it will check in the given horizontal direction, for the three other pieces of trunk
    //    If any of them are still present, it will **not** cause the tree to be chopped
    //    To chop down a 2x2 tree, you have to remove all four blocks of the trunk, at the same level
    // 2. When pathing, this block is considered connected to the block directly below (as per usual), but **also** to the blocks in the immediate
    //    horizontal directions - so when you chop down the trunk of a 2x2 tree, all the main trunk will come down with it.
    TRUNK_NORTH_WEST(Direction.DOWN, Direction.NORTH, Direction.WEST, true),
    TRUNK_NORTH_EAST(Direction.DOWN, Direction.NORTH, Direction.EAST, true),
    TRUNK_SOUTH_WEST(Direction.DOWN, Direction.SOUTH, Direction.WEST, true),
    TRUNK_SOUTH_EAST(Direction.DOWN, Direction.SOUTH, Direction.EAST, true);

    // Rotation + Mirror primitives
    // All mirrors and rotations can be build from compositions of these two operations which we hardcode.

    /** Mirror over the Z axis (so flip the X axis) */
    private static final BranchDirection[] MIRROR = {
        NONE,
        NORTH_EAST, NORTH, NORTH_WEST, EAST, WEST, SOUTH_EAST, SOUTH, SOUTH_WEST,
        DOWN_NORTH_EAST, DOWN_NORTH, DOWN_NORTH_WEST, DOWN_EAST, DOWN, DOWN_WEST, DOWN_SOUTH_EAST, DOWN_SOUTH, DOWN_SOUTH_WEST,
        UP_NORTH_EAST, UP_NORTH, UP_NORTH_WEST, UP_EAST, UP, UP_WEST, UP_SOUTH_EAST, UP_SOUTH, UP_SOUTH_WEST,
        TRUNK_NORTH_EAST, TRUNK_NORTH_WEST, TRUNK_SOUTH_EAST, TRUNK_SOUTH_WEST
    };

    /** Rotate 90 degrees clockwise */
    private static final BranchDirection[] ROTATE = {
        NONE,
        NORTH_EAST, EAST, SOUTH_EAST, NORTH, SOUTH, NORTH_WEST, WEST, SOUTH_WEST,
        DOWN_NORTH_EAST, DOWN_EAST, DOWN_SOUTH_EAST, DOWN_NORTH, DOWN, DOWN_SOUTH, DOWN_NORTH_WEST, DOWN_WEST, DOWN_SOUTH_WEST,
        UP_NORTH_EAST, UP_EAST, UP_SOUTH_EAST, UP_NORTH, UP, UP_SOUTH, UP_NORTH_WEST, UP_WEST, UP_SOUTH_WEST,
        TRUNK_NORTH_EAST, TRUNK_SOUTH_EAST, TRUNK_NORTH_WEST, TRUNK_SOUTH_WEST
    };

    private final int dx, dy, dz;
    private final boolean trunk;
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
        this(dy, dz, dx, false);
    }

    BranchDirection(@Nullable Direction dy, @Nullable Direction dz, @Nullable Direction dx, boolean trunk)
    {
        assert dx == null || dx.getAxis() == Direction.Axis.X;
        assert dy == null || dy.getAxis() == Direction.Axis.Y;
        assert dz == null || dz.getAxis() == Direction.Axis.Z;
        assert !trunk || (dx != null && dy != null && dz != null);

        this.dx = dx == null ? 0 : dx.getStepX();
        this.dy = dy == null ? 0 : dy.getStepY();
        this.dz = dz == null ? 0 : dz.getStepZ();
        this.trunk = trunk;
        this.serializedName = name().toLowerCase(Locale.ROOT);
    }

    public int dx()
    {
        return dx;
    }

    public int dy()
    {
        return dy;
    }

    public int dz()
    {
        return dz;
    }

    public BranchDirection rotate(Rotation rotation)
    {
        return switch (rotation)
            {
                case NONE -> this;
                case CLOCKWISE_90 -> rotate();
                case CLOCKWISE_180 -> rotate().rotate();
                case COUNTERCLOCKWISE_90 -> rotate().rotate().rotate();
            };
    }

    public BranchDirection mirror(Mirror mirror)
    {
        return switch (mirror)
            {
                case NONE -> this;
                case LEFT_RIGHT -> mirror().rotate().rotate();
                case FRONT_BACK -> mirror();
            };
    }

    private BranchDirection rotate()
    {
        return ROTATE[this.ordinal()];
    }

    private BranchDirection mirror()
    {
        return MIRROR[this.ordinal()];
    }

    /**
     * @return {@code true} if this direction connects on three directions as part of a trunk, as opposed to one.
     */
    public boolean trunk()
    {
        return trunk;
    }

    /**
     * @return {@code true} if this is a naturally occurring log as part of world generation or tree growth.
     */
    public boolean natural()
    {
        return this != NONE;
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
            return false; // This does not connect to any other block.
        }
        if (trunk)
        {
            // Trunk blocks connect to blocks in _any_ of the adjacent directions. Not individual ones.
            return branch.offset(dx, 0, 0).equals(root)
                || branch.offset(0, dy, 0).equals(root)
                || branch.offset(0, 0, dz).equals(root);
        }
        else
        {
            // Otherwise, we only connect to one adjacent block
            return branch.offset(dx, dy, dz).equals(root);
        }
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }
}