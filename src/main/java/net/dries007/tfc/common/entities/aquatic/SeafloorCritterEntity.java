package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.SeafloorNavigator;

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
        goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0F, 30));
    }

    @Override
    protected PathNavigator createNavigation(World worldIn)
    {
        return new SwimmerPathNavigator(this, worldIn);
    }
}
