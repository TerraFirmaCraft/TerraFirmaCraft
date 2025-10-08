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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

import static net.dries007.tfc.util.calendar.ICalendar.*;

public class ThermometerBlockEntity extends TickableBlockEntity
{

    protected ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public ThermometerBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.THERMOMETER.get(), pos, state);
    }

    public void needsInstantUpdate()
    {
        assert this.level != null;
        updatePower(this.level, this.worldPosition, this.getBlockState());
    }

    public static void updatePower(Level level, BlockPos pos, BlockState state)
    {
        if (state.is(TFCBlocks.THERMOMETER.get()))
        {
            final int newPower = (int) Math.floor(Mth.clampedMap(Climate.get(level).getTemperature(level, pos), -40, 40, 0, 15));
            if (newPower != state.getValue(BlockStateProperties.POWER))
            {
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWER, newPower));
                level.updateNeighborsAt(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite()), state.getBlock());
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThermometerBlockEntity thermometer)
    {
        if (level.getGameTime() % 40 == 0)
        {
            updatePower(level, pos, state);
        }
    }
}
