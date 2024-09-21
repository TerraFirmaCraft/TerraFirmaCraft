/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import vazkii.patchouli.api.IComponentRenderContext;

import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

public abstract class BarrelComponent<T extends BarrelRecipe> extends RecipeComponent<T>
{
    protected transient List<ItemStack> inputItems = Collections.emptyList();
    protected transient List<FluidStack> inputFluids = Collections.emptyList();

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        renderSetup(graphics);

        graphics.blit(PatchouliIntegration.TEXTURE, 9, 0, 0, 116, 98, 26, 256, 256);

        renderItemStacks(context, graphics, 14, 5, mouseX, mouseY, inputItems);
        renderFluidStacks(context, graphics, 14 + 28, 5, mouseX, mouseY, inputFluids);

        // This only works for barrel recipes with a single output, and no complex item unsealedStack providers
        // If we need more, we can fix it, but for now this should be good enough
        if (!recipe.getResultItem(null).isEmpty())
        {
            context.renderItemStack(graphics, 86, 5, mouseX, mouseY, recipe.getResultItem(null));
        }
        else if (!recipe.getOutputFluid().isEmpty())
        {
            renderFluidStack(context, graphics, 86, 5, mouseX, mouseY, recipe.getOutputFluid());
        }

        renderAdditional(graphics, context, partialTicks, mouseX, mouseY);

        graphics.pose().popPose();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        if (recipe == null) return;

        inputItems = unpackItemStackIngredient(recipe.getInputItem());
        inputFluids = unpackFluidStackIngredient(recipe.getInputFluid());
    }

    protected void renderAdditional(GuiGraphics poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {}
}
