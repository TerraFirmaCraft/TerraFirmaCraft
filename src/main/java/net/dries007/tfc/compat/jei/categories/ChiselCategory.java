/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.ChiselRecipeWrapper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChiselCategory extends BaseRecipeCategory<ChiselRecipeWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");

    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final IDrawableAnimated arrowAnimated;

    public ChiselCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(90, 38), Uid);
        arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        arrow.draw(minecraft, 35, 16);
        arrowAnimated.draw(minecraft, 35, 16);
        slot.draw(minecraft, 10, 16);
        slot.draw(minecraft, 64, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ChiselRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 10, 16);
        itemStackGroup.init(1, false, 64, 16);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
