/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;

/**
 * todo: in 1.13+ move this to a json recipe type
 */
public class AlloyRecipe extends IForgeRegistryEntry.Impl<AlloyRecipe>
{
    private final ImmutableMap<Metal, Predicate<Double>> metalMap;
    private final Metal result;

    private AlloyRecipe(@Nonnull Metal result, ImmutableMap<Metal, Predicate<Double>> alloyMap)
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

    public ImmutableMap<Metal, Predicate<Double>> getMetals()
    {
        return metalMap;
    }

    public static class Builder
    {
        private final Metal result;
        private final ImmutableMap.Builder<Metal, Predicate<Double>> builder;

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
            return add(loc, x -> x >= min && x <= max);
        }

        public Builder add(@Nonnull ResourceLocation loc, @Nonnull Predicate<Double> condition)
        {
            Metal metal = TFCRegistries.METALS.getValue(loc);
            if (metal == null)
                throw new IllegalArgumentException("Result metal is not allowed to be null. Missing metal for key: " + loc.toString());
            return add(metal, condition);
        }

        public Builder add(@Nonnull Metal metal, float min, float max)
        {
            return add(metal, x -> x >= min && x <= max);
        }

        public Builder add(@Nonnull Metal metal, @Nonnull Predicate<Double> condition)
        {
            builder.put(metal, condition);
            return this;
        }

        public AlloyRecipe build()
        {
            return new AlloyRecipe(result, builder.build());
        }
    }

}
