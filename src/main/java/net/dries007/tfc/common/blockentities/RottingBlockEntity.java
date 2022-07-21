/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.calendar.Calendars;

public class RottingBlockEntity extends TFCBlockEntity
{
    private long rottenTick = -1L;

    public RottingBlockEntity(BlockEntityType<? extends RottingBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public RottingBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.ROTTING.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        rottenTick = tag.getLong("rotten");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putLong("rotten", rottenTick);
    }

    public boolean isRotten()
    {
        assert level != null;
        return getRottenTick() > Calendars.get(level).getTicks();
    }

    public void initRottenTicks(long timeToDecay)
    {
        assert level != null;
        setRottenTick(Calendars.get(level).getTicks() + timeToDecay);
    }

    public void setRottenTick(long date)
    {
        rottenTick = date;
    }

    public long getRottenTick()
    {
        return rottenTick;
    }
}
