/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;
import net.dries007.tfc.objects.items.metal.ItemMetal;

@ParametersAreNonnullByDefault
public class WeldingCategory extends BaseRecipeCategory<SimpleRecipeWrapper>
{
    private static final ResourceLocation ANVIL_TEXTURES = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/anvil.png");

    public WeldingCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createDrawable(ANVIL_TEXTURES, 11, 7, 154, 80), Uid);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SimpleRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 1, 60);
        itemStackGroup.init(1, true, 19, 60);
        itemStackGroup.init(2, true, 117, 60);
        itemStackGroup.init(3, true, 135, 60);
        itemStackGroup.init(4, false, 19, 42);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        itemStackGroup.set(2, new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, Metal.ItemType.HAMMER)));
        itemStackGroup.set(3, OreDictionary.getOres("dustFlux").get(0));
        itemStackGroup.set(4, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
