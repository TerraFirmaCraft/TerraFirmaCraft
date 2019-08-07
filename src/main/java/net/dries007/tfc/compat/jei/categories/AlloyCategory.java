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
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.AlloyWrapper;

@ParametersAreNonnullByDefault
public class AlloyCategory extends BaseRecipeCategory<AlloyWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/jei/icons.png");

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
    public void setRecipe(IRecipeLayout recipeLayout, AlloyWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 0, 12);
        itemStackGroup.init(1, true, 60, 12);
        itemStackGroup.init(2, true, 0, 38);
        itemStackGroup.init(3, true, 60, 38);
        itemStackGroup.init(4, false, 138, 25);

        for (int i = 0; i < ingredients.getInputs(VanillaTypes.ITEM).size(); i++)
        {
            itemStackGroup.set(i, ingredients.getInputs(VanillaTypes.ITEM).get(i));
        }
        itemStackGroup.set(4, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
