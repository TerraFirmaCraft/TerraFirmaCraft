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
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.jei.categories.*;
import net.dries007.tfc.jei.wrappers.*;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLoom;
import net.dries007.tfc.objects.items.metal.ItemAnvil;

@JEIPlugin
public final class TFCJEIPlugin implements IModPlugin
{
    private static final String QUERN_UID = TFCConstants.MOD_ID + ".quern";
    private static final String ANVIL_UID = TFCConstants.MOD_ID + ".anvil";
    private static final String WELDING_UID = TFCConstants.MOD_ID + ".welding";
    private static final String PITKILN_UID = TFCConstants.MOD_ID + ".pitkiln";
    private static final String LOOM_UID = TFCConstants.MOD_ID + ".loom";
    private static final String ALLOY_UID = TFCConstants.MOD_ID + ".alloy";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        //Add new JEI recipe categories
        registry.addRecipeCategories(new QuernCategory(registry.getJeiHelpers().getGuiHelper(), QUERN_UID));
        registry.addRecipeCategories(new AnvilCategory(registry.getJeiHelpers().getGuiHelper(), ANVIL_UID));
        registry.addRecipeCategories(new WeldingCategory(registry.getJeiHelpers().getGuiHelper(), WELDING_UID));
        registry.addRecipeCategories(new PitKilnCategory(registry.getJeiHelpers().getGuiHelper(), PITKILN_UID));
        registry.addRecipeCategories(new LoomCategory(registry.getJeiHelpers().getGuiHelper(), LOOM_UID));
        registry.addRecipeCategories(new AlloyCategory(registry.getJeiHelpers().getGuiHelper(), ALLOY_UID));
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

        //Wraps all welding recipes
        List<WeldingWrapper> weldList = TFCRegistries.WELDING.getValuesCollection()
            .stream()
            .map(WeldingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(weldList, WELDING_UID);
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (Metal.ItemType.ANVIL.hasType(metal))
            {
                registry.addRecipeCatalyst(new ItemStack(ItemAnvil.get(metal, Metal.ItemType.ANVIL)), ANVIL_UID);
                registry.addRecipeCatalyst(new ItemStack(ItemAnvil.get(metal, Metal.ItemType.ANVIL)), WELDING_UID);
            }
        }

        //Wraps all pit kiln recipes
        List<PitKilnWrapper> pitkilnRecipes = TFCRegistries.PIT_KILN.getValuesCollection()
            .stream()
            .map(PitKilnWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(pitkilnRecipes, PITKILN_UID);
        //No "Device" to wrap this to

        //Wraps all loom recipes
        List<LoomWrapper> loomRecipes = TFCRegistries.LOOM.getValuesCollection()
            .stream()
            .map(LoomWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(loomRecipes, LOOM_UID);
        registry.addRecipeCatalyst(new ItemStack(BlockLoom.get(Tree.SEQUOIA)), LOOM_UID);

        //Wraps all alloy recipes
        List<AlloyWrapper> alloyRecipes = TFCRegistries.ALLOYS.getValuesCollection()
            .stream()
            .map(AlloyWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(alloyRecipes, ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CRUCIBLE), ALLOY_UID);
    }
}
