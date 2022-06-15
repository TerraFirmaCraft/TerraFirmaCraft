/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config.animals;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

public record MammalConfig(AnimalConfig inner, ForgeConfigSpec.IntValue gestationDays, ForgeConfigSpec.IntValue childCount)
{
    public static MammalConfig build(Function<String, ForgeConfigSpec.Builder> builder, String name, double familiarityCap, int adulthoodDays, int uses, boolean eatsRottenFood, int gestationDays, int childCount)
    {
        return new MammalConfig(
            AnimalConfig.build(builder, name, familiarityCap, adulthoodDays, uses, eatsRottenFood),
            builder.apply("%sGestationDays".formatted(name)).comment("Length of pregnancy in days").defineInRange("%sGestationDays".formatted(name), gestationDays, 0, Integer.MAX_VALUE),
            builder.apply("%sChildCount".formatted(name)).comment("Max number of children born").defineInRange("%sChildCount".formatted(name), childCount, 0, 100)
        );
    }
}
