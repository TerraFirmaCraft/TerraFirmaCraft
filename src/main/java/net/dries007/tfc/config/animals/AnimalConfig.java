/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config.animals;

import java.util.function.Supplier;

import net.dries007.tfc.config.ConfigBuilder;

public record AnimalConfig(
    Supplier<Double> familiarityCap,
    Supplier<Integer> adulthoodDays,
    Supplier<Integer> uses,
    Supplier<Boolean> eatsRottenFood
) {
    public static AnimalConfig build(ConfigBuilder builder, String name, double familiarityCap, int adulthoodDays, int uses, boolean eatsRottenFood)
    {
        return new AnimalConfig(
            builder.comment("Max familiarity an adult may reach").define("%sFamiliarityCap".formatted(name), familiarityCap, 0, 1),
            builder.comment("Days until animal reaches adulthood").define("%sAdulthoodDays".formatted(name), adulthoodDays, 0, Integer.MAX_VALUE),
            builder.comment("Uses before animal becomes old and can no longer be used").define("%sUses".formatted(name), uses, 0, Integer.MAX_VALUE),
            builder.comment("Does the animal eat rotten food?").define("%sEatsRottenFood".formatted(name), eatsRottenFood)
        );
    }
}
