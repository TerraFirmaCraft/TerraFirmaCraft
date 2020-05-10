package net.dries007.tfc.objects.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCRecipeTypes
{
    public static final IRecipeType<CollapseRecipe> COLLAPSE = register("collapse");

    private static <R extends IRecipe<?>> IRecipeType<R> register(String name)
    {
        return IRecipeType.register(MOD_ID + ":" + name);
    }
}
