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

public class ScrapingRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, ScrapingRecipe> CACHE = new IndirectHashCollection<>(ScrapingRecipe::getValidItems);

    @Nullable
    public static ScrapingRecipe getRecipe(World world, ItemStackRecipeWrapper wrapper)
    {
        for (ScrapingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    public ScrapingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result)
    {
        super(id, ingredient, result);
        if (result.getCount() != 1)
        {
            throw new IllegalArgumentException("Scraping recipes only support a stack size of 1");
        }
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SCRAPING.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.SCRAPING;
    }
}
