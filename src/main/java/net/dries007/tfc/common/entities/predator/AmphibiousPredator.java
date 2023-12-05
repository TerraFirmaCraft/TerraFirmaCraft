package net.dries007.tfc.common.entities.predator;

import java.util.Optional;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.ai.predator.AmphibiousPredatorAi;
import net.dries007.tfc.common.entities.aquatic.TrueAmphibiousMoveControl;
import net.dries007.tfc.common.fluids.TFCFluids;

public class AmphibiousPredator extends Predator
{
    //TODO: Crocodile Sounds
    public static AmphibiousPredator createCrocodile(EntityType<? extends Predator> type, Level level)
    {
        return new AmphibiousPredator(type, level, false, TFCSounds.BEAR);
    }


    public AmphibiousPredator(EntityType<? extends Predator> type, Level level, boolean diurnal, TFCSounds.EntitySound sounds)
    {
        super(type, level, diurnal, sounds);
        this.setPathfindingMalus(BlockPathTypes.WALKABLE, 0f);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0F);
        moveControl = new TrueAmphibiousMoveControl(this, 85, 10, 0.1F, 0.5F, false);
        lookControl = new SmoothSwimmingLookControl(this, 20);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.45F).add(Attributes.ATTACK_KNOCKBACK, 0).add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.KNOCKBACK_RESISTANCE, 0.90);
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new AmphibiousPredatorNavigation(this, level);
    }

    public static class AmphibiousPredatorNavigation extends WaterBoundPathNavigation
    {
        AmphibiousPredatorNavigation(AmphibiousPredator animal, Level level)
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
            return !level.getBlockState(pos.below()).isAir();
        }
    }

    @Override
    protected Brain.Provider<? extends Predator> brainProvider()
    {
        return Brain.provider(AmphibiousPredatorAi.MEMORY_TYPES, AmphibiousPredatorAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return AmphibiousPredatorAi.makeBrain(brainProvider().makeBrain(dynamic), this);
    }

    @Override
    public boolean doHurtTarget(Entity target)
    {
        boolean hurt = super.doHurtTarget(target, target.isInWaterOrBubble() ? 2 : 0);
        return hurt;
    }

    @Override
    public MobType getMobType()
    {
        return MobType.WATER;
    }

    @Override
    public boolean canSwimInFluidType(FluidType type)
    {
        return type == ForgeMod.WATER_TYPE.get() || type == TFCFluids.SALT_WATER.type().get() || type == TFCFluids.SPRING_WATER.type().get();
    }

    @Override
    public boolean canDrownInFluidType(FluidType type)
    {
        return !canSwimInFluidType(type);
    }

    @Override
    public boolean isPushedByFluid(FluidType type)
    {
        return false;
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level(), this);
        AmphibiousPredatorAi.updateActivity(this);
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
}
