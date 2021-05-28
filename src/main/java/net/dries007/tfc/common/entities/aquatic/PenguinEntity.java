package net.dries007.tfc.common.entities.aquatic;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.ai.LayEggBlockGoal;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.mixin.entity.ai.goal.GoalSelectorAccessor;
import net.dries007.tfc.mixin.entity.passive.TurtleEntityAccessor;

public class PenguinEntity extends TurtleEntity
{
    public PenguinEntity(EntityType<? extends TurtleEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        Set<PrioritizedGoal> availableGoals = ((GoalSelectorAccessor) goalSelector).getAvailableGoals();
        // need to remove LayEgg, PlayerTempt, GoHome, GoToWater
        availableGoals.removeIf(priority -> {
            Goal goal = priority.getGoal();
            return goal instanceof TemptGoal || (priority.getPriority() == 1 && goal instanceof MoveToBlockGoal)// egg laying
                || goal instanceof GoHomeGoal || goal instanceof GoToWaterGoal;
        });
        goalSelector.addGoal(1, new LayEggBlockGoal(this, 1.0D, TFCBlocks.PENGUIN_EGG));
        goalSelector.addGoal(2, new PlayerTemptGoal(this, 1.1D, TFCItems.MORTAR.get()));//todo: tempt with some tfc item
        goalSelector.addGoal(3, new TFCGoToWaterGoal(this, 1.0D));
        goalSelector.addGoal(4, new TFCGoHomeGoal(this, 1.0D));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes()
    {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.5D);
    }

    @Override
    protected PathNavigator createNavigation(World world)
    {
        return new Navigator(this, world);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem() == Blocks.SEAGRASS.asItem();
    }

    @Override
    protected void ageBoundaryReached()
    {
        super.ageBoundaryReached();
        if (!isBaby() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
        {
            spawnAtLocation(Items.SCUTE, 1);
        }
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        int counter = ((TurtleEntityAccessor) this).getEggCounter();
        if (isAlive() && isLayingEgg() && counter >= 1 && counter % 5 == 0)
        {
            BlockPos pos = blockPosition();
            if (TurtleEggBlock.onSand(level, pos))
            {
                level.levelEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getId(level.getBlockState(pos)));
            }
        }
    }

    private static class TFCGoHomeGoal extends GoHomeGoal
    {
        private final TurtleEntity turtle;
        private final double speedMod;

        public TFCGoHomeGoal(TurtleEntity entity, double speed)
        {
            super(entity, speed);
            turtle = entity;
            speedMod = speed;
        }

        @Override
        public void tick()
        {
            BlockPos blockpos = ((TurtleEntityAccessor) turtle).invoke$getHomePos();
            boolean isNearby = blockpos.closerThan(turtle.position(), 16.0D);
            if (isNearby)
            {
                ++closeToHomeTryTicks;
            }
            if (turtle.getNavigation().isDone())
            {
                Vector3d currentPos = Vector3d.atBottomCenterOf(blockpos);
                Vector3d nextPos = RandomPositionGenerator.getPosTowards(turtle, 16, 3, currentPos, (float) Math.PI / 10F);
                if (nextPos == null)
                {
                    nextPos = RandomPositionGenerator.getPosTowards(turtle, 8, 7, currentPos);
                }
                if (nextPos != null && !isNearby && !turtle.level.getFluidState(new BlockPos(nextPos)).is(FluidTags.WATER))
                {
                    nextPos = RandomPositionGenerator.getPosTowards(turtle, 16, 5, currentPos);
                }
                if (nextPos == null)
                {
                    stuck = true;
                    return;
                }
                turtle.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, speedMod);
            }

        }
    }

    private static class TFCGoToWaterGoal extends GoToWaterGoal
    {
        public TFCGoToWaterGoal(TurtleEntity entity, double speedMod)
        {
            super(entity, speedMod);
        }

        @Override
        protected boolean isValidTarget(IWorldReader world, BlockPos pos)
        {
            return world.getFluidState(pos).is(FluidTags.WATER);
        }
    }

    private static class Navigator extends SwimmerPathNavigator
    {
        Navigator(TurtleEntity entity, World world)
        {
            super(entity, world);
        }

        protected boolean canUpdatePath()
        {
            return true;
        }

        protected PathFinder createPathFinder(int maxNodes)
        {
            nodeEvaluator = new WalkAndSwimNodeProcessor();
            return new PathFinder(nodeEvaluator, maxNodes);
        }

        public boolean isStableDestination(BlockPos pos)
        {
            if (mob instanceof TurtleEntity)
            {
                TurtleEntity turtle = (TurtleEntity) mob;
                if (((TurtleEntityAccessor) turtle).invoke$isTravelling())
                {
                    return level.getFluidState(pos).is(FluidTags.WATER);
                }
            }
            return !level.isEmptyBlock(pos.below());
        }
    }
}
