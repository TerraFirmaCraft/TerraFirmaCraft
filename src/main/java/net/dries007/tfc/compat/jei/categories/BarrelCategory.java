/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.BarrelRecipeWrapper;

@ParametersAreNonnullByDefault
public class BarrelCategory extends BaseRecipeCategory<BarrelRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");
    private static final ResourceLocation BARREL_TEXTURES = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/barrel.png");

    private final IDrawableStatic fluidSlotBackgroound, fluidSlot;
    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final IDrawableAnimated arrowAnimated;

    public BarrelCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(122, 62), Uid);
        fluidSlotBackgroound = helper.createDrawable(BARREL_TEXTURES, 7, 15, 18, 60);
        fluidSlot = helper.createDrawable(BARREL_TEXTURES, 176, 0, 18, 53);
        arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        //Input
        fluidSlotBackgroound.draw(minecraft, 1, 1);
        fluidSlot.draw(minecraft, 1, 5);
        slot.draw(minecraft, 25, 22);

        arrow.draw(minecraft, 50, 22);
        arrowAnimated.draw(minecraft, 50, 22);

        //Output
        slot.draw(minecraft, 79, 22);
        fluidSlotBackgroound.draw(minecraft, 103, 1);
        fluidSlot.draw(minecraft, 103, 5);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BarrelRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        int fluidSlot = 0;
        int itemSlot = 0;

        // Shows input fluid (slot + fluidstack) only if the recipe has one
        if (ingredients.getInputs(VanillaTypes.FLUID).size() > 0)
        {
            List<FluidStack> inputFluid = ingredients.getInputs(VanillaTypes.FLUID).get(0);
            fluidStackGroup.init(fluidSlot, true, 6, 6, 8, 50, inputFluid.get(0).amount, false, null);
            fluidStackGroup.set(fluidSlot, inputFluid);
            fluidSlot++;
        }

        if (recipeWrapper.isFluidMixing())
        {
            // If this is a fluid mixing recipe, fill the input slot with the "bucket" fluid
            List<FluidStack> inputFluid = ingredients.getInputs(VanillaTypes.FLUID).get(1);
            fluidStackGroup.init(fluidSlot, true, 26, 23, 16, 16, inputFluid.get(0).amount, false, null);
            fluidStackGroup.set(fluidSlot, inputFluid);
            fluidSlot++;
        }
        else
        {
            // Draws the input slot and stack othewise
            itemStackGroup.init(itemSlot, true, 25, 22);
            if (ingredients.getInputs(VanillaTypes.ITEM).size() > 0)
            {
                itemStackGroup.set(itemSlot, ingredients.getInputs(VanillaTypes.ITEM).get(0));
            }
            itemSlot++;
        }

        // Shows output fluid (slot + fluidstack) only if the recipe has one
        if (ingredients.getOutputs(VanillaTypes.FLUID).size() > 0)
        {
            List<FluidStack> outputFluid = ingredients.getOutputs(VanillaTypes.FLUID).get(0);
            fluidStackGroup.init(fluidSlot, false, 108, 6, 8, 50, outputFluid.get(0).amount, false, null);
            fluidStackGroup.set(fluidSlot, outputFluid);
        }

        // Draws the output slot and stack
        itemStackGroup.init(itemSlot, false, 79, 22);
        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 0)
        {
            itemStackGroup.set(itemSlot, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }
    }
}
