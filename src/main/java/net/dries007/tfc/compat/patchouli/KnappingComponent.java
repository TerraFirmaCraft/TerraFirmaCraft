/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.client.TFCGuiHandler;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

@SuppressWarnings("unused")
public class KnappingComponent implements ICustomComponent
{
    private final KnappingRecipe recipe = TFCRegistries.KNAPPING.getValuesCollection().stream().filter(r -> r.getType() == KnappingType.LEATHER).findFirst().orElse(null);
    private final ResourceLocation squareLow = null;
    private final ResourceLocation squareHigh = TFCGuiHandler.LEATHER_TEXTURE;
    private int posX, posY;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        this.posX = componentX;
        this.posY = componentY;
    }

    @Override
    public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 0);
        GlStateManager.color(1f, 1f, 1f, 1f);

        for (int y = 0; y < recipe.getMatrix().getHeight(); y++)
        {
            for (int x = 0; x < recipe.getMatrix().getWidth(); x++)
            {
                if (recipe.getMatrix().get(x, y) && squareHigh != null)
                {
                    context.getGui().mc.getTextureManager().bindTexture(squareHigh);
                    Gui.drawModalRectWithCustomSizedTexture(1 + x * 16, 1 + y * 16, 0, 0, 16, 16, 16, 16);
                }
                else if (squareLow != null)
                {
                    context.getGui().mc.getTextureManager().bindTexture(squareLow);
                    Gui.drawModalRectWithCustomSizedTexture(1 + x * 16, 1 + y * 16, 0, 0, 16, 16, 16, 16);
                }
            }
        }

        context.renderItemStack(80, 31, mouseX, mouseY, recipe.getOutput(ItemStack.EMPTY));

        GlStateManager.popMatrix();
    }
}
