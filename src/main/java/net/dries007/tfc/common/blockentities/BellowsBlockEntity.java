/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.devices.BellowsBlock;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;

public class BellowsBlockEntity extends TFCBlockEntity
{
    // Constants used for all TFC bellows related devices
    public static final int BELLOWS_AIR = 200;
    public static final int MAX_DEVICE_AIR_TICKS = 600;

    public static void tickBoth(Level level, BlockPos pos, BlockState state, BellowsBlockEntity bellows)
    {
        if (level.getGameTime() - bellows.lastPushed > 20 || !(state.getBlock() instanceof BellowsBlock))
        {
            return;
        }
        final Direction direction = state.getValue(BellowsBlock.FACING).getOpposite();
        final AABB bounds = state.getShape(level, pos).bounds().move(pos);
        List<Entity> list = level.getEntities(null, bounds);
        if (!list.isEmpty())
        {
            for (Entity entity : list)
            {
                if (entity.getPistonPushReaction() != PushReaction.IGNORE)
                {
                    entity.move(MoverType.SHULKER_BOX, new Vec3(0.1 * direction.getStepX(), 0, 0.1 * direction.getStepZ()));
                }
            }

        }
    }

    private long lastPushed = 0L;

    public BellowsBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BELLOWS.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putLong("pushed", lastPushed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        lastPushed = tag.getLong("pushed");
    }

    public float getExtensionLength()
    {
        assert level != null;
        final int time = (int) (level.getGameTime() - lastPushed);
        if (time < 10)
        {
            return time * 0.05f + 0.125f;
        }
        else if (time < 20)
        {
            return (20 - time) * 0.05f + 0.125f;
        }
        return 0.125f;
    }

    public InteractionResult onRightClick()
    {
        assert level != null;

        if (level.getGameTime() - lastPushed < 20)
        {
            return InteractionResult.PASS;
        }

        final Direction direction = getBlockState().getValue(BellowsBlock.FACING);
        final BlockPos facingPos = worldPosition.relative(direction);

        // We can push EITHER if there are no receivers (and we're just pushing air into empty space), OR if there are receivers willing to receive air.
        // We CANNOT push if the only receivers we can find are not accepting air - this is to give the player feedback something is wrong (why the receiver cannot receive air).
        boolean foundAnyReceivers = false;
        boolean foundAnyAllowingReceivers = false;

        for (IBellowsConsumer.Offset offset : IBellowsConsumer.offsets())
        {
            final BlockPos airPosition = worldPosition.above(offset.up())
                .relative(direction, offset.out())
                .relative(direction.getClockWise(), offset.side());
            final BlockState state = level.getBlockState(airPosition);
            if (state.getBlock() instanceof IBellowsConsumer consumer)
            {
                foundAnyReceivers = true;
                if (consumer.canAcceptAir(level, airPosition, state))
                {
                    foundAnyAllowingReceivers = true;
                    consumer.intakeAir(level, airPosition, state, BELLOWS_AIR);
                }
            }
        }

        if (!foundAnyReceivers || foundAnyAllowingReceivers)
        {
            level.playSound(null, worldPosition, TFCSounds.BELLOWS_BLOW.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
            level.addParticle(ParticleTypes.POOF, facingPos.getX() + 0.5f - 0.3f * direction.getStepX(), facingPos.getY() + 0.5f, facingPos.getZ() + 0.5f - 0.3f * direction.getStepZ(), 0, 0.005D, 0);

            lastPushed = level.getGameTime();
            markForSync();
        }
        // Return success in both cases because we want the player's arm to swing, because they 'tried'
        return InteractionResult.SUCCESS;
    }
}
