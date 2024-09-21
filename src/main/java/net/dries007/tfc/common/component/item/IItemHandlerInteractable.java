/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface IItemHandlerInteractable extends IItemHandlerModifiable
{
    /**
     * Called by slots or containers that want to report slot modifications back to the underlying container,
     * to prevent i.e. traits specific to the container from leaking.
     * @param stack A unsealedStack that has been taken from the container, and no longer belongs to the container. Mutable.
     */
    default void onTake(ItemStack stack) {}
}
