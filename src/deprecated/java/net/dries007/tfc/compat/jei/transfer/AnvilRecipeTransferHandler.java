/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.transfer;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforge.neoforged.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SelectAnvilPlanPacket;

/**
 * Custom transfer handler which selects the anvil plan as part of the transfer process
 */
public class AnvilRecipeTransferHandler<C extends AbstractContainerMenu>
    extends BaseTransferInfo<C, AnvilRecipe>
    implements IRecipeTransferHandler<C, AnvilRecipe>
{
    private final IRecipeTransferHandler<C, AnvilRecipe> transferHandler;

    public AnvilRecipeTransferHandler(IRecipeTransferHandler<C, AnvilRecipe> transferHandler)
    {
        super(transferHandler.getContainerClass(), transferHandler.getMenuType(), transferHandler.getRecipeType(), -1);
        this.transferHandler = transferHandler;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(C container, AnvilRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer)
    {
        final IRecipeTransferError transferError = transferHandler.transferRecipe(container, recipe, recipeSlots, player, maxTransfer, doTransfer);
        // Non-null return means some sort of error happened and nothing will get transferred
        if (transferError != null)
        {
            return transferError;
        }

        if (doTransfer)
        {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SelectAnvilPlanPacket(recipe));
        }

        return null;
    }
}