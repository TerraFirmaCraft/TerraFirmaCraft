/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interfaces waila behavior to TOP and Hwyla
 */
public interface IWailaEntity
{
    /**
     * @param entity this entity
     * @param currentTooltip the current tooltip, for modification (so other mods can modify ours)
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's body
     */
    @Nonnull
    default List<String> getBodyTooltip(@Nonnull Entity entity, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        return currentTooltip;
    }

    /**
     * @param entity this entity
     * @param currentTooltip the current tooltip, for modification (so other mods can modify ours)
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's head
     */
    @Nonnull
    default List<String> getHeadTooltip(@Nonnull Entity entity, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        return currentTooltip;
    }

    /**
     * @param entity this entity
     * @param currentTooltip the current tooltip, for modification (so other mods can modify ours)
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's tail
     */
    @Nonnull
    default List<String> getTailTooltip(@Nonnull Entity entity, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        return currentTooltip;
    }

    /**
     * @return a List containing all classes you wish to look after in this provider (eg: (TileEntity).class, (Block).class)
     */
    @Nonnull
    default List<Class<?>> getHeadClassList()
    {
        return Collections.emptyList();
    }

    /**
     * @return a List containing all classes you wish to look after in this provider (eg: (TileEntity).class, (Block).class)
     */
    @Nonnull
    default List<Class<?>> getBodyClassList()
    {
        return Collections.emptyList();
    }

    /**
     * @return a List containing all classes you wish to look after in this provider (eg: (TileEntity).class, (Block).class)
     */
    @Nonnull
    default List<Class<?>> getTailClassList()
    {
        return Collections.emptyList();
    }

    /**
     * @return a List containing all classes you wish to look after in this provider (eg: (TileEntity).class, (Block).class)
     */
    @Nonnull
    default List<Class<?>> getNBTClassList()
    {
        return Collections.emptyList();
    }
}
