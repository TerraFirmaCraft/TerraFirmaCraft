package net.dries007.tfc.world.biome;

import java.util.function.LongFunction;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.noise.INoise2D;

/**
 * This is a wrapper class around extra data, which is assigned to particular biomes
 * Some functionality of biomes is also redirected (through mixins) to call this extension where possible.
 * This extension is tracked in {@link TFCBiomes} by registry key.
 */
public class BiomeExtension
{
    @SuppressWarnings("ConstantConditions")
    public static final BiomeExtension EMPTY = new BiomeExtension(null, null, null, null);

    private final RegistryKey<Biome> id;
    private final BiomeTemperature temperature;
    private final BiomeRainfall rainfall;
    private final BiomeVariants variants;

    public BiomeExtension(RegistryKey<Biome> id, BiomeTemperature temperature, BiomeRainfall rainfall, BiomeVariants variants)
    {
        this.id = id;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.variants = variants;
    }

    public BiomeVariants getVariants()
    {
        return variants;
    }

    public RegistryKey<Biome> getRegistryKey()
    {
        return id;
    }
}
