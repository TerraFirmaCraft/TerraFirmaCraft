/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.egg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Capability for egg item
 * Allows egg to be fertilized, how long till hatching and also which entity will be born
 */
public interface IEgg extends INBTSerializable<NBTTagCompound>
{
    /**
     * returns the day which this egg will hatch into the entity
     *
     * @return the day value, as in CalendarTFC#getTotalDays
     */
    long getHatchDay();

    /**
     * return the entity this egg will hatch to
     *
     * @return the Entity that is hatched from this egg, or null if none
     */
    @Nullable
    Entity getEntity(World world);

    /**
     * Is this egg fertilized?
     *
     * @return true if this egg is fertilized.
     */
    boolean isFertilized();

    /**
     * Fertilizes this egg, setting what entity and which day this egg will hatch
     *
     * @param entity   the entity this egg's gonna hatch
     * @param hatchDay the hatch day, as in CalendarTFC#getTotalDays
     */
    void setFertilized(@Nonnull Entity entity, long hatchDay);
}
