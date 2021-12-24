/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.recipes.RockKnappingRecipe;

public class RockKnappingRecipeCategory extends KnappingRecipeCategory<RockKnappingRecipe>
{
    private final IDrawableStatic blank;
    private final LoadingCache<RockKnappingRecipe, DisplayData> cache;

    public RockKnappingRecipeCategory(ResourceLocation uId, IGuiHelper helper)
    {
        super(uId, helper, new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.CHERT).get(Rock.BlockType.LOOSE).get()), RockKnappingRecipe.class, null, null);
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(25)
            .build(new CacheLoader<>()
            {
                @Override
                public DisplayData load(RockKnappingRecipe key)
                {
                    return new DisplayData(helper);
                }
            });
        this.blank = helper.createBlankDrawable(1, 1);
    }

    @Override
    public void draw(RockKnappingRecipe recipe, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 88, 60);
        super.draw(recipe, stack, mouseX, mouseY);
    }

    @Nullable
    @Override
    public IDrawable getHigh(RockKnappingRecipe recipe)
    {
        DisplayData data = cache.getUnchecked(recipe);
        Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients = data.getCurrentIngredients();
        if (currentIngredients == null) return blank;

        ItemStack displayed = currentIngredients.get(1).getDisplayedIngredient();
        if (displayed == null) return blank;

        ResourceLocation high = KnappingScreen.getButtonLocation(displayed.getItem(), false);
        return data.getHelper().drawableBuilder(high, 0, 0, 16, 16).setTextureSize(16, 16).build();
    }

    @Override
    public void setIngredients(RockKnappingRecipe recipe, IIngredients ingredients)
    {
        super.setIngredients(recipe, ingredients);
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getIngredient().getItems()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RockKnappingRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(1, true, 88, 60);
        itemStacks.set(1, collapse(ingredients.getInputs(VanillaTypes.ITEM)));

        super.setRecipe(recipeLayout, recipe, ingredients);

        DisplayData data = cache.getUnchecked(recipe);
        data.setCurrentIngredients(itemStacks.getGuiIngredients());
    }

    public static class DisplayData
    {
        private final IGuiHelper helper;
        @Nullable
        private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients;

        public DisplayData(IGuiHelper helper)
        {
            this.helper = helper;
            this.currentIngredients = null;
        }

        public IGuiHelper getHelper()
        {
            return helper;
        }

        public void setCurrentIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients)
        {
            this.currentIngredients = currentIngredients;
        }

        @Nullable
        public Map<Integer, ? extends IGuiIngredient<ItemStack>> getCurrentIngredients()
        {
            return currentIngredients;
        }
    }
}
