/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.jei.IJEIRecipeWrapper;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.metal.ItemIngot;

/**
 * todo: in 1.13+ move this to a json recipe type
 */
public class AlloyRecipe extends IForgeRegistryEntry.Impl<AlloyRecipe> implements IJEIRecipeWrapper
{
    private final ImmutableMap<Metal, AlloyRange> metalMap;
    private final Metal result;

    private AlloyRecipe(@Nonnull Metal result, ImmutableMap<Metal, AlloyRange> alloyMap)
    {
        this.metalMap = alloyMap;
        this.result = result;

        // This ensures that no metal result has more than one alloy recipe
        // Required so that we can search for alloys by result registry name
        //noinspection ConstantConditions
        setRegistryName(result.getRegistryName());
    }

    public Metal getResult()
    {
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public ImmutableMap<Metal, AlloyRange> getMetals()
    {
        return metalMap;
    }

    @Override
    public NonNullList<IIngredient<FluidStack>> getFluidIngredients()
    {
        NonNullList<IIngredient<FluidStack>> list = NonNullList.create();
        for (Metal metal : this.getMetals().keySet())
        {
            int min = (int) (this.getMetals().get(metal).getMin() * 100);
            int max = (int) (this.getMetals().get(metal).getMax() * 100);
            list.add(IIngredient.of(FluidsTFC.getMetalFluid(metal), min));
            list.add(IIngredient.of(FluidsTFC.getMetalFluid(metal), max));
        }
        return list;
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs()
    {
        return NonNullList.withSize(1, new ItemStack(ItemIngot.get(result, Metal.ItemType.INGOT)));
    }

    public static class Builder
    {
        private final Metal result;
        private final ImmutableMap.Builder<Metal, AlloyRange> builder;

        public Builder(@Nonnull Metal result)
        {
            this.result = result;
            this.builder = new ImmutableMap.Builder<>();
        }

        public Builder(@Nonnull ResourceLocation loc)
        {
            this.result = TFCRegistries.METALS.getValue(loc);
            if (result == null)
                throw new IllegalArgumentException("Result metal is not allowed to be null. Missing metal for key: " + loc.toString());
            this.builder = new ImmutableMap.Builder<>();
        }

        public Builder add(@Nonnull ResourceLocation loc, double min, double max)
        {
            return add(loc, new AlloyRange(min, max));
        }

        public Builder add(@Nonnull ResourceLocation loc, @Nonnull AlloyRange condition)
        {
            Metal metal = TFCRegistries.METALS.getValue(loc);
            if (metal == null)
                throw new IllegalArgumentException("Result metal is not allowed to be null. Missing metal for key: " + loc.toString());
            return add(metal, condition);
        }

        public Builder add(@Nonnull Metal metal, double min, double max)
        {
            return add(metal, new AlloyRange(min, max));
        }

        public Builder add(@Nonnull Metal metal, @Nonnull AlloyRange condition)
        {
            builder.put(metal, condition);
            return this;
        }

        public AlloyRecipe build()
        {
            return new AlloyRecipe(result, builder.build());
        }
    }

    public static class AlloyRange
    {
        private double min, max;

        AlloyRange(double min, double max)
        {
            this.min = min;
            this.max = max;
        }

        public double getMin()
        {
            return min;
        }

        public double getMax()
        {
            return max;
        }

        public boolean isValid(double value)
        {
            return value >= min && value <= max;
        }
    }
}
