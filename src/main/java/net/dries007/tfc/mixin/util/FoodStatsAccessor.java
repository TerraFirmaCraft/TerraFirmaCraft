/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.util;

import net.minecraft.util.FoodStats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodStats.class)
public interface FoodStatsAccessor
{
    /**
     * Required as the original value is private and {@link net.dries007.tfc.common.capabilities.food.TFCFoodStats} needs to access it
     */
    @Accessor(value = "exhaustionLevel")
    float accessor$getExhaustionLevel();

    /**
     * Required as the original accessor is client only
     */
    @Accessor(value = "saturationLevel")
    void accessor$setSaturationLevel(float saturationLevel);
}
