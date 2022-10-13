/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.util.Helpers;

public class TFCGroundPathNavigation extends GroundPathNavigation
{
    public static int getSurfaceYRespectingFluid(GroundPathNavigation navigation, Mob mob, Level level)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        if (mob.isInWater() && navigation.canFloat())
        {
            BlockState state = level.getBlockState(cursor.set(mob.getX(), mob.getBlockY(), mob.getZ()));
            int checked = 0;

            while (Helpers.isFluid(state.getFluidState(), FluidTags.WATER))
            {
                state = level.getBlockState(cursor.move(0, 1, 0));
                ++checked;
                if (checked > 16)
                {
                    return mob.getBlockY();
                }
            }
            return cursor.getY();
        }
        return Mth.floor(mob.getY() + 0.5D);
    }

    public TFCGroundPathNavigation(Mob mob, Level level)
    {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int followRange)
    {
        nodeEvaluator = new TFCWalkNodeEvaluator();
        nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(nodeEvaluator, followRange);
    }

    @Override
    protected Vec3 getTempMobPos()
    {
        return new Vec3(mob.getX(), getSurfaceYRespectingFluid(this, mob, level), mob.getZ());
    }

    public static class TFCWalkNodeEvaluator extends WalkNodeEvaluator
    {
        @Override
        protected BlockPathTypes evaluateBlockPathType(BlockGetter level, boolean openDoors, boolean enterDoors, BlockPos pos, BlockPathTypes pathType)
        {
            // interpret leaves as open space
            if (pathType == BlockPathTypes.LEAVES)
            {
                return BlockPathTypes.OPEN;
            }
            return super.evaluateBlockPathType(level, openDoors, enterDoors, pos, pathType);
        }

        @Override
        public Node getStart()
        {
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            int y = mob.getBlockY();
            BlockState state = level.getBlockState(cursor.set(mob.getX(), y, mob.getZ()));
            // if we are in fluid, rise to the surface
            // tfc: don't check if mobs can stand on the fluid, we don't do that here.
            if (canFloat() && mob.isInWater())
            {
                while (true) // tfc: use proper fluid check
                {
                    if (!Helpers.isFluid(state.getFluidState(), FluidTags.WATER))
                    {
                        --y;
                        break;
                    }

                    ++y;
                    state = level.getBlockState(cursor.set(mob.getX(), y, mob.getZ()));
                }
            }
            else if (mob.isOnGround()) // already on the ground, then just return our fuzzed standing pos
            {
                y = Mth.floor(mob.getY() + 0.5D);
            }
            else // move down until we hit something non-pathfindable, then take the block above it
            {
                cursor.set(mob.blockPosition());
                final int min = level.getMinBuildHeight();
                // tfc: use the cursor here instead of immutable positions
                while (true)
                {
                    state = level.getBlockState(cursor);
                    if (!(state.isAir() || state.isPathfindable(level, cursor, PathComputationType.LAND) && cursor.getY() > min))
                    {
                        break;
                    }
                    cursor.move(0, -1, 0);
                }
                y = cursor.getY() + 1;
            }

            // pathfinding malus sets how much we enjoy going to the particular block
            // if we hit a negative malus we should assume it is dangerous/impassable
            final BlockPos mobPos = mob.blockPosition();
            final BlockPathTypes pathType = getCachedBlockType(mob, mobPos.getX(), y, mobPos.getZ());
            if (mob.getPathfindingMalus(pathType) < 0)
            {
                // check each corner of our bounding box, if we hit a positive malus short-circuit and use that.
                final AABB aabb = mob.getBoundingBox();
                if (hasPositiveMalus(cursor.set(aabb.minX, y, aabb.minZ)) || hasPositiveMalus(cursor.set(aabb.minX, y, aabb.maxZ)) || hasPositiveMalus(cursor.set(aabb.maxX, y, aabb.minZ)) || hasPositiveMalus(cursor.set(aabb.maxX, y, aabb.maxZ)))
                {
                    return chooseNode(cursor.getX(), cursor.getY(), cursor.getZ());
                }
            }
            // we are pretty sure that the current block is appropriate, so just return that.
            return chooseNode(mobPos.getX(), y, mobPos.getZ());
        }

        private Node chooseNode(int x, int y, int z)
        {
            final Node node = getNode(x, y, z);
            node.type = getBlockPathType(mob, node.asBlockPos());
            node.costMalus = mob.getPathfindingMalus(node.type);
            return node;
        }

        private boolean hasPositiveMalus(BlockPos pos)
        {
            return mob.getPathfindingMalus(getBlockPathType(mob, pos)) >= 0.0F;
        }

        private BlockPathTypes getBlockPathType(Mob mob, BlockPos pos)
        {
            return getCachedBlockType(mob, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
