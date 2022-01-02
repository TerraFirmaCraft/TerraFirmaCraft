/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;

public class HeatingCategory extends BaseRecipeCategory<HeatingRecipe>
{
    public HeatingCategory(ResourceLocation uId, IGuiHelper helper)
    {
        super(uId, helper, helper.createBlankDrawable(120, 38), new ItemStack(TFCBlocks.FIREPIT.get()), HeatingRecipe.class);
    }

    @Override
    public void setIngredients(HeatingRecipe recipe, IIngredients ingredients)
    {
        ingredients.setInputIngredients(List.of(recipe.getIngredient()));
        if (!recipe.getResultItem().isEmpty())
        {
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
        }
        if (!recipe.getDisplayOutputFluid().isEmpty())
        {
            ingredients.setOutput(VanillaTypes.FLUID, recipe.getDisplayOutputFluid());
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HeatingRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        boolean item = !recipe.getResultItem().isEmpty();
        boolean fluid = !recipe.getDisplayOutputFluid().isEmpty();

        itemStacks.init(0, true, 20, 16);
        itemStacks.init(1, false, 84, 16);
        if (fluid) // we have to wrap everything like this because JEI would rather crash than draw empty item stacks
        {
            fluidStacks.init(2, false, item ? 105 : 85, 17);
        }

        itemStacks.set(0, collapse(ingredients.getInputs(VanillaTypes.ITEM)));
        if (item)
        {
            itemStacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }
        if (fluid)
        {
            fluidStacks.set(2, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        }

        itemStacks.set(ingredients);
    }

    @Override
    public void draw(HeatingRecipe recipe, PoseStack stack, double mouseX, double mouseY)
    {
        fire.draw(stack, 54, 16);
        fireAnimated.draw(stack, 54, 16);
        slot.draw(stack, 20, 16);
        slot.draw(stack, 84, 16);

        if (!recipe.getDisplayOutputFluid().isEmpty() && !recipe.getResultItem().isEmpty())
        {
            slot.draw(stack, 104, 16);
        }

        MutableComponent color = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
        if (color != null)
        {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            font.draw(stack, color, 60f - font.width(color) / 2.0f, 4f, 0xFFFFFF);
        }
    }
}
