package net.dries007.tfc.mixin.world.biome;

import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.level.ColorResolver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeColors.class)
public interface BiomeColorsAccessor
{
    @Accessor("WATER_COLOR_RESOLVER")
    static void accessor$setWaterColorResolver(ColorResolver waterColorResolver) {}
}
