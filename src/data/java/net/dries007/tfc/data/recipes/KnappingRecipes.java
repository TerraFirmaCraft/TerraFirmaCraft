package net.dries007.tfc.data.recipes;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.data.providers.BuiltinKnappingTypes;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

public interface KnappingRecipes extends Recipes
{
    default void knappingRecipes()
    {
        rockKnapping(RockCategory.ItemType.AXE_HEAD, " X   ", "XXXX ", "XXXXX", "XXXX ", " X   ");
        rockKnapping(RockCategory.ItemType.SHOVEL_HEAD, "XXX", "XXX", "XXX", "XXX", " X ");
        rockKnapping(RockCategory.ItemType.HOE_HEAD, "XXXXX", "   XX");
        rockKnapping("1", RockCategory.ItemType.HOE_HEAD, 2, "XXXXX", "XX   ", "     ", "XXXXX", "XX   ");
        rockKnapping("2", RockCategory.ItemType.HOE_HEAD, 2, "XXXXX", "XX   ", "     ", "XXXXX", "   XX");
        rockKnapping(RockCategory.ItemType.KNIFE_HEAD, "X ", "XX", "XX", "XX", "XX");
        rockKnapping("1", RockCategory.ItemType.KNIFE_HEAD, 2, "X  X ", "XX XX", "XX XX", "XX XX", "XX XX");
        rockKnapping("2", RockCategory.ItemType.KNIFE_HEAD, 2, "X   X", "XX XX", "XX XX", "XX XX", "XX XX");
        rockKnapping("3", RockCategory.ItemType.KNIFE_HEAD, 2, " X X ", "XX XX", "XX XX", "XX XX", "XX XX");
        rockKnapping(RockCategory.ItemType.JAVELIN_HEAD, "XXX  ", "XXXX ", "XXXXX", " XXX ", "  X  ");
        rockKnapping(RockCategory.ItemType.HAMMER_HEAD, "XXXXX", "XXXXX", "  X  ");

        clayKnapping(TFCItems.UNFIRED_VESSEL, " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ");
        clayKnapping(TFCItems.UNFIRED_LARGE_VESSEL, "X   X", "X   X", "X   X", "X   X", "XXXXX");
        clayKnapping(TFCItems.UNFIRED_JUG, " X   ", "XXXX ", "XXX X", "XXXX ", "XXX  ");
        clayKnapping(TFCItems.UNFIRED_POT, "X   X", "X   X", "X   X", "XXXXX", " XXX ");
        clayKnapping(TFCItems.UNFIRED_BOWL, 2, "X   X", " XXX ");
        clayKnapping("2", TFCItems.UNFIRED_BOWL, 4, false, "X   X", " XXX ", "     ", "X   X", " XXX ");
        clayKnapping(TFCItems.UNFIRED_BRICK, 3, "XXXXX", "     ", "XXXXX", "     ", "XXXXX");
        clayKnapping(TFCItems.UNFIRED_FLOWER_POT, 2, " X X ", " XXX ", "     ", " X X ", " XXX ");
        clayKnapping(TFCItems.UNFIRED_SPINDLE_HEAD, 1, "  X  ", "XXXXX", "  X  ");
        clayKnapping(TFCItems.UNFIRED_PAN, 1, "X   X", "XXXXX", " XXX ");
        clayKnapping(TFCItems.UNFIRED_BLOWPIPE, 1, " X X ", " X X ", " XXX ", " XXX ", " XXX ");

        clayKnapping(Metal.ItemType.INGOT, "XXXX", "X  X", "X  X", "X  X", "XXXX");
        clayKnapping(Metal.ItemType.AXE_HEAD, "X XXX", "    X", "     ", "    X", "X XXX");
        clayKnapping(Metal.ItemType.CHISEL_HEAD, "XX XX", "XX XX", "XX XX", "XX XX", "XX XX");
        clayKnapping(Metal.ItemType.HAMMER_HEAD, "XXXXX", "     ", "     ", "XX XX", "XXXXX");
        clayKnapping(Metal.ItemType.HOE_HEAD, "XXXXX", "     ", "  XXX", "XXXXX");
        clayKnapping(Metal.ItemType.JAVELIN_HEAD, "   XX", "    X", "     ", "X   X", "XX XX");
        clayKnapping(Metal.ItemType.KNIFE_BLADE, "XX X", "X  X", "X  X", "X  X", "X  X");
        clayKnapping(Metal.ItemType.MACE_HEAD, "XX XX", "X   X", "X   X", "X   X", "XX XX");
        clayKnapping(Metal.ItemType.PICKAXE_HEAD, "XXXXX", "X   X", " XXX ", "XXXXX");
        clayKnapping(Metal.ItemType.PROPICK_HEAD, "XXXXX", "    X", " XXX ", " XXXX", "XXXXX");
        clayKnapping(Metal.ItemType.SAW_BLADE, "  XXX", "   XX", "X   X", "X    ", "XXX  ");
        clayKnapping(Metal.ItemType.SHOVEL_HEAD, "X   X", "X   X", "X   X", "X   X", "XX XX");
        clayKnapping(Metal.ItemType.SWORD_BLADE, "  XXX", "   XX", "X   X", "XX  X", "XXXX ");
        clayKnapping(Metal.ItemType.SCYTHE_BLADE, "XXXXX", "X    ", "    X", "  XXX", "XXXXX");
        clayKnapping(TFCItems.UNFIRED_BELL_MOLD, 1, "XXXXX", "XX XX", "X   X", "X   X", "X   X");

        fireClayKnapping(TFCItems.UNFIRED_CRUCIBLE, 1, "X   X", "X   X", "X   X", "X   X", "XXXXX");
        fireClayKnapping(TFCItems.UNFIRED_FIRE_BRICK, 3, "XXXXX", "     ", "XXXXX", "     ", "XXXXX");
        fireClayKnapping(TFCItems.UNFIRED_FIRE_INGOT_MOLD, 2,"XXXX", "X  X", "X  X", "X  X", "XXXX");

        leatherKnapping(Items.LEATHER_HELMET, "XXXXX", "X   X", "X   X", "     ", "     ");
        leatherKnapping(Items.LEATHER_CHESTPLATE, "X   X", "XXXXX", "XXXXX", "XXXXX", "XXXXX");
        leatherKnapping(Items.LEATHER_LEGGINGS, "XXXXX", "XXXXX", "XX XX", "XX XX", "XX XX");
        leatherKnapping(Items.LEATHER_BOOTS, "XX   ", "XX   ", "XX   ", "XXXX ", "XXXXX");
        leatherKnapping(Items.SADDLE, "  X  ", "XXXXX", "XXXXX", "XXXXX", "  X  ");
        leatherKnapping(Items.LEATHER_HORSE_ARMOR, "    X", " XXXX", "XXX  ", "XX X ", "X   X");

        // todo: goat horn knapping, how tf is this supposed to work with instruments and components
    }

    private void rockKnapping(RockCategory.ItemType output, String... pattern)
    {
        rockKnapping("", output, 1, pattern);
    }

    private void rockKnapping(String suffix, RockCategory.ItemType output, int count, String... pattern)
    {
        for (RockCategory type : RockCategory.values())
            add(nameOf(TFCItems.ROCK_TOOLS.get(type).get(output)) + (suffix.isEmpty() ? "" : "_" + suffix), new KnappingRecipe(
                KnappingType.MANAGER.getCheckedReference(BuiltinKnappingTypes.ROCK),
                KnappingPattern.from(false, pattern),
                Optional.of(Ingredient.of(TFCTags.Items.STONES_LOOSE_CATEGORY.get(type))),
                new ItemStack(TFCItems.ROCK_TOOLS.get(type).get(output), count)
            ));
    }

    private void clayKnapping(Metal.ItemType output, String... pattern)
    {
        clayKnapping("", TFCItems.UNFIRED_MOLDS.get(output), output == Metal.ItemType.INGOT ? 2 : 1, true, pattern);
    }

    private void clayKnapping(ItemLike output, String... pattern)
    {
        clayKnapping(output, 1, pattern);
    }

    private void clayKnapping(ItemLike output, int count, String... pattern)
    {
        clayKnapping("", output, count, false, pattern);
    }

    private void clayKnapping(String suffix, ItemLike output, int count, boolean defaultOn, String... pattern)
    {
        add(nameOf(output) + (suffix.isEmpty() ? "" : "_" + suffix), new KnappingRecipe(
            KnappingType.MANAGER.getCheckedReference(BuiltinKnappingTypes.CLAY),
            KnappingPattern.from(defaultOn, pattern),
            Optional.empty(),
            new ItemStack(output, count)
        ));
        // Un-crafting, only for non-suffixed recipes
        if (suffix.isEmpty()) new CraftingRecipes.Builder((name, r) -> add(nameOf(output) + "_to_clay", r))
            .input(output)
            .shapeless(Items.CLAY, 5 / count);
    }

    private void fireClayKnapping(ItemLike output, int count, String... pattern)
    {
        knapping(BuiltinKnappingTypes.FIRE_CLAY, pattern, output, count);
        // Un-crafting, only for non-suffixed recipes
        new CraftingRecipes.Builder((name, r) -> add(nameOf(output) + "_to_fire_clay", r))
            .input(output)
            .shapeless(TFCItems.FIRE_CLAY, 5 / count);
    }

    private void leatherKnapping(ItemLike output, String... pattern)
    {
        knapping(BuiltinKnappingTypes.LEATHER, pattern, output, 1);
    }

    private void knapping(ResourceLocation knappingType, String[] pattern, ItemLike output, int count)
    {
        add(new KnappingRecipe(KnappingType.MANAGER.getCheckedReference(knappingType), KnappingPattern.from(true, pattern), Optional.empty(), new ItemStack(output, count)));
    }
}
