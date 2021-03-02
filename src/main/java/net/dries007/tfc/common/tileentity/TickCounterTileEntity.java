package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import net.dries007.tfc.util.calendar.Calendars;

public class TickCounterTileEntity extends TFCTileEntity
{
    private long lastUpdateTick;

    public TickCounterTileEntity()
    {
        this(TFCTileEntities.TICK_COUNTER.get());
    }

    protected TickCounterTileEntity(TileEntityType<?> type)
    {
        super(type);
        resetCounter();
    }

    public long getTicksSinceUpdate()
    {
        return Calendars.SERVER.getTicks() - lastUpdateTick;
    }

    public void resetCounter()
    {
        lastUpdateTick = Calendars.SERVER.getTicks();
    }

    public void reduceCounter(long amount)
    {
        lastUpdateTick += amount;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        lastUpdateTick = nbt.getLong("tick");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putLong("tick", lastUpdateTick);
        return super.save(nbt);
    }
}
