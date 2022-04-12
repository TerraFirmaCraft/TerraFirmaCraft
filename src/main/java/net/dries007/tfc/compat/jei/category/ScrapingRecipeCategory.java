/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.util.Metal;

public class ScrapingRecipeCategory extends SimpleItemRecipeCategory<ScrapingRecipe>
{
    public ScrapingRecipeCategory(RecipeType<ScrapingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BLACK_BRONZE).get(Metal.ItemType.KNIFE).get()));
    }

    @Override
    protected TagKey<Item> getToolTag()
    {
        return TFCTags.Items.KNIVES;
    }
}
