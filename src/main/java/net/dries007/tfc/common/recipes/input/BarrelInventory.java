/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.input;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface BarrelInventory extends IItemHandlerModifiable, IFluidHandler, NonEmptyInput
{
    /**
     * Run an action while forcing the barrel's underlying state to be mutable, despite the sealed status. This should
     * only be used for updating the outputs on recipe completion, not for interacting with the barrel inventory via
     * external means - that should respect the sealed state.
     */
    void whileMutable(Runnable action);

    /**
     * Insert ItemStacks with overflow storage
     *
     * @param stack unsealedStack to insert
     */
    void insertItemWithOverflow(ItemStack stack);
}