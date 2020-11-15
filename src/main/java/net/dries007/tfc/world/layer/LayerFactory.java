package net.dries007.tfc.world.layer;

import java.util.Objects;
import java.util.function.IntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.chunkdata.PlateTectonicsClassification;

/**
 * A wrapper around {@link IAreaFactory}
 */
public class LayerFactory<T>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static LayerFactory<BiomeVariants> biomes(IAreaFactory<LazyArea> factory)
    {
        return new LayerFactory<>(factory, TFCLayerUtil::getFromLayerId);
    }

    public static LayerFactory<Rock> rocks(IAreaFactory<LazyArea> factory, TFCBiomeProvider.LayerSettings settings)
    {
        final Rock[] rockArray = settings.getRocks().stream().map(id -> {
            Rock rock = RockManager.INSTANCE.get(id);
            if (rock == null)
            {
                LOGGER.warn("Ignoring unknown rock in layer: " + id);
            }
            return rock;
        }).filter(Objects::nonNull).toArray(Rock[]::new);
        return new LayerFactory<>(factory, i -> rockArray[i]);
    }

    public static LayerFactory<PlateTectonicsClassification> plateTectonics(IAreaFactory<LazyArea> factory)
    {
        return new LayerFactory<>(factory, PlateTectonicsClassification::valueOf);
    }

    private final LazyArea area;
    private final IntFunction<T> mappingFunction;

    protected LayerFactory(IAreaFactory<LazyArea> factory, IntFunction<T> mappingFunction)
    {
        this.area = factory.make();
        this.mappingFunction = mappingFunction;
    }

    public T get(int x, int z)
    {
        return mappingFunction.apply(area.get(x, z));
    }
}
