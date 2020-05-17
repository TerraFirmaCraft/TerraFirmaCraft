package net.dries007.tfc.world.biome;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class BiomeVariantHolder implements Supplier<TFCBiome>
{
    private final Map<BiomeTemperature, Map<BiomeRainfall, RegistryObject<TFCBiome>>> biomeVariants;
    private final List<RegistryObject<TFCBiome>> allVariants;

    public BiomeVariantHolder(String baseName, IFactory<TFCBiome> factory)
    {
        this.biomeVariants = new EnumMap<>(BiomeTemperature.class);
        this.allVariants = new ArrayList<>();

        for (BiomeTemperature temp : BiomeTemperature.values())
        {
            Map<BiomeRainfall, RegistryObject<TFCBiome>> innerBiomes = new EnumMap<>(BiomeRainfall.class);
            for (BiomeRainfall rain : BiomeRainfall.values())
            {
                String name = baseName + "_" + temp.name().toLowerCase() + "_" + rain.name().toLowerCase();
                RegistryObject<TFCBiome> obj = getRegistry().register(name, () -> {
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

    /**
     * Addons need to override this if they intend to use this class for their own biomes.
     */
    public DeferredRegister<Biome> getRegistry()
    {
        return TFCBiomes.BIOMES;
    }

    public interface IFactory<T>
    {
        T create(BiomeTemperature temperature, BiomeRainfall rainfall);
    }
}
