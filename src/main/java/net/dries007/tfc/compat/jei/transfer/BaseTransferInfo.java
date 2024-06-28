/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.transfer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

/**
 * Implements portions of both {@link IRecipeTransferInfo} and {@link IRecipeTransferHandler}
 */
public abstract class BaseTransferInfo<C extends AbstractContainerMenu, R>
{
    private final Class<? extends C> containerClass;
    private final Optional<MenuType<C>> menuType;
    private final RecipeType<R> recipeType;
    private final int[] recipeSlots;
    private final int inventorySlotStart;

    protected BaseTransferInfo(Class<? extends C> containerClass, Optional<MenuType<C>> menuType, RecipeType<R> recipeType, int inventorySlotStart, int... recipeSlots)
    {
        this.containerClass = containerClass;
        this.menuType = menuType;
        this.recipeType = recipeType;
        this.recipeSlots = recipeSlots;
        this.inventorySlotStart = inventorySlotStart;
    }

    public final Class<? extends C> getContainerClass()
    {
        return containerClass;
    }

    public final Optional<MenuType<C>> getMenuType()
    {
        return menuType;
    }

    public final RecipeType<R> getRecipeType()
    {
        return recipeType;
    }

    public final List<Slot> getRecipeSlots(C container, R recipe)
    {
        return Arrays.stream(recipeSlots).mapToObj(container::getSlot).toList();
    }

    public final List<Slot> getInventorySlots(C container, R recipe)
    {
        return IntStream.range(inventorySlotStart, inventorySlotStart + Inventory.INVENTORY_SIZE).mapToObj(container::getSlot).toList();
    }
}
