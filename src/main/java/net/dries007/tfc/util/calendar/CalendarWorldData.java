/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@ParametersAreNonnullByDefault
public class CalendarWorldData extends WorldSavedData
{
    private static final String NAME = MOD_ID + ":calendar";

    @Nonnull
    public static CalendarWorldData get(@Nonnull World world)
    {
        MapStorage mapStorage = world.getMapStorage();
        if (mapStorage != null)
        {
            CalendarWorldData data = (CalendarWorldData) mapStorage.getOrLoadData(CalendarWorldData.class, NAME);
            if (data == null)
            {
                // Unable to load data, so assign default values
                data = new CalendarWorldData();
                data.markDirty();
                mapStorage.setData(NAME, data);
            }
            return data;
        }
        throw new IllegalStateException("Map Storage is NULL!");
    }

    CalendarTFC instance;

    @SuppressWarnings("WeakerAccess")
    public CalendarWorldData()
    {
        super(NAME);
        instance = new CalendarTFC();
    }

    @SuppressWarnings("unused")
    public CalendarWorldData(String name)
    {
        super(name);
        instance = new CalendarTFC();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        instance.deserializeNBT(nbt.getCompoundTag("calendar"));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("calendar", instance.serializeNBT());
        return nbt;
    }
}
