package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraftforge.common.util.Lazy;

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

    public static LayerFactory<BiomeVariants> biomes(IAreaFactory<? extends IArea> factory)
    {
        return new LayerFactory<>(factory, TFCLayerUtil::getFromLayerId);
    }

    public static LayerFactory<Rock> rocks(IAreaFactory<? extends IArea> factory, TFCBiomeProvider.LayerSettings settings)
    {
        // On servers, this is called earlier than resources (rocks) are loaded, for the purposes of initial / spawn chunk generation
        // So, we lazily initialize this, including identifying errors, and return the correct mapping function only once we can gaurentee it's initialized.
        Lazy<IntFunction<Rock>> verifier = Lazy.of(() -> {
            if (RockManager.INSTANCE.isLoaded())
            {
                final List<ResourceLocation> missingIds = new ArrayList<>();
                final Rock[] rockArray = settings.getRocks().stream().map(id -> {
                    Rock rock = RockManager.INSTANCE.get(id);
                    if (rock == null)
                    {
                        missingIds.add(id);
                    }
                    return rock;
                }).filter(Objects::nonNull).toArray(Rock[]::new);
                if (!missingIds.isEmpty())
                {
                    LOGGER.warn("Rock layer factory was initialized with {} missing rocks. If this message was before world creation you can ignore it.", missingIds.size());
                    LOGGER.warn("Missing rock ids for the following values: {}", missingIds);
                }
                return i -> rockArray[i];
            }
            return i -> {
                throw new UnsupportedOperationException("Cannot query rocks before RockManager is loaded");
            };
        });
        return new LayerFactory<>(factory, i -> verifier.get().apply(i));
    }

    public static LayerFactory<PlateTectonicsClassification> plateTectonics(IAreaFactory<? extends IArea> factory)
    {
        return new LayerFactory<>(factory, PlateTectonicsClassification::valueOf);
    }

    /**
     * Uses a thread local area, as the underlying area is not synchronized.
     * This is an optimization adapted from Lithium, implementing a much better cache for the LazyArea underneath
     */
    private final ThreadLocal<? extends IArea> area;
    private final IntFunction<T> mappingFunction;

    protected LayerFactory(IAreaFactory<? extends IArea> factory, IntFunction<T> mappingFunction)
    {
        this.area = ThreadLocal.withInitial(factory::make);
        this.mappingFunction = mappingFunction;
    }

    public T get(int x, int z)
    {
        return mappingFunction.apply(area.get().get(x, z));
    }
}
