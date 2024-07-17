package net.dries007.tfc.data.recipes;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.ItemType;

public interface WeldingRecipes extends Recipes
{
    default void weldingRecipes()
    {
        for (Metal metal : Metal.values())
        {
            if (metal.defaultParts())
            {
                weld(metal, ItemType.INGOT, ItemType.INGOT, ItemType.DOUBLE_INGOT);
                weld(metal, ItemType.SHEET, ItemType.INGOT, ItemType.DOUBLE_SHEET);
            }
            if (metal.allParts())
            {
                weld(metal, ItemType.UNFINISHED_HELMET, ItemType.SHEET, ItemType.HELMET);
                weld(metal, ItemType.UNFINISHED_CHESTPLATE, ItemType.DOUBLE_SHEET, ItemType.CHESTPLATE);
                weld(metal, ItemType.UNFINISHED_GREAVES, ItemType.SHEET, ItemType.GREAVES);
                weld(metal, ItemType.UNFINISHED_BOOTS, ItemType.SHEET, ItemType.BOOTS);
                weld(metal, ItemType.KNIFE_BLADE, ItemType.KNIFE_BLADE, ItemType.SHEARS);
            }
        }

        weld(Metal.WEAK_STEEL, Metal.PIG_IRON, Metal.HIGH_CARBON_BLACK_STEEL);
        weld(Metal.WEAK_BLUE_STEEL, Metal.BLACK_STEEL, Metal.HIGH_CARBON_BLUE_STEEL);
        weld(Metal.WEAK_RED_STEEL, Metal.BLACK_STEEL, Metal.HIGH_CARBON_RED_STEEL);

        add(new WeldingRecipe(
            ingredientOf(Metal.BRASS, ItemType.ROD), ingredientOf(Metal.BRASS, ItemType.ROD),
            0, ItemStackProvider.of(TFCItems.JACKS), false
        ));
    }

    private void weld(Metal ingot1, Metal ingot2, Metal ingotOut)
    {
        add(new WeldingRecipe(
            ingredientOf(ingot1, ItemType.INGOT),
            ingredientOf(ingot2, ItemType.INGOT),
            ingotOut.tier() - 1,
            ItemStackProvider.of(TFCItems.METAL_ITEMS.get(ingotOut).get(ItemType.INGOT)),
            false
        ));
    }

    private void weld(Metal metal, ItemType input1, ItemType input2, ItemType output)
    {
        add(new WeldingRecipe(
            ingredientOf(metal, input1),
            ingredientOf(metal, input2),
            metal.tier() - 1,
            ItemStackProvider.of(TFCItems.METAL_ITEMS.get(metal).get(output)),
            false
        ));
    }
}
