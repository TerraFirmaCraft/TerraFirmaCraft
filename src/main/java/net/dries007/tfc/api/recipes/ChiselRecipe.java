/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class ChiselRecipe extends IForgeRegistryEntry.Impl<ChiselRecipe>
{
    @Nullable
    public static ChiselRecipe get(IBlockState state)
    {
        return TFCRegistries.CHISEL.getValuesCollection().stream().filter(r -> r.matches(state)).findFirst().orElse(null);
    }

    private final IIngredient<IBlockState> ingredient;
    private final IBlockState stateOut;

    public ChiselRecipe(Block blockIn, IBlockState stateOut)
    {
        this(state -> state.getBlock() == blockIn, stateOut);
    }

    public ChiselRecipe(IIngredient<IBlockState> ingredient, IBlockState stateOut)
    {
        this.ingredient = ingredient;
        this.stateOut = stateOut;
    }

    public IBlockState getOutputState()
    {
        return stateOut;
    }

    public boolean matches(IBlockState stateIn)
    {
        return ingredient.test(stateIn);
    }

    public enum Mode
    {
        SMOOTH,
        STAIR,
        SLAB;

        private static final Mode[] VALUES = values();

        @Nonnull
        public static Mode valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : SMOOTH;
        }

        @Nonnull
        public Mode next()
        {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }
}
