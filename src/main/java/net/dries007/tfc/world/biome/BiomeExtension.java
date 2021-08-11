/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * This is a wrapper class around extra data, which is assigned to particular biomes
 * Some functionality of biomes is also redirected (through mixins) to call this extension where possible.
 * This extension is tracked in {@link TFCBiomes} by registry key.
 */
public class BiomeExtension
{
    @SuppressWarnings("ConstantConditions")
    public static final BiomeExtension EMPTY = new BiomeExtension(null, null);

    private final ResourceKey<Biome> id;
    private final BiomeVariants variants;

    public BiomeExtension(ResourceKey<Biome> id, BiomeVariants variants)
    {
        this.id = id;
        this.variants = variants;
    }

    /**
     * Gets the variants object held by this extension.
     * This is responsible for providing noise and grouping parameters, along with access to individual biomes for each climate.
     * This is used ONLY within TFC's chunk generator.
     *
     * @return a variants object, used to get noise and smoothing parameters,
     */
    public BiomeVariants getVariants()
    {
        return variants;
    }

    public ResourceKey<Biome> getRegistryKey()
    {
        return id;
    }
}
