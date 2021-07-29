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
    @Invoker(value = "invalidate")
    void invoke$invalidate();
}
