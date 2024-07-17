package net.dries007.tfc.data.recipes;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.ItemType;

public interface CastingRecipes extends Recipes
{
    default void castingRecipes()
    {
        for (Metal metal : Metal.values())
        {
            casting(metal.name() + "_ingot", TFCItems.MOLDS.get(ItemType.INGOT), metal, 0.1f);
            casting(metal.name() + "_fire_ingot", TFCItems.FIRE_INGOT_MOLD, metal, 0.01f);

            for (ItemType type : ItemType.values())
                if (metal.allParts() && metal.tier() <= 2 && type != ItemType.INGOT && type.hasMold())
                    casting(metal.name() + "_" + type.name(), TFCItems.MOLDS.get(type), metal, TFCItems.METAL_ITEMS.get(metal).get(type), units(type), 1f);
        }

        casting("brass_bell", TFCItems.BELL_MOLD, Metal.BRASS, TFCBlocks.BRASS_BELL, 100, 1f);
        casting("bronze_bell", TFCItems.BELL_MOLD, Metal.BRONZE, TFCBlocks.BRONZE_BELL, 100, 1f);
        casting("gold_bell", TFCItems.BELL_MOLD, Metal.GOLD, Items.BELL, 100, 1f);
    }

    private void casting(String name, ItemLike item, Metal metal, float chance)
    {
        casting(name, item, metal, TFCItems.METAL_ITEMS.get(metal).get(ItemType.INGOT), units(ItemType.INGOT), chance);
    }

    private void casting(String name, ItemLike item, Metal metal, ItemLike result, int units, float chance)
    {
        add(name, new CastingRecipe(
            Ingredient.of(item),
            SizedFluidIngredient.of(fluidOf(metal), units),
            ItemStackProvider.of(result),
            chance
        ));
    }
}
