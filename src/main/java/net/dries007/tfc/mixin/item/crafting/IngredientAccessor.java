/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.item.crafting;

import net.minecraft.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ingredient.class)
public interface IngredientAccessor
{
    /**
     * To call internal ingredients invalidate() method not through super (since it's a delegate)
     */
    @Invoker(value = "invalidate", remap = false)
    void invoke$invalidate();
}
