/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.transfer;

import java.util.Optional;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;

public class BarrelTransferInfo<R>
    extends BaseTransferInfo<BarrelContainer, R>
    implements IRecipeTransferInfo<BarrelContainer, R>
{
    public BarrelTransferInfo(RecipeType<R> recipeType)
    {
        super(BarrelContainer.class, Optional.of(TFCContainerTypes.BARREL.get()), recipeType, BarrelBlockEntity.SLOTS, BarrelBlockEntity.SLOT_ITEM);
    }

    @Override
    public boolean canHandle(BarrelContainer container, R recipe)
    {
        return container.getBlockEntity().canModify();
    }
}