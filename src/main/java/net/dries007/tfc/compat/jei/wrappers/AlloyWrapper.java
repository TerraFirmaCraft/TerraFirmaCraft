package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.items.metal.ItemIngot;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlloyWrapper implements IRecipeWrapper
{
    private String[] slotContent = {"", "", "", ""};
    private AlloyRecipe recipe;

    public AlloyWrapper(AlloyRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        int i = 0;
        List<List<ItemStack>> allInputs = new ArrayList<>();
        for (Metal metal : recipe.getMetals().keySet())
        {
            int min = (int) (recipe.getMetals().get(metal).getMin() * 100);
            int max = (int) (recipe.getMetals().get(metal).getMax() * 100);
            slotContent[i] = min + "-" + max + "%";
            NonNullList<ItemStack> possibleSmeltable = NonNullList.create();
            possibleSmeltable.add(new ItemStack(ItemIngot.get(metal, Metal.ItemType.INGOT)));
            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
            {
                if (ore.getMetal() == metal)
                {
                    possibleSmeltable.add(new ItemStack(ItemOreTFC.get(ore)));
                }
            }
            allInputs.add(possibleSmeltable);
            i++;
        }
        ingredients.setInputLists(VanillaTypes.ITEM, allInputs);
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(ItemIngot.get(this.recipe.getResult(), Metal.ItemType.INGOT)));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        for (int i = 0; i < 4; i++)
        {
            int row = i / 2;
            int column = i % 2;
            float x = 20f + column * 60f;
            float y = 17f + row * 26f;
            String text = slotContent[i];
            minecraft.fontRenderer.drawString(text, x, y, 0x000000, false);
        }
    }
}
