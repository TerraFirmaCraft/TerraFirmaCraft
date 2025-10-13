/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.tracker.WeatherHelpers;

public class VaneBlockEntity extends TickableBlockEntity
{
    public static final float MAX_SPEED = 0.025f;
    private float targetAngle;
    private float angle;
    private float speed;
    private boolean shouldRotate = false;
    private boolean needsUpdate = false;

    protected VaneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public VaneBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.VANE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VaneBlockEntity vane)
    {
        if (vane.needsUpdate)
        {
            vane.markForSync();
            vane.needsUpdate = false;
        }
        if (level.getGameTime() % 40 == 0)
        {
            Vec2 wind = Climate.get(level).getWind(level, pos);
            float angle = (float) Mth.atan2(wind.y, wind.x);
            vane.angle = angle;
            if (vane.targetAngle != angle)
            {
                vane.targetAngle = angle;
                level.updateNeighborsAt(pos, state.getBlock());
                level.updateNeighborsAt(pos.below(), state.getBlock());
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, VaneBlockEntity vane)
    {
        if (level.getGameTime() % 40 == 0)
        {
            Vec2 wind = Climate.get(level).getWind(level, pos);
            vane.targetAngle = (float) Mth.atan2(wind.y, wind.x);
            vane.speed = Mth.clampedMap(wind.length(), 0, 0.5f, 0, MAX_SPEED);
        }

        final float targetAngle = vane.targetAngle;

        final float currentAngle = vane.angle;
        vane.shouldRotate = Math.abs(currentAngle - targetAngle) > vane.speed;

        if (vane.shouldRotate)
        {
            vane.angle = targetAngle > currentAngle
                ? Math.min(targetAngle, currentAngle + vane.speed)
                : Math.max(targetAngle, currentAngle - vane.speed);
        }
        else
        {
            float rand = (level.random.nextFloat() - 0.5f);
            if (Math.abs(rand) < 0.3)
            {
                rand = rand < 0 ? -0.3f : 0.3f;
            }
            vane.targetAngle = vane.targetAngle + rand * Mth.TWO_PI / 72;
            vane.shouldRotate = true;
        }
    }

    public float getAngle(float partialTick)
    {
        if (shouldRotate)
        {
            return targetAngle > angle
                ? Math.min(targetAngle, angle + speed * partialTick)
                : Math.max(targetAngle, angle - speed * partialTick);
        }
        return angle;
    }

    public float getWrappedPositiveAngle()
    {
        return WeatherHelpers.wrappedPositiveAngle(targetAngle);
    }

    public int getRedstoneSignal()
    {
        return Math.clamp(Mth.floor((getWrappedPositiveAngle() / Mth.TWO_PI) * 16), 0, 15);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putFloat("targetAngle", targetAngle);
        tag.putFloat("angle", angle);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        targetAngle = tag.getFloat("targetAngle");
        angle = tag.getFloat("angle");
        needsUpdate = true;
    }
}