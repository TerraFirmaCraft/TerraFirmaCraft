/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.transfer;

import java.util.List;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.compat.jei.JEIIntegration;

/**
 * Transfer handler which filters {@link #transferRecipe(AbstractContainerMenu, Object, IRecipeSlotsView, Player, boolean, boolean)}s {@link IRecipeSlotsView} param
 * to exclude any fluid. This filtered {@link IRecipeSlotsView} is then passed to the {@link #wrapped}
 */
public class FluidIgnoringRecipeTransferHandler<C extends AbstractContainerMenu, R>
    extends BaseTransferInfo<C, R>
    implements IRecipeTransferHandler<C, R>
{
    private final IRecipeTransferHandlerHelper transferHelper;
    private final IRecipeTransferHandler<C, R> wrapped;

    public FluidIgnoringRecipeTransferHandler(IRecipeTransferHandlerHelper transferHelper, IRecipeTransferHandler<C, R> wrapped)
    {
        super(wrapped.getContainerClass(), wrapped.getMenuType(), wrapped.getRecipeType(), -1);
        this.transferHelper = transferHelper;
        this.wrapped = wrapped;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(C container, R recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer)
    {
        List<IRecipeSlotView> slotViews = recipeSlots.getSlotViews().stream().filter(iRecipeSlotView -> iRecipeSlotView.getIngredients(JEIIntegration.FLUID_STACK).findAny().isEmpty()).toList();
        return wrapped.transferRecipe(container, recipe, transferHelper.createRecipeSlotsView(slotViews), player, maxTransfer, doTransfer);
    }
}