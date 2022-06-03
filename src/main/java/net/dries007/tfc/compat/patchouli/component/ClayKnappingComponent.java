/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import org.jetbrains.annotations.Nullable;

public class ClayKnappingComponent extends KnappingRecipeComponent<KnappingRecipe>
{
    @Override
    protected RecipeType<KnappingRecipe> getRecipeType()
    {
        return TFCRecipeTypes.CLAY_KNAPPING.get();
    }

    @Nullable
    @Override
    protected ResourceLocation getHighTexture(int ticks)
    {
        return KnappingScreen.CLAY;
    }

    @Nullable
    @Override
    protected ResourceLocation getLowTexture(int ticks)
    {
        return KnappingScreen.CLAY_DISABLED;
    }
}
