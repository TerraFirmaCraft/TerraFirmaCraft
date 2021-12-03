package net.dries007.tfc.compat.jei.category;

import java.util.stream.Collectors;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.PotRecipe;

public class PotRecipeCategory<T extends PotRecipe> extends BaseRecipeCategory<T>
{
    public PotRecipeCategory(ResourceLocation uId, IGuiHelper helper, IDrawable background, Class<? extends T> recipeClass)
    {
        super(uId, helper, background, new ItemStack(TFCItems.POT.get()), recipeClass);
    }

    @Override
    public void setIngredients(T recipe, IIngredients ingredients)
    {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setInputs(VanillaTypes.FLUID, recipe.getFluidIngredient().getMatchingFluids().stream().map(fluid -> new FluidStack(fluid, 1000)).collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, true, 5, 25);
        itemStacks.init(1, true, 25, 25);
        itemStacks.init(2, true, 45, 25);
        itemStacks.init(3, true, 65, 25);
        itemStacks.init(4, true, 85, 25);
        fluidStacks.init(5, true, 45, 45);

        NonNullList<Ingredient> list = recipe.getIngredients();
        itemStacks.set(0, list.get(0).getItems());
    }
}
