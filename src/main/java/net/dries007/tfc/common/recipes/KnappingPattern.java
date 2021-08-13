/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

/**
 * A simple craft matrix for knapping / leather or clay working
 */
public class KnappingPattern
{
    private static final int MAX_WIDTH = 5;
    private static final int MAX_HEIGHT = 5;
    private static final int MAX_AREA = MAX_WIDTH * MAX_HEIGHT;

    /**
     * This is the actual craft matrix
     * true = full
     * false = empty
     */
    private final boolean[] matrix;
    public final boolean outsideSlot;
    private final int width;
    private final int height;
    private final int area;

    /**
     * Create a empty max size craft matrix
     */
    public KnappingPattern()
    {
        this.width = MAX_WIDTH;
        this.height = MAX_HEIGHT;
        this.area = MAX_AREA;
        this.matrix = new boolean[MAX_AREA];
        this.outsideSlot = false;
        Arrays.fill(matrix, true);
    }

    /**
     * Create a patterned matrix based on a boolean input.
     *
     * @param outsideSlotRequired If the recipe is smaller than MAX_WIDTH x MAX_HEIGHT, what is the slot outside of the recipe required to be?
     *                            true = outside slots need to be full
     *                            false = outside slots need to be empty
     * @param matrix              The actual matrix of booleans in order
     * @param height              Height of the matrix
     * @param width               Width of the matrix
     */
    public KnappingPattern(boolean outsideSlotRequired, boolean[] matrix, int width, int height)
    {
        this.height = height;
        this.width = width;
        this.area = width * height;
        this.matrix = matrix;
        this.outsideSlot = outsideSlotRequired;
    }

    public void setAll(boolean value)
    {
        for (int i = 0; i < width * height; i++)
            set(i, value);
    }

    public void set(int xPos, int yPos, boolean value)
    {
        set(xPos + yPos * width, value);
    }

    public void set(int index, boolean value)
    {
        if (index >= 0 && index < area) matrix[index] = value;
    }

    public boolean get(int xPos, int yPos)
    {
        return get(xPos + yPos * width);
    }

    public boolean get(int index)
    {
        return index >= 0 && index < area && matrix[index];
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    /**
     * @param other Another craft matrix
     * @return if the matrices are identical. Not used for checking if recipe matches
     */
    public boolean isEqual(KnappingPattern other)
    {
        if (other.width != this.width || other.height != this.height)
            return false;
        for (int i = 0; i < width * height; i++)
        {
            if (other.matrix[i] != this.matrix[i])
                return false;
        }
        return true;
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
        for (int xShift = 0; xShift <= this.width - other.width; xShift++)
        {
            for (int yShift = 0; yShift <= this.height - other.height; yShift++)
            {
                if (matches(other, xShift, yShift, false))
                {
                    return true;
                }
                else if (matches(other, xShift, yShift, true))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(KnappingPattern other, int startX, int startY, boolean isMirrored)
    {
        for (int x = 0; x < this.width; x++)
        {
            for (int y = 0; y < this.height; y++)
            {
                int patternIdx = y * width + x;
                if (x < startX || y < startY || x - startX >= other.width || y - startY >= other.height)
                {
                    // If the current position in the matrix is outside the pattern, the value should be set by other.outsideSlot
                    if (matrix[patternIdx] != other.outsideSlot)
                        return false;
                }
                else
                {
                    // Otherwise, the value must equal the value in the pattern
                    int otherIdx;
                    if (isMirrored)
                    {
                        otherIdx = (y - startY) * other.width + (other.width - 1 - (x - startX));
                    }
                    else
                    {
                        otherIdx = (y - startY) * other.width + (x - startX);
                    }

                    if (matrix[patternIdx] != other.matrix[otherIdx])
                        return false;
                }
            }
        }
        return true;
    }

    public void toNetwork(PacketBuffer buffer, int width, int height)
    {
        buffer.writeVarInt(width);
        buffer.writeVarInt(height);
        short packed = 0;
        for (int i = 0; i < matrix.length; i++)
            if (matrix[i]) packed |= (1 << i);
        buffer.writeShort(packed);
    }

    public static KnappingPattern fromNetwork(PacketBuffer buffer, boolean outsideSlotRequired)
    {
        int width = buffer.readVarInt();
        int height = buffer.readVarInt();
        boolean[] matrix = new boolean[width * height];

        short packed = buffer.readShort();
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = (packed & (1 << i)) != 0;
        return new KnappingPattern(outsideSlotRequired, matrix, width, height);
    }

    public static KnappingPattern fromJson(JsonObject json)
    {
        JsonArray array = json.getAsJsonArray("pattern");
        boolean outsideSlotRequired = JSONUtils.getAsBoolean(json, "outside_slot_required", true);

        int height = array.size();
        if (height > MAX_HEIGHT) throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        if (height == 0) throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

        int width = JSONUtils.convertToString(array.get(0), "pattern[ 0 ]").length();
        if (width > MAX_WIDTH) throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");

        boolean[] matrix = new boolean[width * height];
        for (int i = 0; i < height; ++i)
        {
            String s = JSONUtils.convertToString(array.get(i), "pattern[" + i + "]");
            if (i > 0 && width != s.length()) throw new JsonSyntaxException("Invalid pattern: each row must be the same width");

            for (int c = 0; c < width; c++)
            {
                matrix[i * width + c] = (s.charAt(c) != ' ');
            }
        }
        return new KnappingPattern(outsideSlotRequired, matrix, width, height);
    }
}