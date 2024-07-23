/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;

public class QuernComponent extends SimpleItemRecipeComponent<QuernRecipe>
{
    @Override
    protected RecipeType<QuernRecipe> getRecipeType()
    {
        return TFCRecipeTypes.QUERN.get();
    }
}
