/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TESRBase<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
    // Use this to get vertices for a box from Min - Max point in 3D
    // Pass the string of the axies you want the box to render on ('xz') for no top / bottom, etc.
    // Pass 'xyz' for all vertices
    protected static double[][] getVerticesBySide(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, String axies)
    {
        double[][] ret = new double[][] {};
        if (axies.contains("x"))
        {
            ret = append(ret, getXVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        if (axies.contains("y"))
        {
            ret = append(ret, getYVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        if (axies.contains("z"))
        {
            ret = append(ret, getZVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        return ret;

    }

    protected static double[][] getXVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new double[][] {
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

    protected static double[][] getYVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new double[][] {
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

    protected static double[][] getZVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new double[][] {
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

    protected static double[][] append(double[][] a, double[][] b)
    {
        double[][] result = new double[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    protected static double[][] drawBoxByVertex(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new double[][] {
            {minX, maxY, minZ, 0, 1}, // Top
            {minX, maxY, maxZ, 1, 1},
            {maxX, maxY, maxZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            {minX, minY, maxZ, 0, 1}, // Bottom
            {minX, minY, minZ, 1, 1},
            {maxX, minY, minZ, 1, 0},
            {maxX, minY, maxZ, 0, 0},

            {minX, minY, minZ, 0, 1}, // Main +X Side
            {minX, minY, maxZ, 1, 1},
            {minX, maxY, maxZ, 1, 0},
            {minX, maxY, minZ, 0, 0},

            {maxX, minY, maxZ, 0, 1}, // Main -X Side
            {maxX, minY, minZ, 1, 1},
            {maxX, maxY, minZ, 1, 0},
            {maxX, maxY, maxZ, 0, 0}
        };
    }

    protected static double[][] drawCubeByVertex(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new double[][] {
            {maxX, minY, minZ, 0, 1}, // Main +Z Side
            {minX, minY, minZ, 1, 1},
            {minX, maxY, minZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            {minX, minY, maxZ, 0, 1}, // Main -Z Side
            {maxX, minY, maxZ, 1, 1},
            {maxX, maxY, maxZ, 1, 0},
            {minX, maxY, maxZ, 0, 0}
        };
    }
}
