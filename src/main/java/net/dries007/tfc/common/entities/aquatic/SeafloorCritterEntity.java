/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.world.World;

public class SeafloorCritterEntity extends WaterMobEntity
{
    public SeafloorCritterEntity(EntityType<? extends WaterMobEntity> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        // don't have the ability to swim, but will path randomly anyway, resulting in them walking around the seafloor.
        goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0F, 30));
    }

    @Override
    protected PathNavigator createNavigation(World worldIn)
    {
        return new SwimmerPathNavigator(this, worldIn);
    }
}
