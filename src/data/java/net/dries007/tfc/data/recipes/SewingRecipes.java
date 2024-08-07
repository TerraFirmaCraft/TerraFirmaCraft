/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.recipes;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.recipes.SewingRecipe;

public interface SewingRecipes extends Recipes
{
    default void sewingRecipes()
    {
        sewingRecipe(List.of(
            "   # #   ",
            "  ## ##  ",
            "         ",
            "  ## ##  ",
            "   # #   "
        ), List.of(
            "   ##   ",
            "  ####  ",
            "  ####  ",
            "   ##   "
        ), Items.FLOWER_BANNER_PATTERN);
        sewingRecipe(List.of(
            "  ## ##  ",
            "  #   #  ",
            "  # # #  ",
            "  # # #  ",
            "  ## ##  "
        ), List.of(
            "  #  #  ",
            "        ",
            "  ####  ",
            "  #  #  "
        ), Items.CREEPER_BANNER_PATTERN);
        sewingRecipe(List.of(
            "  #   #  ",
            "   ###   ",
            "  ## ##  ",
            "         ",
            "  #   #  "
        ), List.of(
            "  ####  ",
            "  #  #  ",
            "  ####  ",
            "  ####  "
        ), Items.SKULL_BANNER_PATTERN);
        sewingRecipe(List.of(
            "  ## ##  ",
            "  ## ##  ",
            "         ",
            "  ## ##  ",
            "  ## ##  "
        ), List.of(
            "  ####  ",
            "  #  #  ",
            "  #  #  ",
            "  ####  "
        ), Items.GLOBE_BANNER_PATTERN);
        sewingRecipe(List.of(
            "         ",
            " # # # # ",
            "         ",
            " # # # # ",
            "         "
        ), List.of(
            "########",
            "# #  # #",
            "# #  # #",
            "########"
        ), Items.PIGLIN_BANNER_PATTERN);
        sewingRecipe(List.of(
            "         ",
            " # # #  #",
            "       # ",
            " # # # # ",
            "         "
        ), List.of(
            "#####   ",
            "#      #",
            "#      #",
            "########"
        ), Items.MOJANG_BANNER_PATTERN);
        sewingRecipe(List.of(
            "     # # ",
            "    #    ",
            "         ",
            "    #    ",
            "     # # "
        ), List.of(
            "     ## ",
            "    ##  ",
            "    ##  ",
            "     ## "
        ), Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "    #    ",
            "   # #   ",
            "  #   #  ",
            " #     # "
        ), List.of(
            "        ",
            "   ##   ",
            "  ####  ",
            " ###### "
        ), Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "   # #   ",
            "  #   #  ",
            "         ",
            "  #   #  ",
            "   # #   "
        ), List.of(
            "   ##   ",
            "  #  #  ",
            "  #  #  ",
            "   ##   "
        ), Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "# # # # #",
            "         ",
            "         ",
            "     ##  "
        ), List.of(
            "        ",
            "########",
            "     #  ",
            "     #  "
        ), Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "   ##### ",
            "         ",
            "#####    ",
            "         "
        ), List.of(
            "     ###",
            "   ###  ",
            " ###    ",
            "##      "
        ), Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "  # # #  ",
            "         ",
            " # # #   ",
            "         ",
            " # # #   "
        ), List.of(
            "  # # # ",
            "  # # # ",
            " # # #  ",
            " # # #  "
        ), Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "   # #   ",
            "         ",
            "# ## ## #",
            "         ",
            "   # #   "
        ), List.of(
            "   ##   ",
            "###  ###",
            "  #  #  ",
            "   ##   "
        ), Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "         ",
            "         ",
            "         ",
            "         "
        ), List.of(
            "        ",
            "        ",
            "        ",
            "        "
        ), Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "  #      ",
            "         ",
            "  # ##   ",
            "         ",
            "     ##  "
        ), List.of(
            "  #     ",
            "  ###   ",
            "    #   ",
            "    ##  "
        ), Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            " #     # ",
            "# #   # #",
            "         ",
            "# #   # #",
            " #     # "
        ), List.of(
            "        ",
            " #    # ",
            " #    # ",
            "        "
        ), Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "  #   #  ",
            "         ",
            "  ## ##  ",
            "         "
        ), List.of(
            "  #  #  ",
            "  #  #  ",
            "  #  #  ",
            "  ####  "
        ), Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            " # #     ",
            "    # #  ",
            "         ",
            "    # #  ",
            " # #     "
        ), List.of(
            " ###    ",
            "    ### ",
            "    ### ",
            " ###    "
        ), Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "#### ##  ",
            "      #  ",
            "####     ",
            "         "
        ), List.of(
            "        ",
            "###  #  ",
            "### #   ",
            "#####   "
        ), Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "     ##  ",
            " #       ",
            " #   ##  ",
            " #       ",
            "     ##  "
        ), List.of(
            "     #  ",
            " ##  #  ",
            " ##  #  ",
            "     #  "
        ), Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            " #       ",
            " #    ## ",
            "      ## ",
            "  # #    ",
            "       # "
        ), List.of(
            " #   ## ",
            " ##  # #",
            "  # # # ",
            "  ##   #"
        ), Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "##     ##",
            "   ###   ",
            "   # #   ",
            "   # #   "
        ), List.of(
            "##    ##",
            " ##  ## ",
            "   ##   ",
            "   ##   "
        ), Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
    }

    private void sewingRecipe(List<String> stitches, List<String> squares, ItemLike output)
    {
        add(SewingRecipe.from(String.join("", stitches), String.join("", squares), new ItemStack(output)));
    }
}
