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
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.BlastFurnaceRecipeWrapper;

@ParametersAreNonnullByDefault
public class BlastFurnaceCategory extends BaseRecipeCategory<BlastFurnaceRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic fire;
    private final IDrawableAnimated fireAnimated;

    public BlastFurnaceCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(120, 38), Uid);
        fire = helper.createDrawable(ICONS, 0, 0, 14, 14);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        fire.draw(minecraft, 64, 16);
        fireAnimated.draw(minecraft, 64, 16);
        slot.draw(minecraft, 10, 16);
        slot.draw(minecraft, 30, 16);
        slot.draw(minecraft, 94, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BlastFurnaceRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 10, 16);
        itemStackGroup.init(1, true, 30, 16);
        itemStackGroup.init(2, false, 94, 16);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        itemStackGroup.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
