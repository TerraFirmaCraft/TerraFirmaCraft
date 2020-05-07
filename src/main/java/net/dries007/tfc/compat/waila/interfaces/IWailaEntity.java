/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interfaces waila behavior to TOP and Hwyla
 */
public interface IWailaEntity
{
    /**
     * Returns a list of tooltips to write on the Hwyla or TOP panel's body.
     *
     * @param entity this entity
     * @param nbt    the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's body
     */
    @Nonnull
    List<String> getTooltip(@Nonnull Entity entity, @Nonnull NBTTagCompound nbt);

    /**
     * Overrides the title (default to the name of the entity looked upon)
     *
     * @param entity this entity
     * @param nbt    the server sync nbt (not always possible, but non null for checking)
     * @return the title
     */
    @Nonnull
    default String getTitle(@Nonnull Entity entity, @Nonnull NBTTagCompound nbt)
    {
        return "";
    }

    /**
     * Returns a list of classes that Hwyla and TOP should assign to this provider
     *
     * @return List of classes (eg: <Entity>.class)
     */
    @Nonnull
    List<Class<?>> getLookupClass();

    /**
     * Overrides this to tell Hwyla and TOP to override the default title (eg: The name of the entity you're looking at).
     *
     * @return true if you wish to override the name of the entity you're looking at
     */
    default boolean overrideTitle()
    {
        return false;
    }
}
