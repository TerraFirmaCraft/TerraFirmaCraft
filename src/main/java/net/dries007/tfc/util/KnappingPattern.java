/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class KnappingPattern
{
    public static final int MAX_WIDTH = 5;
    public static final int MAX_HEIGHT = 5;

    public static KnappingPattern fromJson(JsonObject json)
    {
        final JsonArray array = json.getAsJsonArray("pattern");
        final boolean empty = GsonHelper.getAsBoolean(json, "outside_slot_required", true);

        final int height = array.size();
        if (height > MAX_HEIGHT) throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        if (height == 0) throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

        final int width = GsonHelper.convertToString(array.get(0), "pattern[ 0 ]").length();
        if (width > MAX_WIDTH) throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");

        final KnappingPattern pattern = new KnappingPattern(width, height, empty);
        for (int r = 0; r < height; ++r)
        {
            String row = GsonHelper.convertToString(array.get(r), "pattern[" + r + "]");
            if (r > 0 && width != row.length()) throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            for (int c = 0; c < width; c++)
            {
                pattern.set(r * width + c, row.charAt(c) != ' ');
            }
        }
        return pattern;
    }

    public static KnappingPattern fromNetwork(FriendlyByteBuf buffer)
    {
        final int width = buffer.readVarInt();
        final int height = buffer.readVarInt();
        final int data = buffer.readInt();
        final boolean empty = buffer.readBoolean();
        return new KnappingPattern(width, height, data, empty);
    }

    private final int width;
    private final int height;
    private final boolean empty;

    private int data; // on = 1, off = 0

    public KnappingPattern()
    {
        this(MAX_WIDTH, MAX_HEIGHT, false);
    }

    public KnappingPattern(int width, int height, boolean empty)
    {
        this(width, height, (1 << (width * height)) - 1, empty);
    }

    private KnappingPattern(int width, int height, int data, boolean empty)
    {
        this.width = width;
        this.height = height;
        this.data = data;
        this.empty = empty;
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
        return empty;
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

    public void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(width);
        buffer.writeVarInt(height);
        buffer.writeInt(data);
        buffer.writeBoolean(empty);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other instanceof KnappingPattern p)
        {
            final int mask = (1 << (width * height)) - 1;
            return width == p.width && height == p.height && empty == p.empty && (data & mask) == (p.data & mask);
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
                    if (get(patternIdx) != other.empty)
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
}