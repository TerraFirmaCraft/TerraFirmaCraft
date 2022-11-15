/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class TFCClimberNavigation extends WallClimberNavigation
{
    public TFCClimberNavigation(Mob mob, Level level)
    {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int followRange)
    {
        nodeEvaluator = new TFCGroundPathNavigation.TFCWalkNodeEvaluator();
        nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(nodeEvaluator, followRange);
    }

    @Override
    protected Vec3 getTempMobPos()
    {
        return new Vec3(mob.getX(), TFCGroundPathNavigation.getSurfaceYRespectingFluid(this, mob, level), mob.getZ());
    }
}
