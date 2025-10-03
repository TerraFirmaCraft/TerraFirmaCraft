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

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

public class VaneBlockEntity extends TickableBlockEntity
{

    private float angle;

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
        clientTick(level, pos, state, vane);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, VaneBlockEntity vane)
    {
        if (level.getGameTime() % 40 == 0)
        {
            Vec2 wind = Climate.get(level).getWind(level, pos);
            vane.angle = (float) Mth.atan2(wind.y, wind.x);
        }
    }

    public float getAngle(){
        return angle;
    }
}
