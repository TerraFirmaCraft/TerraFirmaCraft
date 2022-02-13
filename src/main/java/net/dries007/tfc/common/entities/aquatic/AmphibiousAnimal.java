package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.Dynamic;
import net.dries007.tfc.common.entities.ai.AmphibiousAi;

public class AmphibiousAnimal extends PathfinderMob
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public AmphibiousAnimal(EntityType<? extends AmphibiousAnimal> type, Level level)
    {
        super(type, level);
        moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.1F, 0.5F, false);
        lookControl = new SmoothSwimmingLookControl(this, 20);
        maxUpStep = 1.0F;
    }

    @Override
    protected Brain.Provider<AmphibiousAnimal> brainProvider()
    {
        return Brain.provider(AmphibiousAi.MEMORY_TYPES, AmphibiousAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return AmphibiousAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<AmphibiousAnimal> getBrain()
    {
        return (Brain<AmphibiousAnimal>) super.getBrain();
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    @Override
    public void travel(Vec3 movement)
    {
        if (isEffectiveAi() && isInWater())
        {
            moveRelative(getSpeed(), movement);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9D));
        }
        else
        {
            super.travel(movement);
        }
    }

    @Override
    public MobType getMobType()
    {
        return MobType.WATER;
    }

    @Override
    public boolean isPushedByFluid()
    {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player)
    {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new Navigation(this, level);
    }

    static class Navigation extends WaterBoundPathNavigation
    {
        Navigation(AmphibiousAnimal animal, Level level)
        {
            super(animal, level);
        }

        @Override
        protected PathFinder createPathFinder(int distance)
        {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
            return new PathFinder(this.nodeEvaluator, distance);
        }

        @Override
        protected boolean canUpdatePath()
        {
            return true;
        }

        @Override
        public boolean isStableDestination(BlockPos pos)
        {
            if (level.getFluidState(mob.blockPosition()).is(FluidTags.WATER))
            {
                return level.getFluidState(pos).is(FluidTags.WATER);
            }
            return !level.getBlockState(pos.below()).isAir();
        }
    }
}
