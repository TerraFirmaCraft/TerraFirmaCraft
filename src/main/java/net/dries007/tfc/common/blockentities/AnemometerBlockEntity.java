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

import net.dries007.tfc.util.climate.Climate;

public class AnemometerBlockEntity extends TickableBlockEntity
{
    public static final float MAX_SPEED = 1f;
    private static final float LERP_SPEED = Mth.TWO_PI * 0.0005f;
    private float targetSpeed;
    private float actualSpeed;
    private float windSpeed;
    float angle = 0;
    boolean needsUpdate = false;

    protected AnemometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public AnemometerBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.ANEMOMETER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AnemometerBlockEntity anemometer)
    {
        if (anemometer.needsUpdate)
        {
            anemometer.markForSync();
            anemometer.needsUpdate = false;
        }
        if (level.getGameTime() % 40 == 0)
        {
            float wind = Climate.get(level).getWind(level, pos).length();
            anemometer.actualSpeed = windToVisualSpeed(wind);
            if (anemometer.windSpeed != wind)
            {
                anemometer.windSpeed = wind;
                level.updateNeighborsAt(pos, state.getBlock());
                level.updateNeighborsAt(pos.below(), state.getBlock());
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AnemometerBlockEntity anemometer)
    {
        anemometer.angle += anemometer.actualSpeed;
        if (level.getGameTime() % 40 == 0)
        {
            float wind = Climate.get(level).getWind(level, pos).length();
            // consider the most common wind speeds fall between 0 and 0.25
            anemometer.targetSpeed = windToVisualSpeed(wind);
        }
        final float targetSpeed = anemometer.targetSpeed;
        final float currentSpeed = anemometer.actualSpeed;
        anemometer.actualSpeed = targetSpeed > currentSpeed
            ? Math.min(targetSpeed, currentSpeed + LERP_SPEED)
            : Math.max(targetSpeed, currentSpeed - LERP_SPEED);
    }

    private static float windToVisualSpeed(float wind){
        return Mth.clampedMap(wind, 0, 0.5f, 0, MAX_SPEED);
    }

    public float getAngle(float partialTick)
    {
        return angle + actualSpeed * partialTick;
    }

    public int getRedstoneSignal()
    {
        return Math.clamp(Mth.floor(windSpeed * 16), 0, 15);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putFloat("actualSpeed", actualSpeed);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        actualSpeed = tag.getFloat("actualSpeed");
        needsUpdate = true;
    }

}
