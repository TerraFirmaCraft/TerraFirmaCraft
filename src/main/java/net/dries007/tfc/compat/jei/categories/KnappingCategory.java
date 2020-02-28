/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.wrappers.KnappingRecipeWrapper;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class KnappingCategory extends BaseRecipeCategory<KnappingRecipeWrapper>
{
    private static final ResourceLocation KNAP_TEXTURES = new ResourceLocation(MOD_ID, "textures/gui/knapping.png");

    private final IDrawableStatic arrow, outputSlot;

    public KnappingCategory(IGuiHelper helper, String Uid)
    {
        super(helper.createBlankDrawable(135, 82), Uid);
        arrow = helper.createDrawable(KNAP_TEXTURES, 97, 44, 22, 15);
        outputSlot = helper.getSlotDrawable();
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        outputSlot.draw(minecraft, 116, 32);
        arrow.draw(minecraft, 86, 33);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, KnappingRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 116, 32);
        itemStackGroup.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
