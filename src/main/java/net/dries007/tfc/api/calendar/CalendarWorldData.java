/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.calendar;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CalendarWorldData extends WorldSavedData
{
    private static final String NAME = MOD_ID + ":calendar";

    public static CalendarWorldData get(ServerWorld world)
    {
        return world.getSavedData().getOrCreate(CalendarWorldData::new, NAME);
    }

    private final CalendarTFC calendar;

    public CalendarWorldData()
    {
        super(NAME);
        this.calendar = new CalendarTFC();
    }

    public CalendarWorldData(String name)
    {
        super(name);
        this.calendar = new CalendarTFC();
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        calendar.deserializeNBT(nbt.getCompound("calendar"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("calendar", CalendarTFC.INSTANCE.serializeNBT());
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

    public CalendarTFC getCalendar()
    {
        return calendar;
    }
}