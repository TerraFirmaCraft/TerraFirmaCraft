package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.dries007.tfc.util.collections.IndirectHashCollection;

public class QuernRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, QuernRecipe> CACHE = new IndirectHashCollection<>(QuernRecipe::getValidItems);

    public QuernRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result)
    {
        super(id, ingredient, result);
    }

    @Nullable
    public static QuernRecipe getRecipe(World world, ItemStackRecipeWrapper wrapper)
    {
        for (QuernRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    public static boolean exists(@Nullable World level, ItemStack stack)
    {
        assert level != null;
        return getRecipe(level, new ItemStackRecipeWrapper(stack)) != null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.QUERN.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.QUERN;
    }
}
