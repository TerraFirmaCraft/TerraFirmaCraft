package net.dries007.tfc.common.recipes;

import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

import net.dries007.tfc.TerraFirmaCraft;

/**
 * A simple craft matrix for knapping / leather or clay working
 *
 * @author AlcatrazEscapee
 */
public class SimpleCraftMatrix
{
    private static final int MAX_WIDTH = 5;
    private static final int MAX_HEIGHT = 5;
    private static final int MAX_AREA = MAX_WIDTH * MAX_HEIGHT;

    private static void logMatrix(boolean[] matrix)
    {
        StringBuilder b = new StringBuilder();
        for (boolean m : matrix) b.append(m ? "X" : " ");
        TerraFirmaCraft.LOGGER.debug("Matrix: {" + b + "}");
    }

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
    public SimpleCraftMatrix()
    {
        this.width = MAX_WIDTH;
        this.height = MAX_HEIGHT;
        this.area = MAX_AREA;
        this.matrix = new boolean[MAX_AREA];
        this.outsideSlot = false;
        Arrays.fill(matrix, true);
    }

    /**
     * Create a patterned matrix based on a string pattern input.
     *
     * @param outsideSlotRequired If the recipe is smaller than MAX_WIDTH x MAX_HEIGHT, what is the slot outside of the recipe required to be?
     *                            true = outside slots need to be full
     *                            false = outside slots need to be empty
     * @param pattern             A list of strings. Each string is a row, each character is an element. ' ' represents empty, anything else is full
     */
    public SimpleCraftMatrix(boolean outsideSlotRequired, String... pattern)
    {
        if (pattern.length == 0 || pattern.length > MAX_HEIGHT)
            throw new IllegalArgumentException("Pattern height is invalid");

        this.height = pattern.length;
        this.width = pattern[0].length();
        this.area = width * height;
        this.matrix = new boolean[width * height];
        this.outsideSlot = outsideSlotRequired;
        if (width > MAX_WIDTH)
            throw new IllegalArgumentException("Pattern width is invalid");

        for (int i = 0; i < height; i++)
        {
            String line = pattern[i];
            if (line.length() != width)
                throw new IllegalArgumentException("Line " + i + " in the pattern has the incorrect length");
            for (int c = 0; c < width; c++)
                this.matrix[i * width + c] = (line.charAt(c) != ' ');
        }
    }

    public SimpleCraftMatrix(boolean outsideSlotRequired, boolean[] matrix, int width, int height)
    {
        if (matrix.length == 0 || matrix.length > MAX_HEIGHT)
            throw new IllegalArgumentException("Pattern height is invalid");

        this.height = height;
        this.width = width;
        this.area = width * height;
        this.matrix = matrix;
        this.outsideSlot = outsideSlotRequired;
        if (width > MAX_WIDTH)
            throw new IllegalArgumentException("Pattern width is invalid");
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
    public boolean isEqual(SimpleCraftMatrix other)
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
    public boolean matches(SimpleCraftMatrix other)
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

    private boolean matches(SimpleCraftMatrix other, int startX, int startY, boolean isMirrored)
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
                        otherIdx = (y - startY) * other.width + (other.width - 1 - (x - startX));
                    else
                        otherIdx = (y - startY) * other.width + (x - startX);

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

    public static SimpleCraftMatrix fromNetwork(PacketBuffer buffer, boolean outsideSlotRequired)
    {
        int width = buffer.readVarInt();
        int height = buffer.readVarInt();
        boolean[] matrix = new boolean[width * height];

        short packed = buffer.readShort();
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = (packed & (1 << i)) != 0;
        return new SimpleCraftMatrix(outsideSlotRequired, matrix, width, height);
    }

    /**
     * From {@link net.minecraft.item.crafting.ShapedRecipe}
     */
    public static String[] patternFromJson(JsonArray array)
    {
        String[] strings = new String[array.size()];
        if (strings.length > MAX_HEIGHT)
        {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        }
        else if (strings.length == 0)
        {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        else
        {
            for (int i = 0; i < strings.length; ++i)
            {
                String s = JSONUtils.convertToString(array.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH)
                {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && strings[0].length() != s.length())
                {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = s;
            }

            return strings;
        }
    }
}