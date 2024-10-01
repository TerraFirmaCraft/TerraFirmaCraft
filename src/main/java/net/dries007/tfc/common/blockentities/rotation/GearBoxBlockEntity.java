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
import net.dries007.tfc.util.network.Action;
import net.dries007.tfc.util.network.RotationNetworkManager;
import net.dries007.tfc.util.network.RotationNode;
import net.dries007.tfc.util.network.RotationOwner;

public class GearBoxBlockEntity extends TFCBlockEntity implements RotationOwner
{
    private final RotationNode.GearBox node;

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

        this.node = new RotationNode.GearBox(this, connections, RotationNetworkManager.GEARBOX_TORQUE);
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
        node.updateConvention();
        performNetworkAction(Action.UPDATE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        node.saveAdditional(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        node.loadAdditional(tag);
    }

    @Override
    protected void loadAdditionalOnClient(CompoundTag tag, HolderLookup.Provider provider)
    {
        node.loadAdditionalOnClient(tag);
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(Action.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(Action.REMOVE);
    }

    @Override
    public RotationNode getRotationNode()
    {
        return node;
    }
}
