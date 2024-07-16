/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.Arrays;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.util.data.KnappingPattern;

public class KnappingComponent extends RecipeComponent<KnappingRecipe>
{
    public static void render(GuiGraphics graphics, IComponentRenderContext context, int mouseX, int mouseY, KnappingRecipe recipe, ItemStack resultStack, @Nullable ResourceLocation highTexture, @Nullable ResourceLocation lowTexture, int x0, int y0)
    {
        graphics.blit(PatchouliIntegration.TEXTURE, x0, y0, 0, 0, 116, 90, 256, 256);

        final KnappingPattern pattern = recipe.getPattern();

        // If the pattern is < 5 wide in any direction, we offset it so it appears centered, rounding down
        final int offsetY = (KnappingPattern.MAX_HEIGHT - pattern.getHeight()) / 2;
        final int offsetX = (KnappingPattern.MAX_WIDTH - pattern.getWidth()) / 2;

        for (int y = 0; y < KnappingPattern.MAX_HEIGHT; y++)
        {
            for (int x = 0; x < KnappingPattern.MAX_WIDTH; x++)
            {
                if (0 <= y - offsetY && y - offsetY < pattern.getHeight() && 0 <= x - offsetX && x - offsetX < pattern.getWidth())
                {
                    // (x, y) is in-bounds, so draw based off the pattern
                    drawSquare(graphics, pattern.get(x - offsetX, y - offsetY) ? highTexture : lowTexture, x0, y0, x, y);
                }
                else
                {
                    // (x, y) is out-of-bounds, so draw the 'default' square
                    drawSquare(graphics, pattern.isOutsideSlotRequired() ? highTexture : lowTexture, x0, y0, x, y);
                }
            }
        }

        context.renderItemStack(graphics, 95, 37, mouseX, mouseY, resultStack);
    }

    private static void drawSquare(GuiGraphics graphics, @Nullable ResourceLocation texture, int x0, int y0, int x, int y)
    {
        if (texture != null)
        {
            RenderSystem.setShaderTexture(0, texture);
            graphics.blit(texture, x0 + 5 + x * 16, y0 + 5 + y * 16, 0, 0, 16, 16, 16, 16);
        }
    }

    protected transient ItemStack @Nullable [] inputs;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        if (recipe != null)
        {
            inputs = Arrays.stream(recipe.getKnappingType().inputItem().ingredient().getItems())
                .filter(stack -> recipe.matchesItem(stack))
                .toArray(ItemStack[]::new);
        }
    }

    @Override
    protected RecipeType<KnappingRecipe> getRecipeType()
    {
        return TFCRecipeTypes.KNAPPING.get();
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null || inputs == null) return;

        final ItemStack input = inputs[(context.getTicksInBook() / 20) % inputs.length];
        final ResourceLocation highTexture = KnappingScreen.getHighTexture(input);
        final ResourceLocation lowTexture = KnappingScreen.getLowTexture(recipe.getKnappingType(), input);
        final ItemStack resultStack = recipe.getResultItem(null);

        renderSetup(graphics);
        render(graphics, context, mouseX, mouseY, recipe, resultStack, highTexture, lowTexture, x, y);
        graphics.pose().popPose();
    }
}
