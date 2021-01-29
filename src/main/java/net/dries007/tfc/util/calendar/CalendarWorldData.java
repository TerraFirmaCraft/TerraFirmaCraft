/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CalendarWorldData extends WorldSavedData
{
    private static final String NAME = MOD_ID + "_calendar";

    public static CalendarWorldData get(ServerWorld world)
    {
        return world.getDataStorage().computeIfAbsent(CalendarWorldData::new, NAME);
    }

    private final Calendar calendar;

    public CalendarWorldData()
    {
        super(NAME);
        this.calendar = new Calendar();
    }

    public CalendarWorldData(String name)
    {
        super(name);
        this.calendar = new Calendar();
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        calendar.read(nbt.getCompound("calendar"));
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("calendar", Calendars.SERVER.write());
        return nbt;
    }

    /**
     * Since this updates every tick, and doesn't store a local copy always assume it needs saving to disk
     */
    @Override
    public boolean isDirty()
    {
        return true;
    }

    public Calendar getCalendar()
    {
        return calendar;
    }
}