/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class TFCGroundPathNavigation extends GroundPathNavigation
{
    // todo 1.21: the only modification here looks like replacing a water for water-like check in the original
    public static int getSurfaceYRespectingFluid(GroundPathNavigation navigation, Mob mob, Level level)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        if (mob.isInWater() && navigation.canFloat())
        {
            BlockState state = level.getBlockState(cursor.set(mob.getX(), mob.getBlockY(), mob.getZ()));
            int checked = 0;

            while (Helpers.isFluid(state.getFluidState(), TFCTags.Fluids.WATER_LIKE))
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
        public PathType getPathType(PathfindingContext context, int x, int y, int z)
        {
            // todo 1.21: can this be fixed by just making our leaves return PathType.OPEN ? That seems much better than this hack
            final PathType pathType = super.getPathType(context, x, y, z);
            return pathType == PathType.LEAVES ? PathType.OPEN : pathType;
        }

        // todo 1.21, this might be better implemented as a mixin into the original class, since we should apply for all mobs?
        // It seems we only need to modify one check, which can be done really nice with mixinextras
        @Override
        public Node getStart()
        {
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

            int yPos = this.mob.getBlockY();
            BlockState groundState = this.currentContext.getBlockState(cursor.set(mob.getX(), yPos, mob.getZ()));
            if (!mob.canStandOnFluid(groundState.getFluidState()))
            {
                if (canFloat() && mob.isInWater())
                {
                    while (true)
                    {
                        // TFC: Include other water fluids here
                        if (!Helpers.isFluid(groundState.getFluidState(), TFCTags.Fluids.WATER_LIKE))
                        {
                            yPos--;
                            break;
                        }

                        groundState = currentContext.getBlockState(cursor.set(mob.getX(), ++yPos, mob.getZ()));
                    }
                }
                else if (mob.onGround())
                {
                    yPos = Mth.floor(mob.getY() + 0.5);
                }
                else
                {
                    cursor.set(mob.getX(), mob.getY() + 1.0, mob.getZ());

                    while (cursor.getY() > currentContext.level().getMinBuildHeight())
                    {
                        yPos = cursor.getY();
                        cursor.setY(cursor.getY() - 1);

                        final BlockState insideState = this.currentContext.getBlockState(cursor);
                        if (!insideState.isAir() && !insideState.isPathfindable(PathComputationType.LAND))
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                while (mob.canStandOnFluid(groundState.getFluidState()))
                {
                    groundState = currentContext.getBlockState(cursor.set(mob.getX(), ++yPos, mob.getZ()));
                }
                yPos--;
            }

            final BlockPos mobPos = mob.blockPosition();
            if (!canStartAt(cursor.set(mobPos.getX(), yPos, mobPos.getZ())))
            {
                final AABB box = mob.getBoundingBox();
                if (canStartAt(cursor.set(box.minX, yPos, box.minZ))
                    || canStartAt(cursor.set(box.minX, yPos, box.maxZ))
                    || canStartAt(cursor.set(box.maxX, yPos, box.minZ))
                    || canStartAt(cursor.set(box.maxX, yPos, box.maxZ)))
                {
                    return getStartNode(cursor);
                }
            }

            return this.getStartNode(cursor);
        }
    }
}
