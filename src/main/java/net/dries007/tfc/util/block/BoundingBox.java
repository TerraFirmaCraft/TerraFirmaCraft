/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.block;

import java.util.Arrays;
import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

/**
 * Not Axis Aligned Bounding Box
 * Helper class to compute AABBs for different facings
 * **No more AABBs for each direction** (only NSWE)
 * For performance purposes, this class will create AABBs on demand, and supply the same AABB object if asked again
 */
public class BoundingBox
{
    protected final EnumFacing direction;
    protected final double x, y, z;
    protected final double radiusX;
    protected final double radiusY;
    protected final double radiusZ;

    protected AxisAlignedBB[] values = new AxisAlignedBB[4];

    /**
     * Bounding Box
     * AKA AABB from a single facing
     * To be used in computing other facings
     *
     * @param centerPoint the center position
     * @param radiusX     the X direction radius
     * @param radiusY     the Y direction radius
     * @param radiusZ     the Z direction radius
     * @param direction   the direction this bounding box is being created, or, if this was an AABB, which facing you are creating here
     */
    public BoundingBox(Vec3d centerPoint, double radiusX, double radiusY, double radiusZ, @Nonnull EnumFacing direction)
    {
        this(centerPoint.x, centerPoint.y, centerPoint.z, radiusX, radiusY, radiusZ, direction);
    }

    /**
     * Bounding Box
     * AKA AABB from a single facing
     * To be used in computing other facings
     *
     * @param x         the center X position
     * @param y         the center Y position
     * @param z         the center Z position
     * @param radiusX   the X direction radius
     * @param radiusY   the Y direction radius
     * @param radiusZ   the Z direction radius
     * @param direction the direction this bounding box is being created, or, if this was an AABB, which facing you are creating here
     */
    public BoundingBox(double x, double y, double z, double radiusX, double radiusY, double radiusZ, @Nonnull EnumFacing direction)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
        Arrays.fill(values, null);
    }

    /**
     * Returns an AABB obj from facing
     *
     * @param facing the facing you want to get an AABB for
     * @return an AABB obj, which is computed only once and saved for performance purposes
     */
    @Nonnull
    public AxisAlignedBB getAABB(EnumFacing facing)
    {
        if (values[facing.getHorizontalIndex()] == null)
        {
            if (facing == this.direction)
            {
                // If facing is equal this object's direction, we don't need to compute anything
                values[facing.getHorizontalIndex()] = new AxisAlignedBB(x - radiusX, y - radiusY, z - radiusZ, x + radiusX, y + radiusY, z + radiusZ);
            }
            else
            {
                int rotation = facing.getHorizontalIndex() - direction.getHorizontalIndex();
                if (rotation < 0)
                {
                    rotation += 4;
                }

                // Convert to radians
                double rad = rotation * 90 * (Math.PI / 180);

                // Rotate center position as needed
                double x = Math.cos(rad) * (this.x - 0.5D) - Math.sin(rad) * (this.z - 0.5D) + 0.5D;
                double z = Math.sin(rad) * (this.x - 0.5D) + Math.cos(rad) * (this.z - 0.5D) + 0.5D;

                // Radius is the same if facing is the opposite
                double radiusX = rotation % 2 == 0 ? this.radiusX : this.radiusZ;
                double radiusZ = rotation % 2 == 0 ? this.radiusZ : this.radiusX;

                values[facing.getHorizontalIndex()] = new AxisAlignedBB(x - radiusX, y - radiusY, z - radiusZ, x + radiusX, y + radiusY, z + radiusZ);
            }
        }
        return values[facing.getHorizontalIndex()];
    }
}
