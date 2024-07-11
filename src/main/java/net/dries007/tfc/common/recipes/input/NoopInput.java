/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.input;

/**
 * This is a no-op, un-constructible implementation of {@link net.minecraft.world.item.crafting.RecipeInput}. It is used to signify the recipe
 * has custom matching behavior, or relies on an external cache, and thus should never be queried directly via typical methods.
 */
public final class NoopInput implements NonEmptyInput
{
    private NoopInput() {}
}
