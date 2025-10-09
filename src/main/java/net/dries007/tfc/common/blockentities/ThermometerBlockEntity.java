/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class ThermometerBlockEntity extends TickableBlockEntity
{

    public ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
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
        if (Helpers.isBlock(state, TFCBlocks.THERMOMETER.get()))
        {
            int newPower;
            if (state.getValue(TFCBlockStateProperties.THERMOMETER_ATTACHED))
            {
                final Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
                float temperature = 0;
                if (level.getBlockEntity(pos.relative(direction)) instanceof IHeatable heatable)
                {
                    temperature = heatable.getTemperature();
                }
                newPower = (int) Math.floor(Mth.clampedMap(temperature, 0, Heat.maxVisibleTemperature(), 0, 15));
            }
            else
            {
                newPower = (int) Math.floor(Mth.clampedMap(Climate.get(level).getTemperature(level, pos), -40, 40, 0, 15));
            }

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
