/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;

/**
 * This is a merged knapping component that supports multiple 'similar' recipes
 */
public class RockKnappingComponent extends CustomComponent
{
    @SerializedName("recipes") String recipeVariable;

    private transient String @Nullable [] resolvedRecipeNames;
    private transient Entry @Nullable [] recipes;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        final List<Item> allInputs = Helpers.allItems(TFCTags.Items.ROCK_KNAPPING).toList();
        final List<Entry> recipes = new ArrayList<>();

        if (resolvedRecipeNames == null) return;

        for (String recipeName : resolvedRecipeNames)
        {
            final KnappingRecipe recipe = asRecipe(recipeName, TFCRecipeTypes.KNAPPING.get()).orElse(null);
            if (recipe != null)
            {
                for (Item input : allInputs)
                {
                    final ItemStack inputStack = new ItemStack(input);
                    if (recipe.matchesItem(inputStack))
                    {
                        final ResourceLocation texture = KnappingScreen.getButtonLocation(input, false);
                        recipes.add(new Entry(recipe, recipe.getResultItem(null), texture));
                    }
                }
            }
        }

        this.recipes = recipes.toArray(new Entry[0]);
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipes != null && recipes.length > 0)
        {
            final Entry recipe = recipes[(context.getTicksInBook() / 20) % recipes.length];

            renderSetup(graphics);
            KnappingComponent.render(graphics, context, mouseX, mouseY, recipe.recipe(), recipe.outputStack(), recipe.texture(), null, x, y);
            graphics.pose().popPose();
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup)
    {
        resolvedRecipeNames = lookup.apply(IVariable.wrap(recipeVariable))
            .asStream()
            .map(IVariable::asString)
            .toArray(String[]::new);
    }

    record Entry(KnappingRecipe recipe, ItemStack outputStack, ResourceLocation texture) {}
}
