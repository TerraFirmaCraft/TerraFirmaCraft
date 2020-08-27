/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import vazkii.patchouli.api.VariableHolder;

@SuppressWarnings("unused")
public class SimpleHeatRecipeComponent extends SimpleRecipeComponent<HeatRecipe>
{
    @VariableHolder
    @SerializedName("recipe")
    public String recipeName;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        Objects.requireNonNull(recipeName, "Recipe name is null?");
        recipe = TFCRegistries.HEAT.getValue(new ResourceLocation(recipeName));
        super.build(componentX, componentY, pageNum);
    }
}
