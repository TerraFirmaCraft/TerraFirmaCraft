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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.util.Helpers;

/**
 * Only supports up to 6 operations... because JEI panels aren't resizable as far as I can tell.
 */
public class GlassworkingRecipeCategory extends BaseRecipeCategory<GlassworkingRecipe>
{
    private final Map<GlassOperation, ItemStack> map = ImmutableMap.of(
        GlassOperation.SAW, TFCItems.GEM_SAW.get().getDefaultInstance(),
        GlassOperation.ROLL, TFCItems.WOOL_CLOTH.get().getDefaultInstance(),
        GlassOperation.STRETCH, TFCItems.BLOWPIPE_WITH_GLASS.get().getDefaultInstance(),
        GlassOperation.BLOW, TFCItems.BLOWPIPE_WITH_GLASS.get().getDefaultInstance(),
        GlassOperation.TABLE_POUR, TFCItems.BLOWPIPE_WITH_GLASS.get().getDefaultInstance(),
        GlassOperation.BASIN_POUR, TFCItems.BLOWPIPE_WITH_GLASS.get().getDefaultInstance(),
        GlassOperation.FLATTEN, TFCItems.PADDLE.get().getDefaultInstance(),
        GlassOperation.PINCH, TFCItems.JACKS.get().getDefaultInstance()
    );

    public GlassworkingRecipeCategory(RecipeType<GlassworkingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(175, 110), new ItemStack(TFCItems.BLOWPIPE_WITH_GLASS.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GlassworkingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addIngredients(recipe.batchItem())
            .setBackground(slot, -1, -1);

        ItemStack result = recipe.getResultItem(null);
        if (result.getItem() instanceof BlockItem bi && bi.getBlock() instanceof PouredGlassBlock block)
        {
            result = block.getDrop().getDefaultInstance();
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 5)
            .addItemStack(result)
            .setBackground(slot, -1, -1);

        int idx = 0;
        for (GlassOperation operation : recipe.operations())
        {
            var slot = builder.addSlot(RecipeIngredientRole.CATALYST, idx < 3 ? 6 : 90, 25 * ((idx % 3) + 1))
                .setBackground(this.slot, -1, -1);
            if (map.containsKey(operation))
            {
                slot.addItemStack(map.get(operation));
                if (map.get(operation).getItem() == TFCItems.BLOWPIPE_WITH_GLASS.get())
                {
                    slot.addItemStack(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.get().getDefaultInstance());
                }
            }
            else if (operation != GlassOperation.TABLE_POUR && operation != GlassOperation.BASIN_POUR)
            {
                for (Map.Entry<Item, GlassOperation> entry : GlassOperation.POWDERS.get().entrySet())
                {
                    if (entry.getValue() == operation)
                    {
                        slot.addItemStack(entry.getKey().getDefaultInstance());
                    }
                }
            }

            idx += 1;
        }
    }

    @Override
    public void draw(GlassworkingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        arrow.draw(graphics, 92, 5);
        arrowAnimated.draw(graphics, 92, 5);

        final Font font = Minecraft.getInstance().font;
        int idx = 0;
        for (GlassOperation operation : recipe.operations())
        {
            final Component text = Component.literal((idx + 1) + ". ").append(Helpers.translateEnum(operation));
            if (idx + 3 < recipe.operations().size())
            {
                graphics.drawWordWrap(font, text, (idx < 3 ? 6 : 90) + 20, 25 * ((idx % 3) + 1) + 5, idx < 3 ? 55 : 75, Color.BLACK.getRGB());
            }
            else
            {
                graphics.drawString(font, text, (idx < 3 ? 6 : 90) + 20, 25 * ((idx % 3) + 1) + 5, Color.BLACK.getRGB(), false);
            }
            idx += 1;
        }
    }
}
