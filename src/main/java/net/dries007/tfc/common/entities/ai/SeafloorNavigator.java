package net.dries007.tfc.common.entities.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeafloorNavigator extends SwimmerPathNavigator
{
    public SeafloorNavigator(MobEntity entity, World worldIn)
    {
        super(entity, worldIn);
    }

    @Override
    public boolean isStableDestination(BlockPos pos)
    {
        BlockPos below = pos.below();
        return !level.getBlockState(pos).isSolidRender(level, pos) && level.getBlockState(below).isSolidRender(level, below);
    }
}
