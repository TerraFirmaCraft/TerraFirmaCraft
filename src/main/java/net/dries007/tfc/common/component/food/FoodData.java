/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


/**
 * An immutable collection of data about a certain piece of food.
 *
 * @param hunger Hunger amount. In TFC, it is almost always 4.
 * @param saturation Saturation, only provided by some basic foods and meal bonuses.
 * @param water Water, provided by some foods.
 * @param intoxication An amount of ticks to register as intoxicated
 * @param nutrients The nutrition values, indexed by {@link Nutrient#ordinal()}
 * @param decayModifier Decay modifier - higher = shorter decay.
 */
public record FoodData(
    int hunger,
    float water,
    float saturation,
    int intoxication,
    float[] nutrients,
    float decayModifier
) {
    /**
     * A codec with optional named nutrition values, which serializes to a {@code float[]}, indexed by {@link Nutrient#ordinal()}
     */
    public static final MapCodec<float[]> NUTRITION_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        nutrientCodec(Nutrient.GRAIN),
        nutrientCodec(Nutrient.FRUIT),
        nutrientCodec(Nutrient.VEGETABLES),
        nutrientCodec(Nutrient.PROTEIN),
        nutrientCodec(Nutrient.DAIRY)
    ).apply(i, (t1, t2, t3, t4, t5) -> new float[]{t1, t2, t3, t4, t5}));

    public static final StreamCodec<ByteBuf, float[]> NUTRITION_STREAM_CODEC = StreamCodec.of(
        (buffer, value) -> {
            for (float t : value)
                buffer.writeFloat(t);
        },
        buffer -> {
            final float[] value = new float[Nutrient.TOTAL];
            for (int i = 0; i < value.length; i++)
                value[i] = buffer.readFloat();
            return value;
        }
    );

    private static RecordCodecBuilder<float[], Float> nutrientCodec(Nutrient nutrient)
    {
        return Codec.FLOAT.optionalFieldOf(nutrient.getSerializedName(), 0f).forGetter(c -> c[nutrient.ordinal()]);
    }

    public static final MapCodec<FoodData> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.INT.optionalFieldOf("hunger", 0).forGetter(c -> c.hunger),
        Codec.FLOAT.optionalFieldOf("water", 0f).forGetter(c -> c.water),
        Codec.FLOAT.optionalFieldOf("saturation", 0f).forGetter(c -> c.saturation),
        Codec.INT.optionalFieldOf("intoxication", 0).forGetter(c -> c.intoxication),
        NUTRITION_CODEC.forGetter(c -> c.nutrients),
        Codec.FLOAT.optionalFieldOf("decay_modifier", 0f).forGetter(c -> c.decayModifier)
    ).apply(i, FoodData::new));
    public static final Codec<FoodData> CODEC = MAP_CODEC.codec();
    public static final Codec<List<FoodData>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<ByteBuf, FoodData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.hunger,
        ByteBufCodecs.FLOAT, c -> c.water,
        ByteBufCodecs.FLOAT, c -> c.saturation,
        ByteBufCodecs.VAR_INT, c -> c.intoxication,
        NUTRITION_STREAM_CODEC, c -> c.nutrients,
        ByteBufCodecs.FLOAT, c -> c.decayModifier,
        FoodData::new
    );

    /** An empty instance of a {@link FoodData} with no values, but infinite default expiry time */
    public static final FoodData EMPTY = of(0f);
    public static final FoodData MILK = of(0, 0).dairy(2f);
    public static final FoodData CAKE = of(2, 2f).grain(0.8f).dairy(0.5f);

    /**
     * Creates a new {@link FoodData} with just the provided decay modifier and no other nutritional value.
     */
    public static FoodData of(float decayModifier)
    {
        return ofFood(0, 0, decayModifier);
    }

    /**
     * Creates a new {@link FoodData} with the provided {@code hunger} and {@code saturation} values, no decay modifier and default values otherwise.
     */
    public static FoodData of(int hunger, float saturation)
    {
        return new FoodData(hunger, 0f, saturation, 0, new float[5], 0);
    }

    /**
     * Creates a new {@link FoodData} with a default {@code hunger = 4}, and the provided {@code saturation}, {@code water}, and {@code decayModifier}.
     */
    public static FoodData ofFood(float saturation, float water, float decayModifier)
    {
        return ofFood(4, saturation, water, decayModifier);
    }

    /**
     * Creates a new {@link FoodData} with the provided values, and no additional nutrition.
     */
    public static FoodData ofFood(int hunger, float saturation, float water, float decayModifier)
    {
        return new FoodData(hunger, water, saturation, 0, new float[5], decayModifier);
    }

    /**
     * Creates a new {@link FoodData} for a (typical) drink with the given {@code water} and {@code intoxication} values.
     */
    public static FoodData ofDrink(float water, int intoxication)
    {
        return new FoodData(0, water, 0, intoxication, new float[5], 0);
    }

    public float nutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    public float[] nutrients()
    {
        return nutrients.clone();
    }

    public FoodData grain(float value) { return with(Nutrient.GRAIN, value); }
    public FoodData vegetables(float value) { return with(Nutrient.VEGETABLES, value); }
    public FoodData fruit(float value) { return with(Nutrient.FRUIT, value); }
    public FoodData protein(float value) { return with(Nutrient.PROTEIN, value); }
    public FoodData dairy(float value) { return with(Nutrient.DAIRY, value); }

    /**
     * @return A new {@link FoodData} with values multiplied by the amount consumed
     */
    public FoodData mul(float multiplier)
    {
        return new FoodData(
            (int) (hunger * multiplier),
            water * multiplier,
            saturation * multiplier,
            intoxication,
            mul(nutrients, multiplier),
            decayModifier
        );
    }

    /**
     * Mutates the current {@code FoodData}, setting the nutrient to the provided value. <strong>Do not use</strong> in contexts where
     * the food data is considered immutable! This is used instead of a builder for cases like building food for data generation!
     */
    public FoodData with(Nutrient nutrient, float value)
    {
        nutrients[nutrient.ordinal()] = value;
        return this;
    }

    private static float[] mul(float[] input, float multiplier)
    {
        final float[] output = new float[input.length];
        for (int i = 0; i < output.length; i++)
            output[i] = input[i] * multiplier;
        return output;
    }
}
