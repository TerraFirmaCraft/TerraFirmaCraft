/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.fluid.Fluid;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidProperty extends Property<FluidProperty.FluidKey>
{
    public static FluidProperty create(String name, Stream<Object> fluids)
    {
        return new FluidProperty(name, fluids.map(obj -> {
            if (obj instanceof ResourceLocation)
            {
                return (ResourceLocation) obj; // Direct references to fluid IDs are allowed
            }
            else if (obj instanceof Fluid)
            {
                return ((Fluid) obj).getRegistryName(); // Vanilla fluids are allowed
            }
            else if (obj instanceof RegistryObject)
            {
                return ((RegistryObject<?>) obj).getId(); // Registry objects are allowed, we assume they're fluids
            }
            else if (obj instanceof TFCFluids.FluidPair<?>)
            {
                return ((TFCFluids.FluidPair<?>) obj).getSecond().getId(); // Fluid pairs are allowed (we know how to obtain the ID from it without loading the fluid)
            }
            else
            {
                throw new IllegalArgumentException("FluidProperty#create called with a weird thing: " + obj);
            }
        }));
    }

    private final Map<String, FluidKey> valuesById;
    private final Map<Fluid, FluidKey> cachedValues;
    private final Set<FluidKey> values;
    private final Lazy<Set<Fluid>> fluids;

    protected FluidProperty(String name, Stream<ResourceLocation> fluids)
    {
        super(name, FluidKey.class);

        this.valuesById = fluids.collect(Collectors.toMap(ResourceLocation::getPath, FluidKey::new));
        this.cachedValues = new HashMap<>();
        this.values = new HashSet<>(this.valuesById.values());
        this.fluids = Lazy.of(() -> this.values.stream().map(FluidKey::getFluid).collect(Collectors.toSet()));
    }

    public boolean canContain(Fluid fluid)
    {
        return fluids.get().contains(fluid);
    }

    public Collection<Fluid> getPossibleFluids()
    {
        return fluids.get();
    }

    public FluidKey keyFor(Fluid fluid)
    {
        FluidKey key = cachedValues.get(fluid);
        if (key != null)
        {
            return key;
        }
        key = valuesById.get(Objects.requireNonNull(fluid.getRegistryName()).getPath());
        if (key == null)
        {
            throw new IllegalArgumentException("Tried to get the FluidKey for a fluid [" + fluid.getRegistryName() + "] which was not present in property " + getName() + " / " + getAllowedValues());
        }
        cachedValues.put(fluid, key);
        return key;
    }

    @Override
    public Collection<FluidKey> getAllowedValues()
    {
        return values;
    }

    @Override
    public String getName(FluidKey value)
    {
        return value.name.getPath();
    }



    @Override
    public Optional<FluidKey> parseValue(String value)
    {
        return Optional.ofNullable(valuesById.get(value));
    }

    public static class FluidKey implements Comparable<FluidKey>
    {
        private final ResourceLocation name;
        private final RegistryObject<Fluid> fluid;

        private FluidKey(ResourceLocation name)
        {
            this.name = name;
            this.fluid = RegistryObject.of(name, ForgeRegistries.FLUIDS);
        }

        public Fluid getFluid()
        {
            return fluid.get();
        }

        @Override
        public int compareTo(FluidKey other)
        {
            return name.compareTo(other.name);
        }

        @Override
        public String toString()
        {
            return "FluidKey[" + name + ']';
        }
    }
}
