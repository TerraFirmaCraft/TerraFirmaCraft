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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.GrillBlockEntity.*;

/**
 * Custom transfer info for the pot, this is to override {@link #canHandle(PotContainer, PotRecipe)} to respect
 * {@link PotBlockEntity#hasRecipeStarted()} as the slots get locked
 */
public class PotTransferInfo implements IRecipeTransferInfo<PotContainer, PotRecipe>
{
    private final RecipeType<PotRecipe> recipeType;
    private final IRecipeTransferHandlerHelper transferHelper;

    public PotTransferInfo(IRecipeTransferHandlerHelper transferHelper, RecipeType<PotRecipe> recipeType)
    {
        this.transferHelper = transferHelper;
        this.recipeType = recipeType;
    }

    @Override
    public Class<? extends PotContainer> getContainerClass()
    {
        return PotContainer.class;
    }

    @Override
    public Optional<MenuType<PotContainer>> getMenuType()
    {
        return Optional.of(TFCContainerTypes.POT.get());
    }

    @Override
    public RecipeType<PotRecipe> getRecipeType()
    {
        return recipeType;
    }

    @Override
    public boolean canHandle(PotContainer container, PotRecipe recipe)
    {
        if (container.getBlockEntity().hasRecipeStarted())
        {
            return false;
        }
        else
        {
            final IFluidHandler capability = Helpers.getCapability(container.getBlockEntity(), Capabilities.FLUID);

            if (capability != null)
            {
                final FluidStackIngredient fluidIngredient = recipe.getFluidIngredient();
                final FluidStack fluidInTank = capability.getFluidInTank(0);
                if (!fluidIngredient.ingredient().fluids().contains(fluidInTank.getFluid()))
                {
                    return false;
                }
                else
                {
                    return fluidInTank.getAmount() >= fluidIngredient.amount();
                }
            }
            else
            {
                return true;
            }
        }
    }

    @Nullable
    @Override
    public IRecipeTransferError getHandlingError(PotContainer container, PotRecipe recipe)
    {
        final IFluidHandler capability = Helpers.getCapability(container.getBlockEntity(), Capabilities.FLUID);

        if (capability != null)
        {
            final FluidStackIngredient fluidIngredient = recipe.getFluidIngredient();
            final FluidStack fluidInTank = capability.getFluidInTank(0);

            if (!fluidIngredient.ingredient().fluids().contains(fluidInTank.getFluid()))
            {
                if (fluidInTank.isEmpty())
                {
                    final String fluidNames = fluidIngredient.ingredient().all().map(fluid -> fluid.getFluidType().getDescription().getString()).collect(Collectors.joining(", "));
                    return transferHelper.createUserErrorWithTooltip(Component.translatable("tfc.jei.transfer.error.pot_no_fluid", fluidIngredient.amount(), fluidNames));
                }
                else
                {
                    return transferHelper.createUserErrorWithTooltip(Component.translatable("tfc.jei.transfer.error.pot_wrong_fluid", fluidInTank.getDisplayName().getString()));
                }
            }

            if (fluidInTank.getAmount() < fluidIngredient.amount())
            {
                final int missingFluid = fluidIngredient.amount() - fluidInTank.getAmount();
                return transferHelper.createUserErrorWithTooltip(Component.translatable("tfc.jei.transfer.error.pot_not_enough_fluid", missingFluid, fluidInTank.getDisplayName().getString()));
            }
        }

        return transferHelper.createUserErrorWithTooltip(Component.translatable("tfc.jei.transfer.error.pot_started"));
    }

    @Override
    public List<Slot> getRecipeSlots(PotContainer container, PotRecipe recipe)
    {
        return IntStream.range(SLOT_EXTRA_INPUT_START, SLOT_EXTRA_INPUT_END + 1).mapToObj(container::getSlot).toList();
    }

    @Override
    public List<Slot> getInventorySlots(PotContainer container, PotRecipe recipe)
    {
        return IntStream.range(9, 9 + Inventory.INVENTORY_SIZE).mapToObj(container::getSlot).collect(Collectors.toList());
    }
}