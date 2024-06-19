/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.transfer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;

public class BarrelTransferInfo<R> implements IRecipeTransferInfo<BarrelContainer, R>
{

    private final RecipeType<R> recipeType;

    public BarrelTransferInfo(RecipeType<R> recipeType)
    {
        this.recipeType = recipeType;
    }

    @Override
    public Class<? extends BarrelContainer> getContainerClass()
    {
        return BarrelContainer.class;
    }

    @Override
    public Optional<MenuType<BarrelContainer>> getMenuType()
    {
        return Optional.of(TFCContainerTypes.BARREL.get());
    }

    @Override
    public RecipeType<R> getRecipeType()
    {
        return recipeType;
    }

    @Override
    public boolean canHandle(BarrelContainer container, R recipe)
    {
        return container.getBlockEntity().canModify();
    }

    @Override
    public List<Slot> getRecipeSlots(BarrelContainer container, R recipe)
    {
        return List.of(container.getSlot(BarrelBlockEntity.SLOT_ITEM));
    }

    @Override
    public List<Slot> getInventorySlots(BarrelContainer container, R recipe)
    {
        return IntStream.range(BarrelBlockEntity.SLOTS, BarrelBlockEntity.SLOTS + Inventory.INVENTORY_SIZE).mapToObj(container::getSlot).collect(Collectors.toList());
    }
}