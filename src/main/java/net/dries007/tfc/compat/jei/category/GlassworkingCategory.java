/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.awt.Color;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.util.Helpers;

/**
 * Only supports up to 6 operations... because JEI panels aren't resizable as far as I can tell.
 */
public class GlassworkingCategory extends BaseRecipeCategory<GlassworkingRecipe>
{
    private final Map<GlassOperation, ItemStack> map = ImmutableMap.of(
        GlassOperation.SAW, TFCItems.GEM_SAW.get().getDefaultInstance(),
        GlassOperation.ROLL, TFCItems.WOOL_CLOTH.get().getDefaultInstance(),
        GlassOperation.STRETCH, TFCItems.BLOWPIPE_WITH_GLASS.get().getDefaultInstance(),
        GlassOperation.BLOW, TFCItems.BLOWPIPE_WITH_GLASS.get().getDefaultInstance(),
        GlassOperation.FLATTEN, TFCItems.PADDLE.get().getDefaultInstance(),
        GlassOperation.PINCH, TFCItems.JACKS.get().getDefaultInstance()
    );

    public GlassworkingCategory(RecipeType<GlassworkingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(175, 110), new ItemStack(TFCItems.BLOWPIPE_WITH_GLASS.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GlassworkingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addIngredients(recipe.getBatchItem())
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 5)
            .addItemStack(recipe.getResultItem(null))
            .setBackground(slot, -1, -1);

        int idx = 0;
        for (GlassOperation operation : recipe.getOperations())
        {
            if (map.containsKey(operation))
            {
                var slot = builder.addSlot(RecipeIngredientRole.CATALYST, idx < 3 ? 6 : 80, 25 * ((idx % 3) + 1))
                    .addItemStack(map.get(operation))
                    .setBackground(this.slot, -1, -1);
                if (operation == GlassOperation.BLOW || operation == GlassOperation.STRETCH)
                {
                    slot.addItemStack(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.get().getDefaultInstance());
                }
                idx += 1;
            }
        }
    }

    @Override
    public void draw(GlassworkingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        arrow.draw(graphics, 92, 5);
        arrowAnimated.draw(graphics, 92, 5);
        int idx = 1;
        for (GlassOperation operation : recipe.getOperations())
        {
            graphics.drawString(Minecraft.getInstance().font, Component.literal(idx + ". ").append(Helpers.translateEnum(operation)), idx < 3 ? 6 : 80, 25 * ((idx % 3) + 1), Color.BLACK.getRGB(), false);
            idx += 1;
        }
    }
}
