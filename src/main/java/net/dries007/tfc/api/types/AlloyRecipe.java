/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;

public class AlloyRecipe extends IForgeRegistryEntry.Impl<AlloyRecipe>
{
    public final ImmutableMap<Metal, Predicate<Float>> MAP;
    private final Metal result;

    private AlloyRecipe(@Nonnull Metal result, ImmutableMap<Metal, Predicate<Float>> alloyMap)
    {
        this.MAP = alloyMap;
        this.result = result;

        // This ensures that no metal result has more than one alloy recipe
        // Required so that we can search for alloys by result registry name
        setRegistryName(result.getRegistryName());
    }

    public Metal getResult()
    {
        return result;
    }

    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public static class Builder
    {
        private final Metal result;
        private final ImmutableMap.Builder<Metal, Predicate<Float>> builder;

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

        public Builder add(@Nonnull ResourceLocation loc, float min, float max)
        {
            return add(loc, x -> x >= min && x <= max);
        }

        public Builder add(@Nonnull ResourceLocation loc, @Nonnull Predicate<Float> condition)
        {
            Metal metal = TFCRegistries.METALS.getValue(loc);
            if (metal == null)
                throw new IllegalArgumentException("Result metal is not allowed to be null. Missing metal for key: " + result.toString());
            return add(metal, condition);
        }

        public Builder add(@Nonnull Metal metal, float min, float max)
        {
            return add(metal, x -> x >= min && x <= max);
        }

        public Builder add(@Nonnull Metal metal, @Nonnull Predicate<Float> condition)
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
