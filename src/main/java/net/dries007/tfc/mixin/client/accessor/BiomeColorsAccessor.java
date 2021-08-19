package net.dries007.tfc.mixin.client.accessor;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeColors.class)
public interface BiomeColorsAccessor
{
    @Accessor("WATER_COLOR_RESOLVER")
    static void accessor$setWaterColorsResolver(ColorResolver waterColorsResolver) { throw new AssertionError(); }
}
