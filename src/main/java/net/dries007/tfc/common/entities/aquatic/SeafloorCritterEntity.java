package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.SeafloorNavigator;

public class SeafloorCritterEntity extends WaterMobEntity
{
    public SeafloorCritterEntity(EntityType<? extends WaterMobEntity> type, World world)
    {
        super(type, world);
        setPathfindingMalus(PathNodeType.WATER, 0.0F);
    }

    @Override
    protected PathNavigator createNavigation(World worldIn)
    {
        return new SeafloorNavigator(this, worldIn);
    }
}
