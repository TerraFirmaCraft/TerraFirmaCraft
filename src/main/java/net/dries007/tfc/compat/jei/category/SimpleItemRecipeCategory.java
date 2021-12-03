package net.dries007.tfc.compat.jei.category;

import java.util.Arrays;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.common.recipes.SimpleItemRecipe;

public abstract class SimpleItemRecipeCategory<T extends SimpleItemRecipe> extends BaseRecipeCategory<T>
{
    private final IDrawableStatic arrow;
    private final IDrawableAnimated arrowAnimated;

    public SimpleItemRecipeCategory(ResourceLocation uId, IGuiHelper helper, ItemStack icon, Class<? extends T> recipeClass)
    {
        super(uId, helper, helper.createBlankDrawable(120, 38), icon, recipeClass);
        this.arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void setIngredients(T recipe, IIngredients ingredients)
    {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getIngredient().getItems()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 0, 16);
        itemStacks.init(1, true, 20, 16);
        itemStacks.init(2, false, 84, 16);

        itemStacks.set(0, collapse(ingredients.getInputs(VanillaTypes.ITEM)));
        itemStacks.set(1, Arrays.asList(Ingredient.of(getToolTag()).getItems()));
        itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(T recipe, PoseStack stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 50, 16);
        arrowAnimated.draw(stack, 50, 16);
        slot.draw(stack, 0, 16);
        slot.draw(stack, 20, 16);
        slot.draw(stack, 84, 16);
    }

    protected abstract Tag<Item> getToolTag();
}
