/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.function.UnaryOperator;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IVariable;

public abstract class RecipeComponent<T extends Recipe<?>> extends CustomComponent
{
    @Nullable protected transient T recipe;
    @SerializedName("recipe") String recipeName;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        recipe = asRecipe(recipeName, getRecipeType()).orElse(null);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider provider)
    {
        recipeName = lookup.apply(IVariable.wrap(recipeName, provider)).asString();
    }

    protected abstract RecipeType<T> getRecipeType();
}
