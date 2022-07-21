/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.util.Metal;

// Merged enum
public record FluidType(String name, OptionalInt color, Supplier<? extends Fluid> fluid)
{
    public static final FluidType SALT_WATER = new FluidType("salt_water", OptionalInt.empty(), TFCFluids.SALT_WATER.source());
    public static final FluidType SPRING_WATER = new FluidType("spring_water", OptionalInt.empty(), TFCFluids.SPRING_WATER.source());

    private static final Map<Enum<?>, FluidType> IDENTITY = new HashMap<>();
    private static final List<FluidType> VALUES = Stream.of(
            Stream.of(SALT_WATER, SPRING_WATER),
            Arrays.stream(SimpleFluid.values()).map(fluid -> fromEnum(fluid, fluid.getColor(), fluid.getId(), TFCFluids.SIMPLE_FLUIDS.get(fluid).source())),
            Arrays.stream(Alcohol.values()).map(fluid -> fromEnum(fluid, fluid.getColor(), fluid.getId(), TFCFluids.ALCOHOLS.get(fluid).source())),
            Arrays.stream(DyeColor.values()).map(dye -> fromEnum(dye, TFCFluids.dyeColorToInt(dye), dye.getSerializedName() + "_dye", TFCFluids.COLORED_FLUIDS.get(dye).source())),
            Arrays.stream(Metal.Default.values()).map(metal -> fromEnum(metal, metal.getColor(), "metal/" + metal.getSerializedName(), TFCFluids.METALS.get(metal).source()))
        )
        .flatMap(Function.identity())
        .toList();

    public static <R> Map<FluidType, R> mapOf(Function<? super FluidType, ? extends R> map)
    {
        return VALUES.stream().collect(Collectors.toMap(Function.identity(), map));
    }

    public static FluidType asType(Enum<?> identity)
    {
        return IDENTITY.get(identity);
    }

    private static FluidType fromEnum(Enum<?> identity, int color, String name, Supplier<? extends Fluid> fluid)
    {
        final FluidType type = new FluidType(name, OptionalInt.of(TFCFluids.ALPHA_MASK | color), fluid);
        IDENTITY.put(identity, type);
        return type;
    }
}
