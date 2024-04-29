package net.dries007.tfc.common.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.recipes.inventory.EmptyInventory;

public interface BarrelRecipeWrapper extends IItemHandlerModifiable, IFluidHandler, EmptyInventory
{
    /**
     * Must be mutable to perform recipes despite sealed status
     */
    void whileMutable(Runnable action);

    /**
     * Insert ItemStacks with overflow storage
     *
     * @param stack stack to insert
     */
    void insertItemWithOverflow(ItemStack stack);
}