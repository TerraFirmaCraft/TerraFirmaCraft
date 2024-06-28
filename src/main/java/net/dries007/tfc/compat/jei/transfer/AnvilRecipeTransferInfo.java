/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.transfer;

import java.util.Optional;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public class AnvilRecipeTransferInfo
    extends BaseTransferInfo<AnvilContainer, AnvilRecipe>
    implements IRecipeTransferInfo<AnvilContainer, AnvilRecipe>
{
    private final IRecipeTransferHandlerHelper transferHelper;

    public AnvilRecipeTransferInfo(IRecipeTransferHandlerHelper handlerHelper)
    {
        super(AnvilContainer.class, Optional.of(TFCContainerTypes.ANVIL.get()), JEIIntegration.ANVIL, 4, AnvilBlockEntity.SLOT_INPUT_MAIN, AnvilBlockEntity.SLOT_INPUT_SECOND, AnvilBlockEntity.SLOT_CATALYST);
        this.transferHelper = handlerHelper;
    }

    @Override
    public boolean canHandle(AnvilContainer container, AnvilRecipe recipe)
    {
        return recipe.isCorrectTier(container.getBlockEntity().getTier());
    }

    @Nullable
    @Override
    public IRecipeTransferError getHandlingError(AnvilContainer container, AnvilRecipe recipe)
    {
        return transferHelper.createUserErrorWithTooltip(Component.translatable("tfc.jei.transfer.error.anvil_forging_tier_too_low"));
    }
}