/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SourceNode;


public class WindmillBlockEntity extends TickableBlockEntity implements RotatingBlockEntity
{
    private static final float MIN_SPEED = Mth.TWO_PI / (20 * 20);
    private static final float MAX_SPEED = Mth.TWO_PI / (8 * 20);
    private static final float LERP_SPEED = MIN_SPEED / (5 * 20);

    public static void serverTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        windmill.checkForLastTickSync();

        clientTick(level, pos, state, windmill);

        if (level.getGameTime() % 40 == 0 && isObstructedBySolidBlocks(level, pos, state.getValue(WindmillBlock.AXIS)))
        {
            // Check every two seconds if the windmill is obstructed, and if so, break
            level.destroyBlock(pos, true);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        final Rotation.Tickable rotation = windmill.node.rotation();

        rotation.tick();

        final float targetSpeed = Mth.map(state.getValue(WindmillBlock.COUNT), 1, 5, MIN_SPEED, MAX_SPEED);
        final float currentSpeed = rotation.speed();
        final float nextSpeed = targetSpeed > currentSpeed
            ? Math.min(targetSpeed, currentSpeed + LERP_SPEED)
            : Math.max(targetSpeed, currentSpeed - LERP_SPEED);

        rotation.setSpeed(nextSpeed);
    }

    public static boolean isObstructedBySolidBlocks(Level level, BlockPos pos, Direction.Axis axis)
    {
        // Check every two seconds if the windmill is obstructed, and if so, break
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dH = -6; dH <= 6; dH++)
        {
            for (int dy = -6; dy <= 6; dy++)
            {
                if (dH * dH + dy * dy < 7 * 7 && (dH != 0 || dy != 0))
                {
                    cursor.setWithOffset(pos, axis == Direction.Axis.X ? 0 : dH, dy, axis == Direction.Axis.Z ? 0 : dH);

                    if (!level.getBlockState(cursor).isAir())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final SourceNode node;
    private boolean invalid;

    public WindmillBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.WINDMILL.get(), pos, state);

        // Windmills can have up to five blades added, which increase their maximum speed.
        // - Rotation speed interpolates as not to have a sharp jump between levels.
        // - Connections are static and only in the horizontal directions specified by the axis
        // - Rotation is always in the 'forward' direction (so windmills
        final Direction.Axis axis = state.getValue(WindmillBlock.AXIS);

        this.invalid = false;
        this.node = new SourceNode(pos, Node.ofAxis(axis), Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE), 0f) {
            @Override
            public String toString()
            {
                return "Windmill[pos=%s, axis=%s]".formatted(pos(), axis);
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        node.rotation().saveToTag(tag);
        tag.putBoolean("invalid", invalid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        node.rotation().loadFromTag(tag);
        invalid = tag.getBoolean("invalid");
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void markAsInvalidInNetwork()
    {
        invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork()
    {
        return invalid;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }
}
