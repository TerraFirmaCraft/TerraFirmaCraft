/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;

@SuppressWarnings("unused")
public abstract class KnappingComponent extends CustomComponent
{
    @VariableHolder
    @SerializedName("recipe")
    public String recipeName;

    private transient KnappingRecipe recipe;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        this.posX = componentX;
        this.posY = componentY;
        Objects.requireNonNull(recipeName, "Missing recipe name?");
        this.recipe = TFCRegistries.KNAPPING.getValue(new ResourceLocation(recipeName));
        Objects.requireNonNull(recipe, "Unknown knapping recipe: " + recipeName);
    }

    @Override
    public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(posX, posY, 0);
        GlStateManager.color(1f, 1f, 1f, 1f);

        context.getGui().mc.getTextureManager().bindTexture(TFCPatchouliPlugin.BOOK_UTIL_TEXTURES);
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, 0, 0, 116, 90, 256, 256);

        int ticks = context.getGui() instanceof GuiBook ? ((GuiBook) context.getGui()).ticksInBook : 0;

        ResourceLocation squareHigh = getSquareHigh(ticks);
        ResourceLocation squareLow = getSquareLow(ticks);

        for (int y = 0; y < recipe.getMatrix().getHeight(); y++)
        {
            for (int x = 0; x < recipe.getMatrix().getWidth(); x++)
            {
                if (recipe.getMatrix().get(x, y) && squareHigh != null)
                {
                    context.getGui().mc.getTextureManager().bindTexture(squareHigh);
                    Gui.drawModalRectWithCustomSizedTexture(5 + x * 16, 5 + y * 16, 0, 0, 16, 16, 16, 16);
                }
                else if (squareLow != null)
                {
                    context.getGui().mc.getTextureManager().bindTexture(squareLow);
                    Gui.drawModalRectWithCustomSizedTexture(5 + x * 16, 5 + y * 16, 0, 0, 16, 16, 16, 16);
                }
            }
        }
        context.renderItemStack(95, 37, mouseX, mouseY, recipe.getOutput(getInputItem(ticks)));
        GlStateManager.popMatrix();
    }

    @Nullable
    protected abstract ResourceLocation getSquareLow(int ticks);

    @Nullable
    protected abstract ResourceLocation getSquareHigh(int ticks);

    @Nonnull
    protected ItemStack getInputItem(int ticks)
    {
        return ItemStack.EMPTY;
    }
}
