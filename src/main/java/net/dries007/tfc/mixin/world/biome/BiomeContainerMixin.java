package net.dries007.tfc.mixin.world.biome;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes https://bugs.mojang.com/browse/MC-197616
 * Would be fixed in Forge by https://github.com/MinecraftForge/MinecraftForge/pull/7436
 */
@Mixin(BiomeContainer.class)
public class BiomeContainerMixin
{
    @Shadow @Final private Biome[] biomes;
    @Shadow @Final private IObjectIntIterable<Biome> biomeRegistry;

    @Inject(method = "writeBiomes", at = @At(value = "RETURN"), cancellable = true)
    private void redirect$writeBiomes$getId(CallbackInfoReturnable<int[]> cir)
    {
        // Nothing we can do without verifiable access to the actual biome registry
        if (biomeRegistry instanceof SimpleRegistry)
        {
            final SimpleRegistry<Biome> registry = (SimpleRegistry<Biome>) biomeRegistry;
            final int[] values = cir.getReturnValue();
            for (int i = 0; i < values.length; i++)
            {
                if (values[i] == -1)
                {
                    // Invalid biome id detected - try and serialize again, by querying the name through the registry
                    final Biome biome = registry.get(biomes[i].getRegistryName());
                    if (biome != null)
                    {
                        values[i] = registry.getId(biome);
                    }
                }
            }
        }
    }
}
