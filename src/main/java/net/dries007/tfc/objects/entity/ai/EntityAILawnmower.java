package net.dries007.tfc.objects.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.event.ForgeEventFactory;

import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;

public class EntityAILawnmower extends EntityAIBase
{
    private final EntityLiving grassEater;
    private final World entityWorld;
    private int timer;

    public EntityAILawnmower(EntityLiving grassEater)
    {
        this.grassEater = grassEater;
        this.entityWorld = grassEater.world;
        this.setMutexBits(2);
        this.timer = 0;
    }

    @Override
    public boolean shouldExecute()
    {
        return grassEater.getRNG().nextInt(100) != 0 && isAtShortGrass();
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return timer > 0;
    }

    @Override
    public void startExecuting()
    {
        timer = 40;
        entityWorld.setEntityState(grassEater, (byte) 10);
        grassEater.getNavigator().clearPath();
    }

    @Override
    public void resetTask()
    {
        timer = 0;
    }

    @Override
    public void updateTask()
    {
        timer = Math.max(0, timer - 1);
        if (timer == 4 && isAtShortGrass() && ForgeEventFactory.getMobGriefingEvent(entityWorld, grassEater))
        {
            entityWorld.destroyBlock(grassEater.getPosition(), false);
        }
    }

    private boolean isAtShortGrass()
    {
        BlockPos pos = grassEater.getPosition();
        IBlockState state = entityWorld.getBlockState(pos);
        return state.getBlock() instanceof BlockShortGrassTFC;
    }

    public int getTimer()
    {
        return timer;
    }
}
