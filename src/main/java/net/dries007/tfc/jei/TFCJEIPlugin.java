/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.jei.categories.AnvilCategory;
import net.dries007.tfc.jei.categories.QuernCategory;
import net.dries007.tfc.jei.wrappers.AnvilWrapper;
import net.dries007.tfc.jei.wrappers.QuernWrapper;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;

@JEIPlugin
public final class TFCJEIPlugin implements IModPlugin
{
    public static final String QUERN_UID = TFCConstants.MOD_ID + ".quern";
    public static final String ANVIL_UID = TFCConstants.MOD_ID + ".anvil";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        //Add new JEI recipe categories
        registry.addRecipeCategories(new QuernCategory(registry.getJeiHelpers().getGuiHelper())); //Quern
        registry.addRecipeCategories(new AnvilCategory(registry.getJeiHelpers().getGuiHelper())); //Anvil
    }

    @Override
    public void register(IModRegistry registry)
    {
        //Wraps all quern recipes
        List<QuernWrapper> quernList = TFCRegistries.QUERN.getValuesCollection()
            .stream()
            .map(QuernWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(quernList, QUERN_UID); //Register recipes to quern category
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.QUERN), QUERN_UID); //Register BlockQuern as the device that do quern recipes

        //Wraps all anvil recipes
        List<AnvilWrapper> anvilList = TFCRegistries.ANVIL.getValuesCollection()
            .stream()
            .map(AnvilWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(anvilList, ANVIL_UID);
        registry.addRecipeCatalyst(BlockAnvilTFC.get(Metal.WROUGHT_IRON, 1), ANVIL_UID); //This is returning air :/
    }
}
