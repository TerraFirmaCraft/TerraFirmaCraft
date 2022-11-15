/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.config.TFCConfig;

public class HeatingCategory extends BaseRecipeCategory<HeatingRecipe>
{
    public HeatingCategory(RecipeType<HeatingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), new ItemStack(TFCBlocks.FIREPIT.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatingRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 21, 17);
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 17);

        inputSlot.addIngredients(recipe.getIngredient());
        inputSlot.setBackground(slot, -1,-1);

        final List<ItemStack> outputItems = Arrays.stream(recipe.getIngredient().getItems())
            .map(stack -> recipe.assemble(new ItemStackInventory(stack)))
            .toList();
        final FluidStack resultFluid = recipe.getDisplayOutputFluid();

        if (!outputItems.isEmpty() && !outputItems.stream().allMatch(ItemStack::isEmpty))
        {
            outputSlot.addItemStacks(outputItems);
        }

        if (!resultFluid.isEmpty())
        {
            outputSlot.addIngredient(JEIIntegration.FLUID_STACK, resultFluid);
            outputSlot.setFluidRenderer(1, false, 16, 16);
        }
        outputSlot.setBackground(slot, -1,-1);
    }

    @Override
    public void draw(HeatingRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        fire.draw(stack, 54, 16);
        fireAnimated.draw(stack, 54, 16);

        MutableComponent color = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
        if (color != null)
        {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            font.draw(stack, color, 60f - font.width(color) / 2.0f, 4f, 0xFFFFFF);
        }
    }
}
