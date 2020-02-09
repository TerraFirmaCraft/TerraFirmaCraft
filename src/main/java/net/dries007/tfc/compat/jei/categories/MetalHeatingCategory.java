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
import net.dries007.tfc.compat.jei.wrappers.MetalHeatingRecipeWrapper;

@ParametersAreNonnullByDefault
public class MetalHeatingCategory extends BaseRecipeCategory<MetalHeatingRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic fire;
    private final IDrawableAnimated fireAnimated;

    public MetalHeatingCategory(IGuiHelper helper, String Uid)
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
        fire.draw(minecraft, 54, 16);
        fireAnimated.draw(minecraft, 54, 16);
        slot.draw(minecraft, 20, 16);
        slot.draw(minecraft, 84, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MetalHeatingRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, false, 20, 16);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        fluidStackGroup.init(0, true, 85, 17, 16, 16, ingredients.getOutputs(VanillaTypes.FLUID).get(0).get(0).amount, false, null);
        fluidStackGroup.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}
