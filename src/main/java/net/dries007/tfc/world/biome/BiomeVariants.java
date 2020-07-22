/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * This is a version of {@link RegistryObject} for biomes.
 * Since we have variants in both temperature and rainfall, we use this as the "biome main type" object.
 * To get the variant holder from the biome, use {@link TFCBiome#getVariants()}
 * To get the biome from the variants, use one of the {@link BiomeVariants#get()} methods.
 */
public class BiomeVariants implements Supplier<TFCBiome>
{
    private final Map<BiomeTemperature, Map<BiomeRainfall, RegistryObject<TFCBiome>>> biomeVariants;
    private final List<RegistryObject<TFCBiome>> allVariants;
    private final IFactory factory;
    private BiomeWeightType weightType = BiomeWeightType.OCEAN_IS_SHORE;
    private BiomeEdgeType edgeType = BiomeEdgeType.SINGLE;
    private BiomeLandType landType = BiomeLandType.LAND;
    private boolean spawnBiome;

    public BiomeVariants(DeferredRegister<Biome> registry, String baseName, BiomeVariants parent)
    {
        this(registry, baseName, parent.factory);
    }

    public BiomeVariants(DeferredRegister<Biome> registry, String baseName, IFactory factory)
    {
        this.factory = factory;
        this.biomeVariants = new EnumMap<>(BiomeTemperature.class);
        this.allVariants = new ArrayList<>();

        for (BiomeTemperature temp : BiomeTemperature.values())
        {
            Map<BiomeRainfall, RegistryObject<TFCBiome>> innerBiomes = new EnumMap<>(BiomeRainfall.class);
            for (BiomeRainfall rain : BiomeRainfall.values())
            {
                String name = baseName + "_" + temp.name().toLowerCase() + "_" + rain.name().toLowerCase();
                RegistryObject<TFCBiome> obj = registry.register(name, () -> {
                    TFCBiome biome = factory.create(temp, rain);
                    // Set the variant holder, so we can ask each biome to get variants later!
                    biome.setVariantHolder(this);
                    return biome;
                });
                innerBiomes.put(rain, obj);
                allVariants.add(obj);
            }
            biomeVariants.put(temp, innerBiomes);
        }
    }

    /**
     * @return the default instance (normal / normal)
     */
    public TFCBiome get()
    {
        return get(BiomeTemperature.NORMAL, BiomeRainfall.NORMAL).get();
    }

    /**
     * @return the biome instance of the specified temperature / rainfall
     */
    public RegistryObject<TFCBiome> get(float averageTemperature, float rainfall)
    {
        return get(BiomeTemperature.get(averageTemperature), BiomeRainfall.get(rainfall));
    }

    /**
     * @return the biome instance of the specified temperature / rainfall
     */
    public RegistryObject<TFCBiome> get(BiomeTemperature temp, BiomeRainfall rain)
    {
        return biomeVariants.get(temp).get(rain);
    }

    public List<RegistryObject<TFCBiome>> getAll()
    {
        return allVariants;
    }

    public BiomeWeightType getWeightType()
    {
        return weightType;
    }

    public BiomeVariants setWeightType(BiomeWeightType weightType)
    {
        this.weightType = weightType;
        return this;
    }

    public BiomeEdgeType getEdgeType()
    {
        return edgeType;
    }

    public BiomeVariants setEdgeType(BiomeEdgeType edgeType)
    {
        this.edgeType = edgeType;
        return this;
    }

    public boolean isSpawnBiome()
    {
        return spawnBiome;
    }

    public BiomeVariants setSpawnBiome()
    {
        this.spawnBiome = true;
        return this;
    }

    public BiomeLandType getLandType()
    {
        return landType;
    }

    public BiomeVariants setLandType(BiomeLandType landType)
    {
        this.landType = landType;
        return this;
    }

    public interface IFactory
    {
        TFCBiome create(BiomeTemperature temp, BiomeRainfall rain);
    }
}
