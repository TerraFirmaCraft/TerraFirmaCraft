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
import net.dries007.tfc.util.network.RotationOwner;

public class BellowsBlockEntity extends TFCBlockEntity
{
    // Constants used for all TFC bellows related devices
    public static final int BELLOWS_AIR = 200;
    public static final int MAX_DEVICE_AIR_TICKS = 600;

    public static final float MIN_EXTENSION = 0.125f;
    public static final float MAX_EXTENSION = 0.625f;

    public static void tickBoth(Level level, BlockPos pos, BlockState state, BellowsBlockEntity bellows)
    {
        final @Nullable CrankshaftBlockEntity shaft = bellows.getConnectedCrankShaft();
        final @Nullable RotationOwner owner = shaft == null ? null : shaft.getConnectedNetworkOwner();
        final float rotationSpeed = owner == null ? 0f : RotationOwner.getRotationSpeed(owner);

        // Multiple possible situations based on if a shaft is present and/or rotating:
        // 1. A shaft is present and rotating - decrement the progress based on the speed
        // 2. A shaft is present and not rotating - no movement
        // 3. A shaft is not present and powered - adjust the progress based on the manual amount
        // 4. A shaft is not present and not powered - no movement
        if (rotationSpeed > 0f || (shaft == null && bellows.powered))
        {
            bellows.progress -= rotationSpeed > 0f
                ? rotationSpeed * (1f / Mth.TWO_PI)
                : 0.05f;
            if (bellows.progress <= 0f)
            {
                bellows.progress = 0f;
                bellows.powered = false;

                // If the bellows is rotating, immediately re-power the bellows automatically
                if (rotationSpeed > 0f)
                {
                    bellows.pushAirIntoReceivers();
                    bellows.afterPush();
                    bellows.progress = 1f;
                }
            }

            // Only update entities if we are moving
            moveEntitiesOutOfTheWay(level, pos, state);
        }
    }

    private static void moveEntitiesOutOfTheWay(Level level, BlockPos pos, BlockState state)
    {
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

    private boolean powered = false; // If true, the bellows was previously hand powered, and it's current movement will always finish if possible
    private float progress = 0; // [1, 0] indicating the current extension progress of the bellows. 1 = 0 = Wide, 0.5 = Narrow

    public BellowsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BellowsBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BELLOWS.get(), pos, state);
    }

    @Nullable
    public CrankshaftBlockEntity getConnectedCrankShaft()
    {
        return level == null ? null : CrankshaftBlockEntity.getCrankShaftAt(level, worldPosition, getBlockState().getValue(BellowsBlock.FACING).getOpposite());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        progress = tag.getFloat("progress");
        powered = tag.getBoolean("powered");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putFloat("progress", progress);
        tag.putBoolean("powered", powered);
    }

    public float getExtensionLength(float partialTick)
    {
        if (level == null)
        {
            return MIN_EXTENSION;
        }

        // If connected to a rotating crankshaft, we infer an extension length from the length of the shaft extension.
        // The shaft compresses the extension length by the length the shaft has extended
        final CrankshaftBlockEntity entity = getConnectedCrankShaft();
        if (entity != null)
        {
            return Mth.clamp(MIN_EXTENSION + entity.getExtensionLength(partialTick), MIN_EXTENSION, MAX_EXTENSION);
        }

        // The bellows is only allowed to show movement when powered. This means it reverts naturally to a "not powered; don't read progress"
        // state once any shaft is reattached.
        if (!powered)
        {
            return MIN_EXTENSION;
        }

        // Otherwise, the bellows extension depends on the progress
        return progress > 0.5f
            ? Mth.map(progress, 1f, 0.5f, MIN_EXTENSION, MAX_EXTENSION)
            : Mth.map(progress, 0.5f, 0f, MAX_EXTENSION, MIN_EXTENSION);
    }

    public ItemInteractionResult onRightClick()
    {
        if (canInteractWithHandle())
        {
            if (pushAirIntoReceivers())
            {
                // N.B. It is possible for the bellows to be "left" in a pushed state, but with no power
                // In this case, keep the movement smooth
                progress = 1.0f;
                powered = true;
                markForSync();
                afterPush();
            }

            // Return success in both cases because we want the player's arm to swing, because they 'tried'
            // This allows them to signal failure to move
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * @return {@code true} if the bellows can be interacted with via player interaction
     */
    private boolean canInteractWithHandle()
    {
        return !powered && getConnectedCrankShaft() == null;
    }

    private boolean pushAirIntoReceivers()
    {
        assert level != null;

        // We can push EITHER if:
        // 1. there are no receivers (and we're just pushing air into empty space), OR
        // 2. if there are receivers willing to receive air.
        //
        // We CANNOT push if the only receivers we can find are not accepting air - this is to give the player feedback something is wrong
        // (why the receiver cannot receive air).
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
                    if (!level.isClientSide)
                    {
                        consumer.intakeAir(level, airPosition, state, BELLOWS_AIR);
                    }
                }
            }
        }

        return !foundAnyReceivers || foundAnyAllowingReceivers;
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
}
