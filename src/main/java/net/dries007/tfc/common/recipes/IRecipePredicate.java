/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

/**
 * This is a companion interface to recipe, but without the bound on the matches() and without the additional parameters
 * @param <C> The input type of this recipe
 */
public interface IRecipePredicate<C>
{
    /**
     * @param input The input to this recipe
     * @return {@code true} if the input matches the recipe.
     */
    boolean matches(C input);
}
