/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.mechanical.Node;
import net.dries007.tfc.util.mechanical.Rotation;
import net.dries007.tfc.util.mechanical.RotationNetworkManager;

public class GearBoxBlockEntity extends TFCBlockEntity
{
    private final Node node;

    public GearBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        // Gearboxes start with no connections initially set, and by hammer, we enable or disable certain connections
        // To model what a gearbox does to rotation direction, we model gearboxes as having a set of four gears, all interlocking
        // - This model of gearbox must have one axis of rotation which is unused
        // - When the output direction is the same axis as the input direction, the rotation is inverted
        // - When the output direction is in any perpendicular axis, the rotation angle is the opposite _convention_ (so an incoming rotation hand -> an outgoing perpendicular hand)
        this.node = new Node(pos) {
            @Override
            public Rotation rotation(Direction exitDirection)
            {
                if (sourceRotation == null || sourceDirection == null)
                {
                    return null;
                }

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
                return "GearBox[pos=%s, connections=%s, source=%s]".formatted(pos(), connections(), sourceDirection);
            }
        };
    }

    public GearBoxBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.GEAR_BOX.get(), pos, state);
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
        RotationNetworkManager.update(level, node);
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
