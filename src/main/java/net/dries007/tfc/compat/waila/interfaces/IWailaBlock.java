/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interfaces waila behavior to TOP and Hwyla
 */
public interface IWailaBlock
{
    /**
     * @param world world obj
     * @param pos Block's pos
     * @param currentTooltip the current tooltip, for modification (so other mods can modify ours)
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's body
     */
    @Nonnull
    default List<String> getBodyTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        return currentTooltip;
    }

    /**
     * Only in Hwyla, overrides the title
     * @param world world obj
     * @param pos Block's pos
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's head
     */
    @Nonnull
    default List<String> getHeadTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        return Collections.emptyList();
    }

    /**
     * @param world world obj
     * @param pos Block's pos
     * @param currentTooltip the current tooltip, for modification (so other mods can modify ours)
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's tail
     */
    @Nonnull
    default List<String> getTailTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        return currentTooltip;
    }

    /**
     * @param world world obj
     * @param pos Block's pos
     * @param nbt the server sync nbt (not always possible, but non null for checking)
     * @return a ItemStack to be shown at the side of the panel.
     */
    @Nonnull
    default ItemStack getStack(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        return ItemStack.EMPTY;
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
    default List<Class<?>> getStackClassList()
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
