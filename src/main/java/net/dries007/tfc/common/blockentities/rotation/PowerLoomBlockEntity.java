/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.devices.PowerLoomBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SinkNode;

public class PowerLoomBlockEntity extends LoomBlockEntity implements RotationSinkBlockEntity
{
    public static void powerLoomTick(Level level, BlockPos pos, BlockState state, PowerLoomBlockEntity loom)
    {
        final Rotation rotation = loom.node.rotation();
        if (rotation != null)
        {
            loom.progressTicker += Mth.abs(rotation.speed());
            if (loom.progressTicker > PROGRESS_FOR_ONE_PUSH)
            {
                loom.progressTicker = 0;
                // Push the loom
                if (loom.recipe != null &&
                    loom.recipe.getInputCount() == loom.inventory.getStackInSlot(SLOT_RECIPE).getCount() &&
                    loom.progress < loom.recipe.getStepCount() &&
                    !loom.needsProgressUpdate)
                {
                    level.playSound(null, pos, TFCSounds.LOOM_WEAVE.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
                    loom.lastPushed = level.getGameTime();
                    loom.needsProgressUpdate = true;
                    loom.markForSync();
                }

            }
        }
        tick(level, pos, state, loom);
    }

    private static final float PROGRESS_FOR_ONE_PUSH = Mth.TWO_PI / 4f;

    private float progressTicker = 0f;
    private final Node node;

    public PowerLoomBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.POWER_LOOM.get(), pos, state);

        final Direction connection = state.getValue(PowerLoomBlock.FACING).getClockWise();

        this.node = new SinkNode(pos, connection)
        {
            @Override
            public String toString()
            {
                return "PowerLoom[pos=%s]".formatted(pos());
            }
        };
    }

    @Override
    public Node getRotationNode()
    {
        return node;
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
    protected boolean canPushManually()
    {
        return false;
    }
}
