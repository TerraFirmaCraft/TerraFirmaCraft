/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.client.gui.GuiAnvilTFC;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;

@ParametersAreNonnullByDefault
public class AnvilCategory extends BaseRecipeCategory<SimpleRecipeWrapper>
{

    public AnvilCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createDrawable(GuiAnvilTFC.ANVIL_BACKGROUND, 11, 7, 154, 80), Uid);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SimpleRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 19, 60);
        itemStackGroup.init(1, true, 117, 60);
        itemStackGroup.init(2, false, 19, 42);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        itemStackGroup.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
