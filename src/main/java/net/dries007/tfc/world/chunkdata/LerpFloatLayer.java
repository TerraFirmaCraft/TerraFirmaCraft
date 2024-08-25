/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.dries007.tfc.util.Helpers;

/**
 * This represents an interpolated square of floating point values, which are known at the grid points and interpolated at delta values inbetween
 * each point. The values are named as `valueXZ`, where `0` and `1` indicate the low or high points on the grid square, respectively.
 */
public record LerpFloatLayer(
    float value00,
    float value01,
    float value10,
    float value11
) {
    public static final StreamCodec<ByteBuf, LerpFloatLayer> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.value00,
        ByteBufCodecs.FLOAT, c -> c.value01,
        ByteBufCodecs.FLOAT, c -> c.value10,
        ByteBufCodecs.FLOAT, c -> c.value11,
        LerpFloatLayer::new
    );

    public LerpFloatLayer(CompoundTag nbt)
    {
        this(
            nbt.getFloat("00"),
            nbt.getFloat("01"),
            nbt.getFloat("10"),
            nbt.getFloat("11")
        );
    }

    /**
     * Gets the floating point value approximated at the point within the grid.
     *
     * @param deltaX A distance in the X direction.
     * @param deltaZ A distance in the Z direction.
     */
    public float getValue(float deltaX, float deltaZ)
    {
        return Helpers.lerp4(value00, value01, value10, value11, deltaX, deltaZ);
    }

    /**
     * This acts as a function, when given an original layer, and a sub-square within the original grid, returns a new layer
     * composed of just the sub-square. The sub-square is defined by an origin {@code (originX, originZ)} and a width {@code width}.
     *
     * @param originX The origin of the sub-square, in {@code [0, 1]}
     * @param originZ The origin of the sub-square, in {@code [0, 1]}
     * @param width   The square width of the sub-square, in {@code [0, 1]}
     * @return A new {@code LerpFloatLayer} with the modified values.
     */
    public LerpFloatLayer scaled(float originX, float originZ, float width)
    {
        return new LerpFloatLayer(
            Helpers.lerp4(value00, value01, value10, value11, originX, originZ),
            Helpers.lerp4(value00, value01, value10, value11, originX, originZ + width),
            Helpers.lerp4(value00, value01, value10, value11, originX + width, originZ),
            Helpers.lerp4(value00, value01, value10, value11, originX + width, originZ + width)
        );
    }

    /**
     * Applies {@code point} to each corner of the layer
     * @return A new {@code LerpFloatLayer} after the function has been applied
     */
    public LerpFloatLayer apply(FloatUnaryOperator point)
    {
        return new LerpFloatLayer(
            point.apply(value00),
            point.apply(value01),
            point.apply(value10),
            point.apply(value11)
        );
    }

    public CompoundTag write()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putFloat("00", value00);
        nbt.putFloat("01", value01);
        nbt.putFloat("10", value10);
        nbt.putFloat("11", value11);
        return nbt;
    }
}