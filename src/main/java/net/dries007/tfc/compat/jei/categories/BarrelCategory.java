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
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.BarrelWrapper;

@ParametersAreNonnullByDefault
public class BarrelCategory extends BaseRecipeCategory<BarrelWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/jei/icons.png");
    private static final ResourceLocation BARREL_TEXTURES = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/barrel.png");

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
    public void setRecipe(IRecipeLayout recipeLayout, BarrelWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 25, 22);
        itemStackGroup.init(1, false, 79, 22);

        if (ingredients.getInputs(VanillaTypes.ITEM).size() > 0)
        {
            itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }
        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 0)
        {
            itemStackGroup.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }

        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        fluidStackGroup.init(0, true, 6, 6, 8, 50, 10000, true, null);
        fluidStackGroup.init(1, false, 108, 6, 8, 50, 10000, true, null);

        if (ingredients.getInputs(VanillaTypes.FLUID).size() > 0)
        {
            fluidStackGroup.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        }
        if (ingredients.getOutputs(VanillaTypes.FLUID).size() > 0)
        {
            fluidStackGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        }
    }
}
