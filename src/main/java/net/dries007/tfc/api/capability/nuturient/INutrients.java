/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nuturient;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.util.agriculture.Nutrient;

public interface INutrients
{
    float getNutrients(ItemStack stack, Nutrient nutrient);

    @SideOnly(Side.CLIENT)
    default void addNutrientInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        // todo: make this respect skills tiers
        // i.e. lowest level shows category, i.e. "Category: Grain"
        // next level shows which nutrients it has, i.e. "Category: Grain, Nutrients: Carbohydrates, Fat"
        // final level shows exact values, i.e. "Category: Grain, Nutrients: Carbohydrates (1.5), Fat (3.0)"
        for (Nutrient nutrient : Nutrient.values())
        {
            text.add(nutrient.name() + ": " + getNutrients(stack, nutrient));
        }
    }
}
