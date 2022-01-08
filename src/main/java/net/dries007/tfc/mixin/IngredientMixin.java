/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Set;

import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.collections.NoopAddSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public abstract class IngredientMixin
{
    @Shadow(remap = false) @Final @Mutable private static Set<Ingredient> INSTANCES;

    static
    {
        INSTANCES = new NoopAddSet<>(); // Silly hypothetical bugfix causing actual race conditions
    }
}
