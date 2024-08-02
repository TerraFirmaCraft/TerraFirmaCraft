/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config.animals;

import java.util.function.Supplier;

import net.dries007.tfc.config.ConfigBuilder;

public record MammalConfig(
    AnimalConfig inner,
    Supplier<Integer> gestationDays,
    Supplier<Integer> childCount
) {
    public static MammalConfig build(ConfigBuilder builder, String name, double familiarityCap, int adulthoodDays, int uses, boolean eatsRottenFood, int gestationDays, int childCount)
    {
        return new MammalConfig(
            AnimalConfig.build(builder, name, familiarityCap, adulthoodDays, uses, eatsRottenFood),
            builder.comment("Length of pregnancy in days").define("%sGestationDays".formatted(name), gestationDays, 0, Integer.MAX_VALUE),
            builder.comment("Max number of children born").define("%sChildCount".formatted(name), childCount, 0, 100)
        );
    }
}
