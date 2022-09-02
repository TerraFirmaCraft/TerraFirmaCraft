/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import vazkii.patchouli.api.IComponentRenderContext;

public abstract class BarrelComponent<T extends BarrelRecipe> extends RecipeComponent<T>
{
    protected transient List<ItemStack> inputItems = Collections.emptyList();
    protected transient List<FluidStack> inputFluids = Collections.emptyList();

    @Override
    public void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        renderSetup(poseStack);

        GuiComponent.blit(poseStack, 9, 0, 0, 116, 98, 26, 256, 256);

        renderItemStacks(context, poseStack, 14, 5, mouseX, mouseY, inputItems);
        renderFluidStacks(context, poseStack, 14 + 28, 5, mouseX, mouseY, inputFluids);

        // This only works for barrel recipes with a single output, and no complex item stack providers
        // If we need more, we can fix it, but for now this should be good enough
        if (!recipe.getResultItem().isEmpty())
        {
            context.renderItemStack(poseStack, 86, 5, mouseX, mouseY, recipe.getResultItem());
        }
        else if (!recipe.getOutputFluid().isEmpty())
        {
            renderFluidStack(context, poseStack, 86, 5, mouseX, mouseY, recipe.getOutputFluid());
        }

        renderAdditional(poseStack, context, partialTicks, mouseX, mouseY);

        poseStack.popPose();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        if (recipe == null) return;

        inputItems = unpackItemStackIngredient(recipe.getInputItem());
        inputFluids = unpackFluidStackIngredient(recipe.getInputFluid());
    }

    protected void renderAdditional(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {}
}
