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

public record ClimateSettings(float firstMax, float secondMax, float thirdMax, float fourthMax, int scale, boolean endlessPoles)
{
    public static final Codec<ClimateSettings> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("first_max").forGetter(c -> c.firstMax),
        Codec.FLOAT.fieldOf("second_max").forGetter(c -> c.secondMax),
        Codec.FLOAT.fieldOf("third_max").forGetter(c -> c.thirdMax),
        Codec.FLOAT.fieldOf("fourth_max").forGetter(c -> c.fourthMax),
        Codec.INT.fieldOf("scale").forGetter(c -> c.scale),
        Codec.BOOL.fieldOf("endless_poles").forGetter(c -> c.endlessPoles)
    ).apply(instance, ClimateSettings::new));
    private static final Map<ResourceLocation, ClimateSettings> PRESETS = new ConcurrentHashMap<>();
    public static final Codec<ClimateSettings> CODEC = Codec.either(
        ResourceLocation.CODEC,
        DIRECT_CODEC
    ).comapFlatMap(
        e -> e.map(
            id -> Codecs.resultFromNullable(PRESETS.get(id), "No climate settings preset for id: " + id),
            DataResult::success
        ),
        Either::right
    );

    public static final ClimateSettings DEFAULT_TEMPERATURE = register("default_temperature", new ClimateSettings(-17.25f, -3.75f, 9.75f, 23.25f, 20_000, false));
    public static final ClimateSettings DEFAULT_RAINFALL = register("default_rainfall", new ClimateSettings(125, 200, 300, 375, 20_000, false));

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
