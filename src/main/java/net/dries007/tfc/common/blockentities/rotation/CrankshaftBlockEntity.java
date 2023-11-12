/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SinkNode;

public class CrankshaftBlockEntity extends TFCBlockEntity implements RotationSinkBlockEntity
{
    private final Node node;

    public CrankshaftBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CRANKSHAFT.get(), pos, state);

        // Crank shafts have a single connection, to the CW of their facing, and are thus a sink
        // The piston moves forward in the direction of their facing
        final Direction connection = state.getValue(CrankshaftBlock.FACING).getCounterClockWise();

        this.node = new SinkNode(pos, connection) {
            @Override
            public String toString()
            {
                return "Crankshaft[pos=%s]".formatted(pos());
            }
        };
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
    public Node getRotationNode()
    {
        return node;
    }
}
