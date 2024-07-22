/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


/**
 * <h2>Knapping Pattern</h2>
 * A Knapping Pattern represents the required pattern that a knapping grid must achieve to complete a recipe. This pattern is a (up to) 5x5 grid of squares,
 * each of which start (default) as <strong>on</strong> and may be clicked <strong>off</strong>. In a pattern, <strong>on</strong> is represented as
 * anything non-space, for example:
 * <pre>
 * " #   ",
 * "#### ",
 * "#####",
 * "#### ",
 * " #   "
 * </pre>
 * This represents the typical pattern for an axe head. The squares not marked <strong>on</strong> must be clicked.
 * <p>
 * Note that knapping patterns may be smaller than the ultimate knapping grid, for example:
 * <pre>
 * "X   X",
 * "XXXXX",
 * " XXX "
 * </pre>
 * In this case, the knapping pattern implicitly includes all other rows, depending on the {@code default_on} value, where {@code true} indicates this
 * default must be <strong>on</strong>, and if {@code false} - as would make sense in the above recipe - indicates the default must be <strong>off</strong>.
 *
 * <h3>Implementation</h3>
 * This uses a fixed-size bitset to encode the pattern internally. A {@code true} value indicates the value is <strong>on</strong> at that position.
 * The bitset is variable width, to allow expressing smaller than 5x5 patterns.
 * <p>
 * N.B. A 5x5 pattern should not need to declare {@code default_on}, as the limit for knapping recipes is 5x5
 */
public final class KnappingPattern
{
    public static final int MAX_WIDTH = 5;
    public static final int MAX_HEIGHT = 5;

    public static final MapCodec<KnappingPattern> CODEC = RecordCodecBuilder.<Prototype>mapCodec(i -> i.group(
        Codec.STRING.listOf(1, 5).fieldOf("pattern").forGetter(c -> c.pattern),
        Codec.BOOL.optionalFieldOf("default_on").forGetter(c -> c.defaultOn)
    ).apply(i, Prototype::new)).flatXmap(KnappingPattern::readPattern, KnappingPattern::writePattern);

    public static final StreamCodec<ByteBuf, KnappingPattern> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.width,
        ByteBufCodecs.VAR_INT, c -> c.height,
        ByteBufCodecs.INT, c -> c.data,
        ByteBufCodecs.BOOL, c -> c.defaultOn,
        KnappingPattern::new
    );

    public static KnappingPattern from(boolean defaultOn, String... pattern)
    {
        return readPattern(new Prototype(List.of(pattern), Optional.of(defaultOn))).getOrThrow();
    }

    private static DataResult<KnappingPattern> readPattern(Prototype proto)
    {
        final int height = proto.pattern.size();
        if (height == 0 || height > MAX_HEIGHT) return DataResult.error(() -> "Invalid pattern: must have [1, " + MAX_HEIGHT + "] rows");

        final int width = proto.pattern.getFirst().length();
        if (width == 0 || width > MAX_WIDTH) return DataResult.error(() -> "Invalid pattern: must have [1, " + MAX_WIDTH + "] columns");
        if ((height != MAX_HEIGHT || width != MAX_HEIGHT) && proto.defaultOn.isEmpty()) return DataResult.error(() -> "default_on is required if the pattern is not " + MAX_WIDTH + "x" + MAX_HEIGHT);

        final KnappingPattern pattern = new KnappingPattern(width, height, proto.defaultOn.orElse(false));
        for (int r = 0; r < height; ++r)
        {
            String row = proto.pattern.get(r);
            if (r > 0 && width != row.length()) return DataResult.error(() -> "Invalid pattern: each row must be the same width");
            for (int c = 0; c < width; c++)
            {
                pattern.set(r * width + c, row.charAt(c) != ' '); // on = anything that isn't ' '
            }
        }
        return DataResult.success(pattern);
    }

    private static DataResult<Prototype> writePattern(KnappingPattern pattern)
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
        return DataResult.success(new Prototype(array, pattern.height == MAX_HEIGHT && pattern.width == MAX_WIDTH ? Optional.empty() : Optional.of(pattern.defaultOn)));
    }

    private final int width;
    private final int height;
    private final boolean defaultOn;

    /**
     * The bitset containing the on/off data. Indexed by [x + y * width]
     */
    private int data; // on = 1, off = 0

    public KnappingPattern()
    {
        this(MAX_WIDTH, MAX_HEIGHT, false);
    }

    public KnappingPattern(int width, int height, boolean defaultOn)
    {
        this(width, height, (1 << (width * height)) - 1, defaultOn);
    }

    private KnappingPattern(int width, int height, int data, boolean defaultOn)
    {
        this.width = width;
        this.height = height;
        this.data = data;
        this.defaultOn = defaultOn;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
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
            return width == p.width && height == p.height && defaultOn == p.defaultOn && (data & mask) == (p.data & mask);
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
                    if (get(patternIdx) != other.defaultOn)
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

    /**
     * @param defaultOn The value of {@code defaultOn}, or {@code Optional.empty()} to indicate the pattern is full-size and it should not be written
     */
    record Prototype(List<String> pattern, Optional<Boolean> defaultOn) {}
}