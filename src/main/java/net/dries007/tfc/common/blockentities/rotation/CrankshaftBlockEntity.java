/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.util.network.RotationOwner;

public class CrankshaftBlockEntity extends TFCBlockEntity
{
    /**
     * The radius (in pixels / 16f) from the center of the wheel, to the center of the connecting point with the shaft.
     */
    public static final float WHEEL_RADIUS = 4f / 16f;
    public static final float ARM_LENGTH = 12 / 16f;

    /**
     * The piston length is based on the wheel radius, as we want at max retraction, it to be exactly at the border between blocks.
     * |piston| = O + R - armLength
     */
    public static final float PISTON_LENGTH = 1.5f + WHEEL_RADIUS - ARM_LENGTH;

    public static ShaftMovement calculateShaftMovement(float rotationAngle)
    {
        final int quadrant = Mth.clamp((int) (rotationAngle / Mth.HALF_PI), 0, 3);

        final float unitCircleAngle = quadrant == 0 || quadrant == 3
            ? rotationAngle
            : (quadrant == 1 ? Mth.PI - rotationAngle : rotationAngle - Mth.PI);

        // Where:
        //   O := The center of the circle with radius R
        //        The radius is the distance to the _center_ of the connecting point - not the whole radius of the wheel
        //   H := The center of the connecting box
        //   A := The point where the line OH intersects the circle
        //   B := A point on the radius of the circle s.t. the angle HOB is ~45° - call this angle theta
        //   C := A point on the line OH s.t. angles OCB and HCB are 90°
        //   E := A point on the edge of the shaft block (where a bellows would be, and at 0 in the z axis
        //
        // Then, relative to the shaft block's origin, where the direction conventions are:
        //   +x := Into the page (The axis of the connected axle)
        //   +y := Up
        //   +z := Left (the axis of the shaft)
        //
        // We have the following positions:
        //
        // O = (0.5, 0.5, 1.5)
        //   Note that the X value describes the point touching the wheel, but the midpoint of the shaft is actually 0.5 + armRadius
        //
        // |OB| = |OA| = R
        // |BH| = armLength, but the length between connecting points (so total length rendered += 2 x armRadius)
        // |CB| = R sin theta
        // |OC| = R cos theta
        // L^2 = |CB|^2 + |CH|^2

        final float lengthCB = WHEEL_RADIUS * Mth.sin(unitCircleAngle);
        final float lengthOC = WHEEL_RADIUS * Mth.cos(unitCircleAngle);
        final float lengthCH = Mth.sqrt(ARM_LENGTH * ARM_LENGTH - lengthCB * lengthCB);

        final float angleHCB = (float) Math.acos(lengthCH / ARM_LENGTH);

        final float raiseAngle = quadrant == 2 || quadrant == 3
            ? angleHCB
            : -angleHCB;

        final float lengthEH = 1.5f - lengthCH + (quadrant == 1 || quadrant == 2 ? lengthOC : -lengthOC);

        return new ShaftMovement(lengthEH, raiseAngle);
    }

    public static float calculateRealRotationAngle(RotationOwner owner, Direction face, float partialTick)
    {
        float angle = RotationOwner.getRotationAngle(owner, partialTick);

        if (face == Direction.NORTH || face == Direction.EAST)
        {
            // Adjust the handed-ness of the angle to fix the direction of the crankshaft
            angle = Mth.TWO_PI - angle;
        }

        // Shift the angle 180 degrees, so that our resting position is with the shaft the furthest withdrawn.
        // Ensure that we clamp the result angle to [0, 2pi)
        angle += Mth.PI;
        if (angle > Mth.TWO_PI)
        {
            angle -= Mth.TWO_PI;
        }

        return angle;
    }

    /**
     * Tries to access the main (base) crank shaft block entity, provided all conditions are met:
     * <ul>
     *     <li>Both a crank shaft base, and shaft are present and connected</li>
     *     <li>The shaft movement direction is <strong>into</strong> the current block, <strong>from</strong> the given direction</li>
     * </ul>
     * @return The crankshaft if one exists, else {@code null}
     */
    @Nullable
    public static CrankshaftBlockEntity getCrankShaftAt(LevelAccessor level, BlockPos pos, Direction direction)
    {
        final BlockPos shaftPos = pos.relative(direction);
        final BlockState shaftState = level.getBlockState(shaftPos);
        if (isPart(shaftState, CrankshaftBlock.Part.SHAFT, direction))
        {
            final BlockPos basePos = pos.relative(direction, 2);
            final BlockState baseState = level.getBlockState(basePos);
            if (isPart(baseState, CrankshaftBlock.Part.BASE, direction))
            {
                return level.getBlockEntity(basePos, TFCBlockEntities.CRANKSHAFT.get()).orElse(null);
            }
        }
        return null;
    }

    private static boolean isPart(BlockState state, CrankshaftBlock.Part part, Direction direction)
    {
        return state.getBlock() == TFCBlocks.CRANKSHAFT.get() && state.getValue(CrankshaftBlock.PART) == part && state.getValue(CrankshaftBlock.FACING) == direction.getOpposite();
    }

    public CrankshaftBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CRANKSHAFT.get(), pos, state);
    }

    public void setRotationFromOutsideWorld()
    {
        // todo: need to connect this to a "fake" network, and handle that properly on client with a fixed rotation
    }

    @Nullable
    public RotationOwner getConnectedNetworkOwner()
    {
        assert level != null;

        // The direction and position that the rotation input would be facing in, relative to this crankshaft
        final Direction direction = getBlockState().getValue(CrankshaftBlock.FACING).getCounterClockWise();
        final BlockPos pos = worldPosition.relative(direction);

        return level.getBlockEntity(pos) instanceof RotationOwner owner ? owner : null;
    }

    /**
     * @return The current shaft extension length of the crankshaft. Will always be between {@code [0, 2 * WHEEL_RADIUS]}.
     */
    public float getExtensionLength(float partialTick)
    {
        final @Nullable RotationOwner owner = getConnectedNetworkOwner();
        if (owner != null)
        {
            final Direction face = getBlockState().getValue(CrankshaftBlock.FACING);
            final float rotationAngle = calculateRealRotationAngle(owner, face, partialTick);
            final ShaftMovement movement = calculateShaftMovement(rotationAngle);

            return Math.max(0, PISTON_LENGTH - movement.lengthEH);
        }
        return 0f;
    }

    public record ShaftMovement(float lengthEH, float raiseAngle) {}
}
