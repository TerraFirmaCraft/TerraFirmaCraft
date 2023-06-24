/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;

public abstract class KnappingRecipeComponent<T extends KnappingRecipe> extends RecipeComponent<T>
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

    @Override
    public void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        final int bookTicks = context.getTicksInBook();
        final ResourceLocation highTexture = getHighTexture(bookTicks);
        final ResourceLocation lowTexture = getLowTexture(bookTicks);
        final ItemStack resultStack = recipe.getResultItem();

        renderSetup(poseStack);
        render(poseStack, context, mouseX, mouseY, recipe, resultStack, highTexture, lowTexture, x, y);
        poseStack.popPose();
    }

    @Nullable
    protected abstract ResourceLocation getHighTexture(int ticks);

    @Nullable
    protected abstract ResourceLocation getLowTexture(int ticks);
}
