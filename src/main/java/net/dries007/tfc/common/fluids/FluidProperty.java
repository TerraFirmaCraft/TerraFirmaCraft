/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.base.Suppliers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.util.registry.IdHolder;


public class FluidProperty extends Property<FluidProperty.FluidKey>
{
    public static FluidProperty create(String name, Stream<Object> fluids)
    {
        return new FluidProperty(name, fluids.map(obj -> switch (obj)
        {
            case ResourceLocation id -> id; // Direct references to fluid IDs are allowed
            case Fluid fluid -> BuiltInRegistries.FLUID.getKey(fluid);
            case IdHolder<?> reg -> reg.getId(); // Registry objects are allowed, we assume they're fluids
            case FluidHolder<?> pair -> pair.source().getId(); // Fluid pairs are allowed (we know how to obtain the ID from it without loading the fluid)
            default -> throw new IllegalArgumentException("FluidProperty#create called with a weird thing: " + obj);
        }));
    }

    private final Map<String, FluidKey> keysById;
    private final Map<Fluid, FluidKey> keysByFluid;
    private final List<FluidKey> keysByIndex;
    private final Supplier<Set<Fluid>> fluids;

    protected FluidProperty(String name, Stream<ResourceLocation> fluids)
    {
        super(name, FluidKey.class);

        this.keysByFluid = new HashMap<>();
        this.keysById = new HashMap<>();
        this.keysByIndex = new ArrayList<>(); // Needs to be deterministically ordered

        fluids.sorted().forEach(id -> {
            assert !keysById.containsKey(id.getPath()) : "Duplicate fluid key: " + id.getPath();
            final FluidKey key = new FluidKey(id);

            keysById.put(id.getPath(), key);
            keysByIndex.add(key);
        });

        this.fluids = Suppliers.memoize(() -> this.keysByIndex.stream().map(FluidKey::getFluid).collect(Collectors.toSet()));
    }

    public boolean canContain(Fluid fluid)
    {
        return fluids.get().contains(fluid);
    }

    public FluidKey keyForOrEmpty(Fluid fluid)
    {
        return canContain(fluid) ? keyFor(fluid) : keyFor(Fluids.EMPTY);
    }

    public FluidKey keyFor(Fluid fluid)
    {
        FluidKey key = keysByFluid.get(fluid);
        if (key != null)
        {
            return key;
        }
        key = keysById.get(Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(fluid)).getPath());
        if (key == null)
        {
            throw new IllegalArgumentException("Tried to get the FluidKey for a fluid [" + BuiltInRegistries.FLUID.getKey(fluid) + "] which was not present in property " + getName() + " / " + getPossibleValues());
        }
        keysByFluid.put(fluid, key);
        return key;
    }

    @Override
    public Collection<FluidKey> getPossibleValues()
    {
        return keysByIndex;
    }

    @Override
    public String getName(FluidKey value)
    {
        return value.name.getPath();
    }

    @Override
    public Optional<FluidKey> getValue(String value)
    {
        return Optional.ofNullable(keysById.get(value));
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o;
    }

    @Override
    public int generateHashCode()
    {
        return System.identityHashCode(this);
    }

    public static class FluidKey implements Comparable<FluidKey>
    {
        private final ResourceLocation name;
        private final DeferredHolder<Fluid, ? extends Fluid> fluid;

        private FluidKey(ResourceLocation name)
        {
            this.name = name;
            this.fluid = DeferredHolder.create(Registries.FLUID, name);
        }

        public Fluid getFluid()
        {
            return fluid.get();
        }

        public boolean is(Fluid other)
        {
            return fluid.get() == other;
        }

        public boolean is(FluidKey other)
        {
            return name.equals(other.name);
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
