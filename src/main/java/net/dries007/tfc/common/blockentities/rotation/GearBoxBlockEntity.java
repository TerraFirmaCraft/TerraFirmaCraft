/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;

public class GearBoxBlockEntity extends TFCBlockEntity implements RotatingBlockEntity
{
    private final Node node;
    private boolean invalid;

    public GearBoxBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.GEAR_BOX.get(), pos, state);
    }

    public GearBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        // Gearboxes start with no connections initially set, and by hammer, we enable or disable certain connections
        // To model what a gearbox does to rotation direction, we model gearboxes as having a set of four gears, all interlocking
        // - This model of gearbox must have one axis of rotation which is unused
        // - When the output direction is the same axis as the input direction, the rotation is inverted
        // - When the output direction is in any perpendicular axis, the rotation angle is the opposite _convention_ (so an incoming rotation hand -> an outgoing perpendicular hand)

        final EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);
        for (Direction direction : Helpers.DIRECTIONS)
        {
            if (state.getValue(DirectionPropertyBlock.getProperty(direction)))
            {
                connections.add(direction);
            }
        }

        this.invalid = false;
        this.node = new Node(pos, connections) {
            @Override
            public Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
            {
                // Same axis as source direction -> opposite handed-ness, but same axis
                if (sourceDirection.getAxis() == exitDirection.getAxis())
                {
                    return Rotation.of(sourceRotation, sourceRotation.direction().getOpposite());
                }

                // Otherwise, we must be on a perpendicular axis
                // The convention gets reversed, relative to the source direction.
                // If the source (outgoing convention) and rotation direction are the same, the need to _not_ be the same as the exit, and vice versa
                final Direction outputDirection = sourceDirection == sourceRotation.direction()
                    ? exitDirection.getOpposite()
                    : exitDirection;
                return Rotation.of(sourceRotation, outputDirection);
            }

            @Override
            public String toString()
            {
                return "GearBox[pos=%s, connections=%s, source=%s]".formatted(pos(), connections(), source());
            }
        };
    }

    public void updateDirection(Direction direction, boolean value)
    {
        assert level != null;
        if (value)
        {
            node.connections().add(direction);
        }
        else
        {
            node.connections().remove(direction);
        }
        performNetworkAction(NetworkAction.UPDATE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putBoolean("invalid", invalid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        invalid = tag.getBoolean("invalid");
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD);
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
