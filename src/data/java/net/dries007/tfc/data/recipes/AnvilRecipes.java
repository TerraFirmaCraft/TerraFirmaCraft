/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.recipes;

import java.util.List;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.BlockType;
import net.dries007.tfc.util.Metal.ItemType;

import static net.dries007.tfc.common.component.forge.ForgeRule.*;

public interface AnvilRecipes extends Recipes
{
    default void anvilRecipes()
    {
        final ForgeRule[] hitX3 = {HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST};

        for (Metal metal : Metal.values())
        {
            if (metal.defaultParts())
            {
                anvil(metal, ItemType.INGOT, ItemType.SHEET, false, hitX3);
                anvil(
                    ingredientOf(metal, ItemType.DOUBLE_INGOT),
                    ItemStackProvider.of(TFCItems.METAL_ITEMS.get(metal).get(ItemType.ROD), 2), metal.tier(), false, hitX3);
            }

            if (metal.allParts())
            {
                anvil(metal, ItemType.DOUBLE_SHEET, ItemType.TUYERE, false, BEND_LAST, BEND_SECOND_LAST);
                anvil(metal, ItemType.INGOT, ItemType.PICKAXE_HEAD, true, PUNCH_LAST, BEND_NOT_LAST, DRAW_NOT_LAST);
                anvil(metal, ItemType.INGOT, ItemType.SHOVEL_HEAD, true, PUNCH_LAST, HIT_NOT_LAST);
                anvil(metal, ItemType.INGOT, ItemType.AXE_HEAD, true, PUNCH_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
                anvil(metal, ItemType.INGOT, ItemType.HOE_HEAD, true, PUNCH_LAST, HIT_NOT_LAST, BEND_NOT_LAST);
                anvil(metal, ItemType.INGOT, ItemType.HAMMER_HEAD, true, PUNCH_LAST, SHRINK_NOT_LAST);
                anvil(metal, ItemType.INGOT, ItemType.PROPICK_HEAD, true, PUNCH_LAST, DRAW_NOT_LAST, BEND_NOT_LAST);
                anvil(metal, ItemType.INGOT, ItemType.SAW_BLADE, true, HIT_LAST, HIT_SECOND_LAST);
                anvil(metal, ItemType.DOUBLE_INGOT, ItemType.SWORD_BLADE, true, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
                anvil(metal, ItemType.DOUBLE_INGOT, ItemType.MACE_HEAD, true, HIT_LAST, SHRINK_NOT_LAST, BEND_NOT_LAST);
                anvil(metal, ItemType.INGOT, ItemType.SCYTHE_BLADE, true, HIT_LAST, DRAW_SECOND_LAST, BEND_THIRD_LAST);
                anvil(metal, ItemType.INGOT, ItemType.KNIFE_BLADE, true, HIT_LAST, DRAW_SECOND_LAST, DRAW_THIRD_LAST);
                anvil(metal, ItemType.INGOT, ItemType.JAVELIN_HEAD, true, HIT_LAST, HIT_SECOND_LAST, DRAW_THIRD_LAST);
                anvil(metal, ItemType.INGOT, ItemType.CHISEL_HEAD, true, HIT_LAST, HIT_NOT_LAST, DRAW_NOT_LAST);
                anvil(metal, ItemType.DOUBLE_SHEET, ItemType.SHIELD, true, UPSET_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
                anvil(metal, ItemType.SHEET, ItemType.FISH_HOOK, true, DRAW_NOT_LAST, BEND_ANY, HIT_ANY);

                anvil(metal, ItemType.DOUBLE_SHEET, ItemType.UNFINISHED_HELMET, true, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
                anvil(metal, ItemType.DOUBLE_SHEET, ItemType.UNFINISHED_CHESTPLATE, true, HIT_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
                anvil(metal, ItemType.DOUBLE_SHEET, ItemType.UNFINISHED_GREAVES, true, BEND_ANY, DRAW_ANY, HIT_ANY);
                anvil(metal, ItemType.SHEET, ItemType.UNFINISHED_BOOTS, true, BEND_LAST, BEND_SECOND_LAST, SHRINK_THIRD_LAST);

                anvil(metal, ItemType.SHEET, BlockType.TRAPDOOR, 1, BEND_LAST, DRAW_SECOND_LAST, DRAW_THIRD_LAST);
                anvil(metal, ItemType.INGOT, ItemType.UNFINISHED_LAMP, false, BEND_LAST, BEND_SECOND_LAST, DRAW_THIRD_LAST);
                anvil(metal, ItemType.INGOT, BlockType.CHAIN, 16, HIT_ANY, DRAW_THIRD_LAST);
                anvil(metal, ItemType.SHEET, BlockType.BARS, 8, UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST);
                add("metal/bars/" + metal.getSerializedName() + "_double", new AnvilRecipe(
                    ingredientOf(metal, ItemType.DOUBLE_SHEET),
                    metal.tier(), List.of(UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST), false,
                    ItemStackProvider.of(TFCBlocks.METALS.get(metal).get(BlockType.BARS), 16)));
            }
        }

        anvil(Ingredient.of(TFCItems.RAW_IRON_BLOOM), ItemStackProvider.of(TFCItems.REFINED_IRON_BLOOM), 2, false, hitX3);
        anvil(Ingredient.of(TFCItems.REFINED_IRON_BLOOM), ItemStackProvider.of(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(ItemType.INGOT)), 2, false, hitX3);

        anvil(Metal.PIG_IRON, Metal.HIGH_CARBON_STEEL);
        anvil(Metal.HIGH_CARBON_STEEL, Metal.STEEL);
        anvil(Metal.HIGH_CARBON_BLACK_STEEL, Metal.BLACK_STEEL);
        anvil(Metal.HIGH_CARBON_BLUE_STEEL, Metal.BLUE_STEEL);
        anvil(Metal.HIGH_CARBON_RED_STEEL, Metal.RED_STEEL);

        anvil(ingredientOf(Metal.WROUGHT_IRON, ItemType.SHEET), ItemStackProvider.of(Blocks.IRON_DOOR), 3, false, HIT_LAST, DRAW_NOT_LAST, PUNCH_NOT_LAST);
        anvil(ingredientOf(Metal.RED_STEEL, ItemType.SHEET), ItemStackProvider.of(TFCItems.RED_STEEL_BUCKET), 6, false, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        anvil(ingredientOf(Metal.BLUE_STEEL, ItemType.SHEET), ItemStackProvider.of(TFCItems.BLUE_STEEL_BUCKET), 6, false, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        anvil(ingredientOf(Metal.WROUGHT_IRON, ItemType.DOUBLE_SHEET), ItemStackProvider.of(TFCBlocks.GRILL), 3, false, PUNCH_LAST, PUNCH_NOT_LAST, DRAW_ANY);
        anvil(ingredientOf(Metal.BRASS, ItemType.INGOT), ItemStackProvider.of(TFCItems.BRASS_MECHANISMS, 2), 0, false, PUNCH_LAST, HIT_SECOND_LAST, PUNCH_THIRD_LAST);
        anvil(ingredientOf(Metal.TIN, ItemType.INGOT), ItemStackProvider.of(TFCItems.JAR_LID, 16), 0, false, HIT_LAST, HIT_SECOND_LAST, PUNCH_THIRD_LAST);
        anvil(ingredientOf(Metal.BRASS, ItemType.ROD), ItemStackProvider.of(TFCItems.BLOWPIPE), 0, false, DRAW_LAST, DRAW_SECOND_LAST, HIT_THIRD_LAST);
        anvil(ingredientOf(Metal.STEEL, ItemType.SHEET), ItemStackProvider.of(TFCBlocks.STEEL_PIPE, 8), 4, false, DRAW_LAST, BEND_NOT_LAST);
    }

    private void anvil(Metal metal, ItemType inputType, ItemType outputType, boolean applyForgingBonus, ForgeRule... rules)
    {
        anvil(ingredientOf(metal, inputType), ItemStackProvider.of(TFCItems.METAL_ITEMS.get(metal).get(outputType)), metal.tier(), applyForgingBonus, rules);
    }

    private void anvil(Metal metal, ItemType inputType, BlockType outputType, int amount, ForgeRule... rules)
    {
        anvil(ingredientOf(metal, inputType), ItemStackProvider.of(TFCBlocks.METALS.get(metal).get(outputType), amount), metal.tier(), false, rules);
    }

    private void anvil(Metal ingotIn, Metal ingotOut)
    {
        anvil(ingredientOf(ingotIn, ItemType.INGOT), ItemStackProvider.of(TFCItems.METAL_ITEMS.get(ingotOut).get(ItemType.INGOT)), ingotIn.tier(), false, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST);
    }

    private void anvil(Ingredient input, ItemStackProvider output, int minTier, boolean applyForgingBonus, ForgeRule... rules)
    {
        add(new AnvilRecipe(input, minTier, List.of(rules), applyForgingBonus, output));
    }
}
