/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.categories;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.compat.jei.BaseRecipeCategory;
import net.dries007.tfc.compat.jei.util.BackgroundDrawable;
import net.dries007.tfc.compat.jei.wrappers.RockLayerWrapper;

@ParametersAreNonnullByDefault
public class RockLayerCategory extends BaseRecipeCategory<RockLayerWrapper>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/jei_rocklayer.png");

    public RockLayerCategory(IGuiHelper helper, String Uid)
    {
        super(new BackgroundDrawable(BACKGROUND, 164, 110), Uid);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RockLayerWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 73, 5);
        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));


        int slot = 1;
        for (List<ItemStack> oreStacks : ingredients.getOutputs(VanillaTypes.ITEM))
        {
            if (slot > 27) break; // Avoid overflow
            int x = 1 + ((slot - 1) % 9) * 18;
            int y = 55 + ((slot - 1) / 9) * 18;
            itemStackGroup.init(slot, false, x, y);
            itemStackGroup.set(slot, oreStacks);
            slot++;
        }
    }
}
