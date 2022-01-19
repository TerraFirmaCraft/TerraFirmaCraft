/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.ScrapingWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
public class ScrapingCategory extends BaseRecipeCategory<ScrapingWrapper>
{
    private static final ResourceLocation ICONS = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/icons/jei.png");
    private IDrawableStatic arrow;
    private IDrawableAnimated arrowAnimated;

    public ScrapingCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createDrawable(new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/jei_leatherworking.png"), 0, 0, 154, 180), Uid);
        arrow = helper.createDrawable(ICONS, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);

    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        arrow.draw(minecraft, 66, 32);
        arrowAnimated.draw(minecraft, 66, 32);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ScrapingWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 45, 31);
        itemStackGroup.init(1, true, 68, 13);
        itemStackGroup.init(2, false, 92, 31);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        itemStackGroup.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
