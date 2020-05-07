/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.interfaces;

import java.util.List;
import javax.annotation.Nonnull;

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
     * Returns a list of tooltips to write on the Hwyla or TOP panel's body.
     *
     * @param world world obj
     * @param pos   Block's pos
     * @param nbt   the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's body
     */
    @Nonnull
    List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt);

    /**
     * Overrides the title (default to the name of the block looked upon)
     *
     * @param world world obj
     * @param pos   Block's pos
     * @param nbt   the server sync nbt (not always possible, but non null for checking)
     * @return a List containing tooltips to write on the panel's head
     */
    @Nonnull
    default String getTitle(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        return "";
    }

    /**
     * Overrides the ItemStack used for icon
     *
     * @param world world obj
     * @param pos   Block's pos
     * @param nbt   the server sync nbt (not always possible, but non null for checking)
     * @return a ItemStack to be shown at the side of the panel.
     */
    @Nonnull
    default ItemStack getIcon(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        return ItemStack.EMPTY;
    }

    /**
     * Returns a list of classes that Hwyla and TOP should assign to this provider
     *
     * @return List of classes (eg: <Block>.class, <TileEntity>.class)
     */
    @Nonnull
    List<Class<?>> getLookupClass();

    /**
     * Overrides this to tell Hwyla and TOP to override the default title (eg: The name of the block you're looking at).
     *
     * @return true if you wish to override the name of the block you're looking at
     */
    default boolean overrideTitle()
    {
        return false;
    }

    /**
     * Overrides this to tell Hwyla and TOP to override the default stack (eg: The icon that is shown when you're looking at something).
     *
     * @return true if you wish to override the stack icon of the block you're looking at
     */
    default boolean overrideIcon()
    {
        return false;
    }
}
