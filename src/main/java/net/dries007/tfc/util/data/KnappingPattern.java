/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class KnappingPattern
{
    public static final int MAX_WIDTH = 5;
    public static final int MAX_HEIGHT = 5;

    public static final Codec<KnappingPattern> CODEC = RecordCodecBuilder.<Prototype>create(i -> i.group(
        Codec.STRING.listOf(1, 5).fieldOf("pattern").forGetter(c -> c.pattern),
        Codec.BOOL.fieldOf("outside_slot_required").forGetter(c -> c.outsideSlotRequired)
    ).apply(i, Prototype::new)).comapFlatMap(KnappingPattern::readPattern, KnappingPattern::writePattern);

    public static final StreamCodec<ByteBuf, KnappingPattern> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.width,
        ByteBufCodecs.VAR_INT, c -> c.height,
        ByteBufCodecs.INT, c -> c.data,
        ByteBufCodecs.BOOL, c -> c.outsideSlotRequired,
        KnappingPattern::new
    );

    private static DataResult<KnappingPattern> readPattern(Prototype proto)
    {
        final int height = proto.pattern.size();
        if (height == 0 || height > MAX_HEIGHT) return DataResult.error(() -> "Invalid pattern: must have [1, 5] rows");

        final int width = proto.pattern.getFirst().length();
        if (width == 0 || width > MAX_WIDTH) return DataResult.error(() -> "Invalid pattern: must have [1, 5] columns");

        final KnappingPattern pattern = new KnappingPattern(width, height, proto.outsideSlotRequired);
        for (int r = 0; r < height; ++r)
        {
            String row = proto.pattern.get(r);
            if (r > 0 && width != row.length()) return DataResult.error(() -> "Invalid pattern: each row must be the same width");
            for (int c = 0; c < width; c++)
            {
                pattern.set(r * width + c, row.charAt(c) != ' ');
            }
        }
        return DataResult.success(pattern);
    }

    private static Prototype writePattern(KnappingPattern pattern)
    {
        final List<String> array = new ArrayList<>();
        for (int r = 0; r < pattern.height; ++r)
        {
            final StringBuilder row = new StringBuilder();
            for (int c = 0; c < pattern.width; c++)
            {
                row.append(pattern.get(r * pattern.width + c) ? '#' : ' ');
            }
            array.add(row.toString());
        }
        return new Prototype(array, pattern.outsideSlotRequired);
    }

    private final int width;
    private final int height;
    private final boolean outsideSlotRequired;

    private int data; // on = 1, off = 0

    public KnappingPattern()
    {
        this(MAX_WIDTH, MAX_HEIGHT, false);
    }

    public KnappingPattern(int width, int height, boolean outsideSlotRequired)
    {
        this(width, height, (1 << (width * height)) - 1, outsideSlotRequired);
    }

    private KnappingPattern(int width, int height, int data, boolean outsideSlotRequired)
    {
        this.width = width;
        this.height = height;
        this.data = data;
        this.outsideSlotRequired = outsideSlotRequired;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean isOutsideSlotRequired()
    {
        return outsideSlotRequired;
    }

    public void setAll(boolean value)
    {
        data = value ? (1 << (width * height)) - 1 : 0;
    }

    public void set(int x, int y, boolean value)
    {
        set(x + y * width, value);
    }

    public void set(int index, boolean value)
    {
        assert index >= 0 && index < 32;
        if (value)
        {
            data |= 1 << index;
        }
        else
        {
            data &= ~(1 << index);
        }
    }

    public boolean get(int x, int y)
    {
        return get(x + y * width);
    }

    public boolean get(int index)
    {
        assert index >= 0 && index < 32;
        return ((data >> index) & 0b1) == 1;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other instanceof KnappingPattern p)
        {
            final int mask = (1 << (width * height)) - 1;
            return width == p.width && height == p.height && outsideSlotRequired == p.outsideSlotRequired && (data & mask) == (p.data & mask);
        }
        return false;
    }

    /**
     * Used to check if a craft matrix matches another one.
     *
     * @param other Another craft matrix
     * @return if 'other' is a subset of the current craft matrix (i.e. other is found somewhere within the current matrix)
     */
    public boolean matches(KnappingPattern other)
    {
        // Check all possible shifted positions
        for (int dx = 0; dx <= this.width - other.width; dx++)
        {
            for (int dy = 0; dy <= this.height - other.height; dy++)
            {
                if (matches(other, dx, dy, false) || matches(other, dx, dy, true))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(KnappingPattern other, int startX, int startY, boolean mirror)
    {
        for (int x = 0; x < this.width; x++)
        {
            for (int y = 0; y < this.height; y++)
            {
                int patternIdx = y * width + x;
                if (x < startX || y < startY || x - startX >= other.width || y - startY >= other.height)
                {
                    // If the current position in the matrix is outside the pattern, the value should be set by other.empty
                    if (get(patternIdx) != other.outsideSlotRequired)
                    {
                        return false;
                    }
                }
                else
                {
                    // Otherwise, the value must equal the value in the pattern
                    int otherIdx;
                    if (mirror)
                    {
                        otherIdx = (y - startY) * other.width + (other.width - 1 - (x - startX));
                    }
                    else
                    {
                        otherIdx = (y - startY) * other.width + (x - startX);
                    }

                    if (get(patternIdx) != other.get(otherIdx))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    record Prototype(List<String> pattern, boolean outsideSlotRequired) {}
}