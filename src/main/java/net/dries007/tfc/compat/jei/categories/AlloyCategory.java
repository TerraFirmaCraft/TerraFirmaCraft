/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackRenderer;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.AlloyRecipeWrapper;

@ParametersAreNonnullByDefault
public class AlloyCategory extends BaseRecipeCategory<AlloyRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic fire;
    private final IDrawableAnimated fireAnimated;

    public AlloyCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(156, 64), Uid);
        fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        slot.draw(minecraft, 0, 12); //1st ingot
        slot.draw(minecraft, 60, 12); //2nd ingot
        slot.draw(minecraft, 0, 38); //3rd ingot
        slot.draw(minecraft, 60, 38); //4th ingot
        fire.draw(minecraft, 118, 25);
        fireAnimated.draw(minecraft, 118, 25);
        slot.draw(minecraft, 138, 25); //output
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlloyRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        FluidStackRenderer renderer = new FluidStackRenderer(); // Defaults to hide fluid amount
        fluidStackGroup.init(0, false, renderer, 1, 13, 16, 16, 0, 0);
        fluidStackGroup.init(1, false, renderer, 61, 13, 16, 16, 0, 0);
        fluidStackGroup.init(2, false, renderer, 1, 39, 16, 16, 0, 0);
        fluidStackGroup.init(3, false, renderer, 61, 39, 16, 16, 0, 0);
        fluidStackGroup.init(4, true, renderer, 139, 26, 16, 16, 0, 0);

        for (int i = 0; i < ingredients.getInputs(VanillaTypes.FLUID).size(); i++)
        {
            fluidStackGroup.set(i, ingredients.getInputs(VanillaTypes.FLUID).get(i));
        }
        fluidStackGroup.set(4, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}
