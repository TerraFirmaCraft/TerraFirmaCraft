/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.mechanical.AxleBlock;
import net.dries007.tfc.util.mechanical.Node;
import net.dries007.tfc.util.mechanical.Rotation;
import net.dries007.tfc.util.mechanical.RotationCapability;
import net.dries007.tfc.util.mechanical.RotationNetworkManager;

public class AxleBlockEntity extends TFCBlockEntity
{
    private final Node node;

    public AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        // Axles translate along a single axis, and continue the input rotation out exactly
        final Direction.Axis axis = state.getValue(AxleBlock.AXIS);
        final Direction forward = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        final Direction backwards = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

        this.node = new Node(pos, EnumSet.of(forward, backwards)) {
            @Nullable
            @Override
            public Rotation rotation(Direction exitDirection)
            {
                assert exitDirection.getAxis() == axis;
                return rotation();
            }

            @Override
            public String toString()
            {
                return "Axle[pos=%s, axis=%s]".formatted(axis, pos());
            }
        };
    }

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    public float getRotationAngle(float partialTick)
    {
        final Rotation rotation = node.rotation();
        return rotation == null ? 0 : rotation.angle(partialTick);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == RotationCapability.CAPABILITY)
        {
            return node.handler();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved()
    {
        assert level != null;
        super.setRemoved();
        RotationNetworkManager.remove(level, node);
    }

    @Override
    public void onChunkUnloaded()
    {
        assert level != null;
        super.onChunkUnloaded();
        RotationNetworkManager.remove(level, node);
    }

    @Override
    public void onLoad()
    {
        assert level != null;
        super.onLoad();
        RotationNetworkManager.add(level, node);
    }
}
