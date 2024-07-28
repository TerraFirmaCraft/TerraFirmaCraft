/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.util.Metal;

public class ScrapingRecipeCategory extends SimpleItemRecipeCategory<ScrapingRecipe>
{
    public ScrapingRecipeCategory(RecipeType<ScrapingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.KNIFE).get()));
    }

    @Override
    protected boolean addItemsToOutputSlot(ScrapingRecipe recipe, IRecipeSlotBuilder output, List<ItemStack> inputs)
    {
        final List<ItemStack> collapsed = collapse(inputs, recipe.getExtraDrop());
        boolean added = false;
        for (ItemStack stack : collapsed)
        {
            if (!stack.isEmpty())
            {
                output.addItemStack(stack);
                added = true;
            }
        }
        return added;
    }

    @Override
    protected TagKey<Item> getToolTag()
    {
        return TFCTags.Items.TOOLS_KNIFE;
    }
}
