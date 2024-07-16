/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.IComponentRenderContext;

import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;

public class GlassworkingComponent extends InputOutputComponent<GlassworkingRecipe>
{
    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        super.render(graphics, context, partialTicks, mouseX, mouseY);

        if (recipe == null) return;

        final Font font = Minecraft.getInstance().font;
        int idx = 0;
        for (GlassOperation operation : recipe.operations())
        {
            final Component text = Component.literal((idx + 1) + ". ").append(Helpers.translateEnum(operation)).withStyle(Style.EMPTY.withFont(Minecraft.UNIFORM_FONT));
            graphics.drawString(font, text, 14, 30 + (idx * 9), 0x404040, false);
            idx++;
        }
    }

    @Override
    protected RecipeType<GlassworkingRecipe> getRecipeType()
    {
        return TFCRecipeTypes.GLASSWORKING.get();
    }

    @Override
    public Ingredient getIngredient(GlassworkingRecipe recipe)
    {
        return recipe.batchItem();
    }

    @Override
    public ItemStack getOutput(GlassworkingRecipe recipe)
    {
        return recipe.getResultItem(null);
    }
}
