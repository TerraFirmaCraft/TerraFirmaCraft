/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WaterWheelBlock;
import net.dries007.tfc.util.climate.Climate;

public class AnemometerBlockEntity extends TickableBlockEntity
{
    private float speed;

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
        clientTick(level, pos, state, anemometer);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AnemometerBlockEntity anemometer)
    {
        if (level.getGameTime() % 40 == 0)
        {
            Vec2 wind = Climate.get(level).getWind(level, pos);
            anemometer.speed = wind.length();
        }
    }

    public float getSpeed(){
        return speed;
    }


}
