/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.biome;

import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.level.ColorResolver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeColors.class)
public interface BiomeColorsAccessor
{
    /**
     * This is used to replace the color resolver, which is the central channel point for all water color queries. This is perfect as it has a position context available and does not use it, so we just replace it with one which queries our color handler.
     */
    @Accessor("WATER_COLOR")
    static void accessor$setWaterColorResolver(ColorResolver waterColorResolver) {}
}
