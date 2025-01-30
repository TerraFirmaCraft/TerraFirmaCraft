/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.recipes;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.ItemType;

import static net.dries007.tfc.common.recipes.WeldingRecipe.Behavior.*;

public interface WeldingRecipes extends Recipes
{
    default void weldingRecipes()
    {
        for (Metal metal : Metal.values())
        {
            if (metal.defaultParts())
            {
                weld(metal, ItemType.INGOT, ItemType.INGOT, ItemType.DOUBLE_INGOT, IGNORE);
                weld(metal, ItemType.SHEET, ItemType.INGOT, ItemType.DOUBLE_SHEET, IGNORE);
            }
            if (metal.allParts())
            {
                weld(metal, ItemType.UNFINISHED_HELMET, ItemType.SHEET, ItemType.HELMET, COPY_BEST);
                weld(metal, ItemType.UNFINISHED_CHESTPLATE, ItemType.DOUBLE_SHEET, ItemType.CHESTPLATE, COPY_BEST);
                weld(metal, ItemType.UNFINISHED_GREAVES, ItemType.SHEET, ItemType.GREAVES, COPY_BEST);
                weld(metal, ItemType.UNFINISHED_BOOTS, ItemType.SHEET, ItemType.BOOTS, COPY_BEST);
                weld(metal, ItemType.KNIFE_BLADE, ItemType.KNIFE_BLADE, ItemType.SHEARS, COPY_WORST);
            }
        }

        weld(Metal.WEAK_STEEL, Metal.PIG_IRON, Metal.HIGH_CARBON_BLACK_STEEL);
        weld(Metal.WEAK_BLUE_STEEL, Metal.BLACK_STEEL, Metal.HIGH_CARBON_BLUE_STEEL);
        weld(Metal.WEAK_RED_STEEL, Metal.BLACK_STEEL, Metal.HIGH_CARBON_RED_STEEL);

        add(new WeldingRecipe(
            ingredientOf(Metal.BRASS, ItemType.ROD), ingredientOf(Metal.BRASS, ItemType.ROD),
            0, ItemStackProvider.of(TFCItems.JACKS), IGNORE
        ));
    }

    private void weld(Metal ingot1, Metal ingot2, Metal ingotOut)
    {
        add(new WeldingRecipe(
            ingredientOf(ingot1, ItemType.INGOT),
            ingredientOf(ingot2, ItemType.INGOT),
            ingotOut.tier() - 1,
            ItemStackProvider.of(TFCItems.METAL_ITEMS.get(ingotOut).get(ItemType.INGOT)),
            IGNORE
        ));
    }

    private void weld(Metal metal, ItemType input1, ItemType input2, ItemType output, WeldingRecipe.Behavior behavior)
    {
        add(new WeldingRecipe(
            ingredientOf(metal, input1),
            ingredientOf(metal, input2),
            metal.tier() - 1,
            ItemStackProvider.of(TFCItems.METAL_ITEMS.get(metal).get(output)),
            behavior
        ));
    }
}
