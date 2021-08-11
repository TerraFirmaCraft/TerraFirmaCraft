/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.PlateTectonicsClassification;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;


public class LayerFactory<T>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static LayerFactory<BiomeVariants> biomes(AreaFactory factory)
    {
        return new LayerFactory<>(factory, TFCLayerUtil::getFromLayerId);
    }

    public static LayerFactory<Rock> rocks(AreaFactory factory, List<ResourceLocation> rockNames)
    {
        // On servers, this is called earlier than resources (rocks) are loaded, for the purposes of initial / spawn chunk generation
        // So, we lazy initialize this as we can't check that the rock names are valid before this
        Lazy<IntFunction<Rock>> verifier = Lazy.of(() -> {
            if (RockManager.INSTANCE.isLoaded())
            {
                final List<ResourceLocation> missingIds = new ArrayList<>();
                final Rock[] rockArray = rockNames.stream().map(id -> {
                    final Rock rock = RockManager.INSTANCE.get(id);
                    if (rock == null)
                    {
                        missingIds.add(id);
                    }
                    return rock;
                }).filter(Objects::nonNull).toArray(Rock[]::new);
                if (!missingIds.isEmpty())
                {
                    LOGGER.error("Rock layer factory was initialized with {} missing rocks.", missingIds.size());
                    LOGGER.error("Missing rock ids for the following values: {}", missingIds);
                    LOGGER.debug(new Exception());
                }
                return i -> rockArray[i];
            }
            throw new UnsupportedOperationException("Cannot query rocks before RockManager is loaded");
        });
        return new LayerFactory<>(factory, i -> verifier.get().apply(i));
    }

    public static LayerFactory<PlateTectonicsClassification> plateTectonics(AreaFactory factory)
    {
        return new LayerFactory<>(factory, PlateTectonicsClassification::valueOf);
    }

    public static LayerFactory<ForestType> forest(AreaFactory factory)
    {
        return new LayerFactory<>(factory, ForestType::valueOf);
    }

    /**
     * Uses a thread local area, as the underlying area is not synchronized.
     * This is an optimization adapted from Lithium, implementing a much better cache for the LazyArea underneath
     */
    private final ThreadLocal<Area> area;
    private final IntFunction<T> mappingFunction;

    protected LayerFactory(AreaFactory factory, IntFunction<T> mappingFunction)
    {
        this.area = ThreadLocal.withInitial(factory);
        this.mappingFunction = mappingFunction;
    }

    public T get(int x, int z)
    {
        return mappingFunction.apply(area.get().get(x, z));
    }
}
