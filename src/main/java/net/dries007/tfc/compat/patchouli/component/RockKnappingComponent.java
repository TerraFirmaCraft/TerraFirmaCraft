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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.RockKnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

public class RockKnappingComponent extends CustomComponent
{
    @SerializedName("recipes") String recipeVariable;

    private transient String @Nullable [] resolvedRecipeNames;
    private transient Entry @Nullable [] recipes;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        final List<Item> allInputs = Helpers.getAllTagValues(TFCTags.Items.ROCK_KNAPPING, ForgeRegistries.ITEMS);
        final List<Entry> recipes = new ArrayList<>();

        if (resolvedRecipeNames == null) return;

        for (String recipeName : resolvedRecipeNames)
        {
            final RockKnappingRecipe recipe = asRecipe(recipeName, TFCRecipeTypes.ROCK_KNAPPING.get()).orElse(null);
            if (recipe != null)
            {
                for (Item input : allInputs)
                {
                    final ItemStack inputStack = new ItemStack(input);
                    if (recipe.matchesItem(inputStack))
                    {
                        final ResourceLocation texture = KnappingScreen.getButtonLocation(input, false);
                        recipes.add(new Entry(recipe, recipe.getResultItem(), texture));
                    }
                }
            }
        }

        this.recipes = recipes.toArray(new Entry[0]);
    }

    @Override
    public void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipes != null && recipes.length > 0)
        {
            final Entry recipe = recipes[(context.getTicksInBook() / 20) % 20];

            renderSetup(poseStack);
            KnappingRecipeComponent.render(poseStack, context, mouseX, mouseY, recipe.recipe(), recipe.outputStack(), recipe.texture(), null, x, y);
            poseStack.popPose();
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

    record Entry(RockKnappingRecipe recipe, ItemStack outputStack, ResourceLocation texture) {}
}
