/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blocks.devices.BellowsBlock;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.dries007.tfc.util.rotation.Rotation;

public class BellowsBlockEntity extends TFCBlockEntity
{
    // Constants used for all TFC bellows related devices
    public static final int BELLOWS_AIR = 200;
    public static final int MAX_DEVICE_AIR_TICKS = 600;

    public static final float MIN_EXTENSION = 0.125f;
    public static final float MAX_EXTENSION = 0.625f;

    public static void tickBoth(Level level, BlockPos pos, BlockState state, BellowsBlockEntity bellows)
    {
        final @Nullable Rotation networkRotation = bellows.getCrankRotation();
        final float extension = bellows.getExtensionLength(1f);
        if (extension > MAX_EXTENSION - 0.05f && !bellows.justPushed && bellows.lastPushed + 20 < level.getGameTime() && networkRotation != null)
        {
            bellows.doPush();

            // Calculate a 'last pushed' time based on how fast the bellows is currently being operated,
            // which we can infer from the speed (assuming it does not speed up or slow down significantly
            // This prevents really slow moving bellows from triggering this push twice
            bellows.justPushed = true;
            bellows.lastPushed = level.getGameTime() - 20 + Math.max(20, (int) (0.8f * Mth.TWO_PI / networkRotation.speed()));
        }
        else if (bellows.justPushed)
        {
            bellows.justPushed = false;
            bellows.afterPush();
        }

        if (extension >= MAX_EXTENSION)
        {
            return;
        }

        final Direction direction = state.getValue(BellowsBlock.FACING).getOpposite();
        final AABB bounds = state.getShape(level, pos).bounds().move(pos);
        final List<Entity> list = level.getEntities(null, bounds);
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
    private boolean justPushed = false;

    public BellowsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BellowsBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BELLOWS.get(), pos, state);
    }

    public boolean isConnectedToNetwork()
    {
        return getCrankRotation() != null;
    }

    @Nullable
    public Rotation getCrankRotation()
    {
        final CrankshaftBlockEntity crank = getCrankBlockEntity();
        return crank == null ? null : crank.getRotationNode().rotation();
    }

    @Nullable
    public CrankshaftBlockEntity getCrankBlockEntity()
    {
        return level == null ? null : CrankshaftBlockEntity.getCrankShaftAt(level, worldPosition, getBlockState().getValue(BellowsBlock.FACING).getOpposite());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putLong("pushed", lastPushed);
        tag.putBoolean("justPushed", justPushed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        lastPushed = tag.getLong("pushed");
        justPushed = tag.getBoolean("justPushed");
    }

    public float getExtensionLength(float partialTick)
    {
        if (level == null)
        {
            return MIN_EXTENSION;
        }

        // If connected to a rotating crankshaft, we infer an extension length from the length of the shaft extension.
        // The shaft compresses the extension length by the length the shaft has extended
        final CrankshaftBlockEntity entity = getCrankBlockEntity();
        if (entity != null)
        {
            return Mth.clamp(MIN_EXTENSION + entity.getExtensionLength(partialTick), MIN_EXTENSION, MAX_EXTENSION);
        }

        // Otherwise, we fall back to the last pushed time, indicating the player is controlling this block
        final int time = (int) (level.getGameTime() - lastPushed);
        if (time < 10)
        {
            return time * 0.05f + MIN_EXTENSION;
        }
        else if (time < 20)
        {
            return (20 - time) * 0.05f + MIN_EXTENSION;
        }
        return MIN_EXTENSION;
    }

    public ItemInteractionResult onRightClick()
    {
        assert level != null;

        if (level.getGameTime() - lastPushed < 20 || isConnectedToNetwork())
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        doPush();

        // Return success in both cases because we want the player's arm to swing, because they 'tried'
        return ItemInteractionResult.SUCCESS;
    }

    private void doPush()
    {
        assert level != null;

        if (level.isClientSide)
        {
            // Run the effects on server just after we successfully push, as this will reset the lastPushed and justPushed flags
            // Those will be synced to client, and as soon as it receives them, it will run afterPush() through it's tick() method
            return;
        }

        // We can push EITHER if there are no receivers (and we're just pushing air into empty space), OR if there are receivers willing to receive air.
        // We CANNOT push if the only receivers we can find are not accepting air - this is to give the player feedback something is wrong (why the receiver cannot receive air).
        boolean foundAnyReceivers = false;
        boolean foundAnyAllowingReceivers = false;

        final Direction direction = getBlockState().getValue(BellowsBlock.FACING);
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
            lastPushed = level.getGameTime();
            justPushed = true;
            markForSync();
            afterPush();
        }
    }

    /**
     * Runs effects that need to happen on both sides, just after a successful push.
     */
    private void afterPush()
    {
        assert level != null;

        final Direction direction = getBlockState().getValue(BellowsBlock.FACING);
        final BlockPos facingPos = worldPosition.relative(direction);

        level.playSound(null, worldPosition, TFCSounds.BELLOWS_BLOW.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
        level.addParticle(ParticleTypes.POOF, facingPos.getX() + 0.5f - 0.3f * direction.getStepX(), facingPos.getY() + 0.5f, facingPos.getZ() + 0.5f - 0.3f * direction.getStepZ(), 0, 0.005D, 0);
    }

    public int getTicksSincePushed()
    {
        assert level != null;
        return (int) (level.getGameTime() - lastPushed);
    }
}
