package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.PlacementPredicate;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.world.decorator.ClimateConfig;

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

    public static PlacementPredicate<SeafloorCritterEntity> createSpawnRules(float minTemp, float maxTemp, float minRain, float maxRain)
    {
        return new PlacementPredicate<SeafloorCritterEntity>().fluid(TFCFluids.SALT_WATER.getSource()).belowSeaLevel(20).simpleClimate(minTemp, maxTemp, minRain, maxRain);
    }
}
