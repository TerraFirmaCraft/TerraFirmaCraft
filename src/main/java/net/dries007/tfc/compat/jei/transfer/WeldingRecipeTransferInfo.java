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
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public class WeldingRecipeTransferInfo implements IRecipeTransferInfo<AnvilContainer, WeldingRecipe>
{
    private final IRecipeTransferHandlerHelper transferHelper;

    public WeldingRecipeTransferInfo(IRecipeTransferHandlerHelper handlerHelper)
    {
        this.transferHelper = handlerHelper;
    }

    @Override
    public Class<? extends AnvilContainer> getContainerClass()
    {
        return AnvilContainer.class;
    }

    @Override
    public Optional<MenuType<AnvilContainer>> getMenuType()
    {
        return Optional.of(TFCContainerTypes.ANVIL.get());
    }

    @Override
    public RecipeType<WeldingRecipe> getRecipeType()
    {
        return JEIIntegration.WELDING;
    }

    @Override
    public boolean canHandle(AnvilContainer container, WeldingRecipe recipe)
    {
        return container.getBlockEntity().getTier() < recipe.getTier();
    }

    @Nullable
    @Override
    public IRecipeTransferError getHandlingError(AnvilContainer container, WeldingRecipe recipe)
    {
        return transferHelper.createUserErrorWithTooltip(Component.translatable("tfc.jei.transfer.error.anvil_welding_tier_too_low"));
    }

    @Override
    public List<Slot> getRecipeSlots(AnvilContainer container, WeldingRecipe recipe)
    {
        return List.of(container.getSlot(AnvilBlockEntity.SLOT_INPUT_MAIN), container.getSlot(AnvilBlockEntity.SLOT_INPUT_SECOND), container.getSlot(AnvilBlockEntity.SLOT_CATALYST));
    }

    @Override
    public List<Slot> getInventorySlots(AnvilContainer container, WeldingRecipe recipe)
    {
        return IntStream.range(4, 4 + Inventory.INVENTORY_SIZE).mapToObj(container::getSlot).collect(Collectors.toList());
    }
}