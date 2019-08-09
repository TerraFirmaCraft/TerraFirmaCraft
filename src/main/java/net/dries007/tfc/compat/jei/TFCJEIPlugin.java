/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.client.gui.*;
import net.dries007.tfc.compat.jei.categories.*;
import net.dries007.tfc.compat.jei.wrappers.*;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLoom;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.metal.ItemAnvil;
import net.dries007.tfc.objects.items.rock.ItemRock;

@JEIPlugin
public final class TFCJEIPlugin implements IModPlugin
{
    private static final String ALLOY_UID = TFCConstants.MOD_ID + ".alloy";
    private static final String ANVIL_UID = TFCConstants.MOD_ID + ".anvil";
    private static final String BARREL_UID = TFCConstants.MOD_ID + ".barrel";
    private static final String HEAT_UID = TFCConstants.MOD_ID + ".heat";
    private static final String KNAP_CLAY_UID = TFCConstants.MOD_ID + ".knap.clay";
    private static final String KNAP_FIRECLAY_UID = TFCConstants.MOD_ID + ".knap.fireclay";
    private static final String KNAP_LEATHER_UID = TFCConstants.MOD_ID + ".knap.leather";
    private static final String KNAP_STONE_UID = TFCConstants.MOD_ID + ".knap.stone";
    private static final String LOOM_UID = TFCConstants.MOD_ID + ".loom";
    private static final String QUERN_UID = TFCConstants.MOD_ID + ".quern";
    private static final String WELDING_UID = TFCConstants.MOD_ID + ".welding";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        //Add new JEI recipe categories
        registry.addRecipeCategories(new AlloyCategory(registry.getJeiHelpers().getGuiHelper(), ALLOY_UID));
        registry.addRecipeCategories(new AnvilCategory(registry.getJeiHelpers().getGuiHelper(), ANVIL_UID));
        registry.addRecipeCategories(new BarrelCategory(registry.getJeiHelpers().getGuiHelper(), BARREL_UID));
        registry.addRecipeCategories(new HeatCategory(registry.getJeiHelpers().getGuiHelper(), HEAT_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_CLAY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_FIRECLAY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_LEATHER_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_STONE_UID));
        registry.addRecipeCategories(new LoomCategory(registry.getJeiHelpers().getGuiHelper(), LOOM_UID));
        registry.addRecipeCategories(new QuernCategory(registry.getJeiHelpers().getGuiHelper(), QUERN_UID));
        registry.addRecipeCategories(new WeldingCategory(registry.getJeiHelpers().getGuiHelper(), WELDING_UID));
    }

    @Override
    public void register(IModRegistry registry)
    {
        //Wraps all quern recipes
        List<SimpleRecipeWrapper> quernList = TFCRegistries.QUERN.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(quernList, QUERN_UID); //Register recipes to quern category
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.QUERN), QUERN_UID); //Register BlockQuern as the device that do quern recipes

        //Wraps all heating recipes, if they return ingredients(1 or more) -> itemstacks(1 or more)
        List<HeatRecipeWrapper> heatList = TFCRegistries.HEAT.getValuesCollection()
            .stream()
            .filter(r -> r.getOutputs().size() > 0 && r.getIngredients().size() > 0)
            .map(HeatRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(heatList, HEAT_UID);
        //No block/item to wrap this one

        //Wraps all anvil recipes
        List<AnvilRecipeWrapper> anvilList = TFCRegistries.ANVIL.getValuesCollection()
            .stream()
            .map(AnvilRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(anvilList, ANVIL_UID);

        //Wraps all welding recipes
        List<SimpleRecipeWrapper> weldList = TFCRegistries.WELDING.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
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

        //Wraps all loom recipes
        List<SimpleRecipeWrapper> loomRecipes = TFCRegistries.LOOM.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(loomRecipes, LOOM_UID);
        for (Tree tree : TFCRegistries.TREES.getValuesCollection())
        {
            registry.addRecipeCatalyst(new ItemStack(BlockLoom.get(tree)), LOOM_UID);
        }

        //Wraps all alloy recipes
        List<AlloyWrapper> alloyRecipes = TFCRegistries.ALLOYS.getValuesCollection()
            .stream()
            .map(AlloyWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(alloyRecipes, ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CRUCIBLE), ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.FIRED_VESSEL), ALLOY_UID);

        //Wraps all clay knap recipes
        List<KnappingWrapper> clayknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.CLAY)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(clayknapRecipes, KNAP_CLAY_UID);
        registry.addRecipeCatalyst(new ItemStack(Items.CLAY_BALL), KNAP_CLAY_UID);

        //Wraps all fire clay knap recipes
        List<KnappingWrapper> fireclayknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.FIRE_CLAY)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(fireclayknapRecipes, KNAP_FIRECLAY_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.FIRE_CLAY), KNAP_FIRECLAY_UID);

        //Wraps all leather knap recipes
        List<KnappingWrapper> leatherknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.LEATHER)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(leatherknapRecipes, KNAP_LEATHER_UID);
        registry.addRecipeCatalyst(new ItemStack(Items.LEATHER), KNAP_LEATHER_UID);

        //Wraps all leather knap recipes
        List<KnappingWrapper> stoneknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream()
            .filter(recipe -> recipe.getType() == KnappingRecipe.Type.STONE)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(stoneknapRecipes, KNAP_STONE_UID);
        for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
        {
            registry.addRecipeCatalyst(new ItemStack(ItemRock.get(rock)), KNAP_STONE_UID);
        }

        //Wraps all barrel recipes
        List<BarrelWrapper> barrelRecipes = TFCRegistries.BARREL.getValuesCollection()
            .stream().filter(recipe -> recipe.getOutputStack() != ItemStack.EMPTY || recipe.getOutputFluid() != null)
            .map(BarrelWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(barrelRecipes, BARREL_UID);
        for (Item barrelItem : BlocksTFC.getAllBarrelItemBlocks())
        {
            registry.addRecipeCatalyst(new ItemStack(barrelItem), BARREL_UID);
        }

        //Click areas
        registry.addRecipeClickArea(GuiKnapping.class, 97, 42, 22, 19, KNAP_CLAY_UID, KNAP_FIRECLAY_UID, KNAP_LEATHER_UID, KNAP_STONE_UID);
        registry.addRecipeClickArea(GuiAnvilTFC.class, 12, 96, 152, 7, ANVIL_UID, WELDING_UID);
        registry.addRecipeClickArea(GuiBarrel.class, 36, 38, 14, 14, BARREL_UID);
        registry.addRecipeClickArea(GuiQuern.class, 83, 19, 9, 46, QUERN_UID);
        registry.addRecipeClickArea(GuiCrucible.class, 137, 23, 15, 66, ALLOY_UID);
    }
}
