package net.dries007.tfc.world.biome;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * This is a wrapper class around extra data, which is assigned to particular biomes
 * Some functionality of biomes is also redirected (through mixins) to call this extension where possible.
 * This extension is tracked in {@link TFCBiomes} by registry key.
 */
public class BiomeExtension
{
    @SuppressWarnings("ConstantConditions")
    public static final BiomeExtension EMPTY = new BiomeExtension(null, null);

    private final RegistryKey<Biome> id;
    private final BiomeVariants variants;

    public BiomeExtension(RegistryKey<Biome> id, BiomeVariants variants)
    {
        this.id = id;
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
