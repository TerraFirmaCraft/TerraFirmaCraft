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
import net.dries007.tfc.compat.jei.wrappers.VeinWrapper;

@ParametersAreNonnullByDefault
public class VeinCategory extends BaseRecipeCategory<VeinWrapper>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(TerraFirmaCraft.MOD_ID, "textures/gui/jei_vein.png");

    public VeinCategory(IGuiHelper helper, String Uid)
    {
        super(new BackgroundDrawable(BACKGROUND, 164, 110), Uid);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, VeinWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, false, 73, 18);
        itemStackGroup.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));


        int slot = 1;
        for (List<ItemStack> oreStacks : ingredients.getInputs(VanillaTypes.ITEM))
        {
            if (slot > 18) break; // Avoid overflow
            int x = 1 + ((slot - 1) % 9) * 18;
            int y = 68 + ((slot - 1) / 9) * 18;
            itemStackGroup.init(slot, true, x, y);
            itemStackGroup.set(slot, oreStacks);
            slot++;
        }

        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 1)
        {
            // Has loose rock
            itemStackGroup.init(slot, false, 118, 43);
            itemStackGroup.set(slot, ingredients.getOutputs(VanillaTypes.ITEM).get(1));
        }
    }
}
