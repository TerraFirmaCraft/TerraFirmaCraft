/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.CastingRecipeWrapper;

@ParametersAreNonnullByDefault
public class CastingCategory extends BaseRecipeCategory<CastingRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final IDrawableAnimated arrowAnimated;

    public CastingCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(120, 38), Uid);
        arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        arrow.draw(minecraft, 48, 16);
        arrowAnimated.draw(minecraft, 48, 16);
        slot.draw(minecraft, 20, 16);
        slot.draw(minecraft, 84, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CastingRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        fluidStackGroup.init(0, false, 21, 17, 16, 16, ingredients.getInputs(VanillaTypes.FLUID).get(0).get(0).amount, false, null);
        fluidStackGroup.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));


        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 84, 16);
        itemStackGroup.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));


    }
}
