/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

import net.minecraft.world.item.ItemStack;

/**
 * An interface representing static information about an item container which is composed of a fixed number of internal slots.
 */
public interface ItemContainerInfo
{
    /**
     * @return {@code true} the container can hold the given item {@code stack}.
     */
    boolean canContainItem(ItemStack stack);

    /**
     * @return The capacity (maximum stack size) of each individual slot in the container. Excess amounts past this will prevent insertion.
     */
    int slotCapacity();
}
