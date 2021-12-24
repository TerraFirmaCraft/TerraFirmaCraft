/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.Helpers;


public class KnappingRecipeCategory<T extends KnappingRecipe> extends BaseRecipeCategory<T>
{
    private static final ResourceLocation KNAP_TEXTURES = Helpers.identifier("textures/gui/knapping.png");

    private final IDrawableStatic arrow;
    @Nullable
    private final IDrawable high, low;

    public KnappingRecipeCategory(ResourceLocation uId, IGuiHelper helper, ItemStack icon, Class<? extends T> recipeClass, @Nullable ResourceLocation high, @Nullable ResourceLocation low)
    {
        super(uId, helper, helper.createBlankDrawable(135, 82), icon, recipeClass);
        arrow = helper.createDrawable(KNAP_TEXTURES, 97, 44, 22, 15);
        this.high = high == null ? null : helper.drawableBuilder(high, 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.low = low == null ? null : helper.drawableBuilder(low, 0, 0, 16, 16).setTextureSize(16, 16).build();
    }

    @Override
    public void draw(T recipe, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 116, 32);
        arrow.draw(stack, 86, 33);
        IDrawable high = getHigh(recipe);
        IDrawable low = getLow(recipe);

        for (int y = 0; y < recipe.getPattern().getHeight(); y++)
        {
            for (int x = 0; x < recipe.getPattern().getWidth(); x++)
            {
                if (recipe.getPattern().get(x, y) && high != null)
                {
                    high.draw(stack, 1 + x * 16, 1 + y * 16);
                }
                else if (low != null)
                {
                    low.draw(stack, 1 + x * 16, 1 + y * 16);
                }
            }
        }
    }

    @Nullable
    public IDrawable getHigh(T recipe)
    {
        return high;
    }

    @Nullable
    public IDrawable getLow(T recipe)
    {
        return low;
    }

    @Override
    public void setIngredients(T recipe, IIngredients ingredients)
    {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 116, 32);
        itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
