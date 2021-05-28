package net.dries007.tfc.common.entities.ai;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.mixin.entity.passive.TurtleEntityAccessor;

public class LayEggBlockGoal extends MoveToBlockGoal
{
    private final TurtleEntity turtle;
    private final Supplier<? extends Block> egg;

    public LayEggBlockGoal(TurtleEntity entity, double distance, Supplier<? extends Block> eggBlock)
    {
        super(entity, distance, 16);
        turtle = entity;
        egg = eggBlock;
    }

    @Override
    public boolean canUse()
    {
        return turtle.hasEgg() && ((TurtleEntityAccessor) turtle).invoke$getHomePos().closerThan(turtle.position(), 9.0D) && super.canUse();
    }

    @Override
    public boolean canContinueToUse()
    {
        return super.canContinueToUse() && turtle.hasEgg() && ((TurtleEntityAccessor) turtle).invoke$getHomePos().closerThan(turtle.position(), 9.0D);
    }

    @Override
    public void tick()
    {
        super.tick();
        BlockPos blockpos = turtle.blockPosition();
        if (!turtle.isInWater() && isReachedTarget())
        {
            int counter = ((TurtleEntityAccessor) turtle).getEggCounter();
            if (counter < 1)
            {
                ((TurtleEntityAccessor) turtle).invoke$setHasEgg(true);
            }
            else if (counter > 200)
            {
                World world = turtle.level;
                world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                world.setBlock(blockPos.above(), egg.get().defaultBlockState().setValue(TurtleEggBlock.EGGS, turtle.getRandom().nextInt(4) + 1), 3);
                ((TurtleEntityAccessor) turtle).invoke$setHasEgg(false);
                ((TurtleEntityAccessor) turtle).invoke$setLayingEgg(false);
                turtle.setInLoveTime(600);
            }

            if (turtle.isLayingEgg())
            {
                ((TurtleEntityAccessor) turtle).setEggCounter(counter + 1);
            }
        }

    }

    @Override
    protected boolean isValidTarget(IWorldReader world, BlockPos pos)
    {
        return world.isEmptyBlock(pos.above()) && TurtleEggBlock.isSand(world, pos);
    }
}
