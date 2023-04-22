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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;

public class KnappingRecipeComponent extends CustomComponent
{
    public static void render(PoseStack poseStack, IComponentRenderContext context, int mouseX, int mouseY, KnappingRecipe recipe, ItemStack resultStack, @Nullable ResourceLocation highTexture, @Nullable ResourceLocation lowTexture, int x0, int y0)
    {
        GuiComponent.blit(poseStack, x0, y0, 0, 0, 116, 90, 256, 256);

        for (int y = 0; y < recipe.getPattern().getHeight(); y++)
        {
            for (int x = 0; x < recipe.getPattern().getWidth(); x++)
            {
                if (recipe.getPattern().get(x, y) && highTexture != null)
                {
                    RenderSystem.setShaderTexture(0, highTexture);
                    GuiComponent.blit(poseStack, x0 + 5 + x * 16, y0 + 5 + y * 16, 0, 0, 16, 16, 16, 16);
                }
                else if (lowTexture != null)
                {
                    RenderSystem.setShaderTexture(0, lowTexture);
                    GuiComponent.blit(poseStack, x0 + 5 + x * 16, y0 + 5 + y * 16, 0, 0, 16, 16, 16, 16);
                }
            }
        }

        context.renderItemStack(poseStack, 95, 37, mouseX, mouseY, resultStack);
    }

    @SerializedName("recipes") String recipeVariable;

    private transient String @Nullable [] resolvedRecipeNames;
    private transient Entry @Nullable [] recipes;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        final List<Item> allInputs = Helpers.getAllTagValues(TFCTags.Items.ANY_KNAPPING, ForgeRegistries.ITEMS);
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
                        final ResourceLocation highTexture = KnappingScreen.getButtonLocation(input, false);
                        final ResourceLocation lowTexture = recipe.getKnappingType().usesDisabledTexture() ? KnappingScreen.getButtonLocation(input, true) : null;
                        recipes.add(new Entry(recipe, recipe.getResultItem(), highTexture, lowTexture));
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
            render(poseStack, context, mouseX, mouseY, recipe.recipe(), recipe.outputStack(), recipe.highTexture(), null, x, y);
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

    record Entry(KnappingRecipe recipe, ItemStack outputStack, ResourceLocation highTexture, @Nullable ResourceLocation lowTexture) {}
}
