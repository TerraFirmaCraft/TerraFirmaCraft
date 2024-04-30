/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface BarrelInventory extends IItemHandlerModifiable, IFluidHandler, Container
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
     * @param stack stack to insert
     */
    void insertItemWithOverflow(ItemStack stack);
}