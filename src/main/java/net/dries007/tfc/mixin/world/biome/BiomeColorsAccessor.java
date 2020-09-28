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
    @Accessor("WATER_COLOR_RESOLVER")
    static void accessor$setWaterColorResolver(ColorResolver waterColorResolver) {}
}
