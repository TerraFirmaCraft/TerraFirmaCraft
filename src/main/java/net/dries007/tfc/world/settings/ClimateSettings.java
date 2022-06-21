/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.settings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceLocation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public record ClimateSettings(int scale, boolean endlessPoles)
{
    public static final Codec<ClimateSettings> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("scale").forGetter(c -> c.scale),
        Codec.BOOL.fieldOf("endless_poles").forGetter(c -> c.endlessPoles)
    ).apply(instance, ClimateSettings::new));

    private static final Map<ResourceLocation, ClimateSettings> PRESETS = new ConcurrentHashMap<>();

    public static final Codec<ClimateSettings> CODEC = Codec.either(
        ResourceLocation.CODEC,
        DIRECT_CODEC
    ).comapFlatMap(
        e -> e.map(
            id -> Codecs.requireNonNull(PRESETS.get(id), "No climate settings preset for id: " + id),
            DataResult::success
        ),
        Either::right
    );

    public static final ClimateSettings DEFAULT = register("default", new ClimateSettings(20_000, false));

    /**
     * Register a climate settings preset. Used for both temperature and rainfall.
     * This method is safe to call during parallel mod loading.
     */
    public static ClimateSettings register(ResourceLocation id, ClimateSettings preset)
    {
        PRESETS.put(id, preset);
        return preset;
    }

    private static ClimateSettings register(String id, ClimateSettings preset)
    {
        return register(Helpers.identifier(id), preset);
    }
}
