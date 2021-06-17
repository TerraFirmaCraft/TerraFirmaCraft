/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.objects.items.ItemAnimalHide;

public class ScrapingWrapper implements IRecipeWrapper
{
    private final List<ItemStack> knives;
    private final ItemStack output;
    private final List<ItemStack> hides;

    public ScrapingWrapper(ItemAnimalHide in, ItemAnimalHide out)
    {
        output = new ItemStack(out);
        hides = new ArrayList<>();
        hides.add(new ItemStack(in));
        knives = new ArrayList<>(OreDictionary.getOres("knife"));
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<List<ItemStack>> allInputs = new ArrayList<>();
        allInputs.add(hides);
        allInputs.add(knives);
        ingredients.setInputLists(VanillaTypes.ITEM, allInputs);
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        String text = I18n.format("jei.description.tfc.hide_scraping");
        int i = 0;
        for (String a : minecraft.fontRenderer.listFormattedStringToWidth(text, 150))
        {
            minecraft.fontRenderer.drawString(a, 1, 60f + (minecraft.fontRenderer.FONT_HEIGHT + 2) * i, 0x000000, false);
            i++;
        }
    }
}
