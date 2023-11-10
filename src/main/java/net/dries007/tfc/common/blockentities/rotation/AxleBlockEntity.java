/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;

public class AxleBlockEntity extends TFCBlockEntity implements RotatingBlockEntity
{
    private final Node node;
    private boolean invalid;

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    public AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        // Axles translate along a single axis, and continue the input rotation out exactly
        final Direction.Axis axis = state.getValue(AxleBlock.AXIS);
        final Direction forward = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        final Direction backwards = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

        this.invalid = false;
        this.node = new Node(pos, EnumSet.of(forward, backwards)) {
            @Override
            public Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
            {
                assert exitDirection.getAxis() == axis;
                return sourceRotation;
            }

            @Override
            public String toString()
            {
                return "Axle[pos=%s, axis=%s]".formatted(axis, pos());
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("invalid", invalid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        invalid = tag.getBoolean("invalid");
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void onChunkUnloaded()
    {
        super.onChunkUnloaded();
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        performNetworkAction(NetworkAction.ADD);
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
