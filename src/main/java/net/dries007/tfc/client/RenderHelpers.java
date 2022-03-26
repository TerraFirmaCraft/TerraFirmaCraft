/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderHelpers
{
    public static void setShaderColor(int color)
    {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
    }

    // Use this to get vertices for a box from Min - Max point in 3D
    // Pass the string of the axies you want the box to render on ('xz') for no top / bottom, etc.
    // Pass 'xyz' for all vertices
    public static float[][] getVerticesBySide(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, String axes)
    {
        float[][] ret = new float[][] {};
        if (axes.contains("x"))
        {
            ret = append(ret, getXVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        if (axes.contains("y"))
        {
            ret = append(ret, getYVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        if (axes.contains("z"))
        {
            ret = append(ret, getZVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        return ret;

    }

    public static float[][] getXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, minY, minZ, 0, 1}, // Main +X Side
            {minX, minY, maxZ, 1, 1},
            {minX, maxY, maxZ, 1, 0},
            {minX, maxY, minZ, 0, 0},

            {maxX, minY, maxZ, 1, 0}, // Main -X Side
            {maxX, minY, minZ, 0, 0},
            {maxX, maxY, minZ, 0, 1},
            {maxX, maxY, maxZ, 1, 1}
        };
    }

    public static float[][] getYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, maxY, minZ, 0, 1}, // Top
            {minX, maxY, maxZ, 1, 1},
            {maxX, maxY, maxZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            {minX, minY, maxZ, 1, 0}, // Bottom
            {minX, minY, minZ, 0, 0},
            {maxX, minY, minZ, 0, 1},
            {maxX, minY, maxZ, 1, 1}
        };
    }

    public static float[][] getZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {maxX, minY, minZ, 0, 1}, // Main +Z Side
            {minX, minY, minZ, 1, 1},
            {minX, maxY, minZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            {minX, minY, maxZ, 1, 0}, // Main -Z Side
            {maxX, minY, maxZ, 0, 0},
            {maxX, maxY, maxZ, 0, 1},
            {minX, maxY, maxZ, 1, 1}
        };
    }

    public static float[][] append(float[][] a, float[][] b)
    {
        float[][] result = new float[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
