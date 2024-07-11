/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;

import net.dries007.tfc.common.recipes.inventory.NoopInput;

/**
 * A companion interface for {@link NoopInput}, which provides no-op implementations of recipe methods that would typically be
 * queried with that input. The input is not constructible, so we know that nobody should be able to call these methods.
 */
public interface INoopInputRecipe extends ISimpleRecipe<NoopInput>
{
    @Override
    @Contract("_, _ -> fail")
    default boolean matches(NoopInput input, Level level)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract("_, _ -> fail")
    default ItemStack assemble(NoopInput input, HolderLookup.Provider registries)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract("_ -> fail")
    default NonNullList<ItemStack> getRemainingItems(NoopInput input)
    {
        throw new UnsupportedOperationException();
    }
}
