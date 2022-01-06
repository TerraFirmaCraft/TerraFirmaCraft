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
    @Shadow @Final @Mutable private static Set<Ingredient> INSTANCES;

    static
    {
        INSTANCES = new NoopAddSet<>(); // Silly hypothetical bugfix causing actual race conditions
    }
}
