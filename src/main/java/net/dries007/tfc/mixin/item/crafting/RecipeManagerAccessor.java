package net.dries007.tfc.mixin.item.crafting;

import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor
{
    @Invoker("byType")
    <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> call$byType(IRecipeType<T> recipeTypeIn);
}
