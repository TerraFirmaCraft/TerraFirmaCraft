/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.dries007.tfc.util.JsonHelpers;

/**
 * An immutable collection of data about a certain piece of food.
 *
 * @param hunger Hunger amount. In TFC, it is almost always 4.
 * @param saturation Saturation, only provided by some basic foods and meal bonuses.
 * @param water Water, provided by some foods.
 * @param nutrients The nutrition values, indexed by {@link Nutrient#ordinal()}
 * @param decayModifier Decay modifier - higher = shorter decay.
 */
public record FoodData(
    int hunger,
    float water,
    float saturation,
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

    public static final Codec<FoodData> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.fieldOf("hunger").forGetter(c -> c.hunger),
        Codec.FLOAT.fieldOf("water").forGetter(c -> c.water),
        Codec.FLOAT.fieldOf("saturation").forGetter(c -> c.saturation),
        NUTRITION_CODEC.forGetter(c -> c.nutrients),
        Codec.FLOAT.fieldOf("decay_modifier").forGetter(c -> c.decayModifier)
    ).apply(i, FoodData::new));

    public static final StreamCodec<ByteBuf, FoodData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.hunger,
        ByteBufCodecs.FLOAT, c -> c.water,
        ByteBufCodecs.FLOAT, c -> c.saturation,
        NUTRITION_STREAM_CODEC, c -> c.nutrients,
        ByteBufCodecs.FLOAT, c -> c.decayModifier,
        FoodData::new
    );

    /** An empty instance of a {@link FoodData} with no values */
    public static final FoodData EMPTY = new FoodData(0, 0, 0, new float[] {0, 0, 0, 0, 0}, 0);

    private static RecordCodecBuilder<float[], Float> nutrientCodec(Nutrient nutrient)
    {
        return Codec.FLOAT.optionalFieldOf(nutrient.getSerializedName(), 0f).forGetter(c -> c[nutrient.ordinal()]);
    }

    public static FoodData of(int hunger, float water, float saturation, float[] nutrients, float decayModifier)
    {
        return new FoodData(hunger, water, saturation, nutrients, decayModifier);
    }

    public static FoodData decayOnly(float decayModifier)
    {
        return new FoodData(0, 0, 0, new float[] {0, 0, 0, 0, 0}, decayModifier);
    }


    public float nutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    public float[] nutrients()
    {
        return nutrients.clone();
    }


    // Old

    public static FoodData decode(FriendlyByteBuf buffer)
    {
        final int hunger = buffer.readVarInt();
        final float saturation = buffer.readFloat();
        final float water = buffer.readFloat();
        final float decayModifier = buffer.readFloat();

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = buffer.readFloat();
        }

        return FoodData.of(hunger, water, saturation, nutrition, decayModifier);
    }

    public static FoodData read(JsonObject json)
    {
        final int hunger = JsonHelpers.getAsInt(json, "hunger", 4);
        final float saturation = JsonHelpers.getAsFloat(json, "saturation", 0);
        final float water = JsonHelpers.getAsFloat(json, "water", 0);
        final float decayModifier = JsonHelpers.getAsFloat(json, "decay_modifier", 1);

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = JsonHelpers.getAsFloat(json, nutrient.getSerializedName(), 0);
        }

        return FoodData.of(hunger, water, saturation, nutrition, decayModifier);
    }

    public static FoodData read(CompoundTag nbt)
    {
        return new FoodData(
            nbt.getInt("food"),
            nbt.getFloat("water"),
            nbt.getFloat("sat"),
            nbt.getFloat("grain"),
            nbt.getFloat("fruit"),
            nbt.getFloat("veg"),
            nbt.getFloat("meat"),
            nbt.getFloat("dairy"),
            nbt.getFloat("decay")
        );
    }

    public CompoundTag write()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putInt("food", hunger);
        nbt.putFloat("sat", saturation);
        nbt.putFloat("water", water);
        nbt.putFloat("decay", decayModifier);
        nbt.putFloat("grain", grain);
        nbt.putFloat("veg", vegetables);
        nbt.putFloat("fruit", fruit);
        nbt.putFloat("meat", protein);
        nbt.putFloat("dairy", dairy);
        return nbt;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(hunger);
        buffer.writeFloat(saturation);
        buffer.writeFloat(water);
        buffer.writeFloat(decayModifier);

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            buffer.writeFloat(nutrient(nutrient));
        }
    }
}
