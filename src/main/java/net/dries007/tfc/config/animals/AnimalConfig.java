/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config.animals;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

public record AnimalConfig(ForgeConfigSpec.DoubleValue familiarityCap, ForgeConfigSpec.IntValue adulthoodDays, ForgeConfigSpec.IntValue uses, ForgeConfigSpec.BooleanValue eatsRottenFood)
{
    public static AnimalConfig build(Function<String, ForgeConfigSpec.Builder> builder, String name, double familiarityCap, int adulthoodDays, int uses, boolean eatsRottenFood)
    {
        return new AnimalConfig(
            builder.apply("%sFamiliarityCap".formatted(name)).comment("Max familiarity an adult may reach").defineInRange("%sFamiliarityCap".formatted(name), familiarityCap, 0, 1),
            builder.apply("%sAdulthoodDays".formatted(name)).comment("Days until animal reaches adulthood").defineInRange("%sAdulthoodDays".formatted(name), adulthoodDays, 0, Integer.MAX_VALUE),
            builder.apply("%sUses".formatted(name)).comment("Uses before animal becomes old and can no longer be used").defineInRange("%sUses".formatted(name), uses, 0, Integer.MAX_VALUE),
            builder.apply("%sEatsRottenFood".formatted(name)).comment("Does the animal eat rotten food?").define("%sEatsRottenFood".formatted(name), eatsRottenFood)
        );
    }
}
