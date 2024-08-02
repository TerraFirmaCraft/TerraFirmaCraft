/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config.animals;

import java.util.function.Supplier;

import net.dries007.tfc.config.ConfigBuilder;

public record OviparousAnimalConfig(
    ProducingAnimalConfig inner,
    Supplier<Integer> hatchDays
) {
    public static OviparousAnimalConfig build(ConfigBuilder builder, String name, double familiarityCap, int adulthoodDays, int uses, boolean eatsRottenFood, int produceTicks, double produceFamiliarity, int hatchDays)
    {
        return new OviparousAnimalConfig(
            ProducingAnimalConfig.build(builder, name, familiarityCap, adulthoodDays, uses, eatsRottenFood, produceTicks, produceFamiliarity),
            builder.comment("Ticks until egg is ready to hatch").define("%sHatchDays".formatted(name), hatchDays, 0, Integer.MAX_VALUE)
        );
    }
}
