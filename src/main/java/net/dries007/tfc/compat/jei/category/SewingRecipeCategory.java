/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.screen.SewingTableScreen;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.SewingRecipe;

public class SewingRecipeCategory extends BaseRecipeCategory<SewingRecipe>
{
    private final IDrawable sewingBackground;
    private final IDrawable stitch;
    private final IDrawable wool;
    private final IDrawable burlap;

    public SewingRecipeCategory(RecipeType<SewingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(144, 70), new ItemStack(TFCItems.BONE_NEEDLE.get()));

        sewingBackground = helper.drawableBuilder(SewingTableScreen.TEXTURE, 8, 14, 112, 64).build();
        stitch = helper.drawableBuilder(SewingTableScreen.TEXTURE, 192, 0, 5, 5).build();
        wool = helper.drawableBuilder(SewingTableScreen.TEXTURE, 208, 0, 12, 12).build();
        burlap = helper.drawableBuilder(SewingTableScreen.TEXTURE, 208, 16, 12, 12).build();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SewingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 25)
            .addItemStack(recipe.getResultItem(registryAccess()))
            .setBackground(slot, -1, -1);
    }

    @Override
    public void draw(SewingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        final int xPadding = 3;
        final int yPadding = 4;
        sewingBackground.draw(graphics, xPadding, yPadding);

        SewingTableScreen.forEachClothSquare((x, y, i) -> {
            final int material = recipe.getSquare(i);
            if (material != -1)
            {
                if (material == SewingTableContainer.BURLAP_ID)
                {
                    burlap.draw(graphics, x * 12 + 8 + xPadding, y * 12 + 8 + yPadding);
                }
                else
                {
                    wool.draw(graphics, x * 12 + 8 + xPadding, y * 12 + 8 + yPadding);
                }
            }
        });

        SewingTableScreen.forEachStitch((x, y, i) -> {
            if (recipe.getStitch(i))
            {
                stitch.draw(graphics, x * 12 + 8 + xPadding - 2, y * 12 + 8 + yPadding - 2);
            }
            return false;
        });

    }
}
