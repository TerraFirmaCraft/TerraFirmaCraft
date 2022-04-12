/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.Dynamic;
import net.dries007.tfc.common.entities.ai.amphibian.AmphibianAi;
import net.dries007.tfc.util.Helpers;

public class AmphibiousAnimal extends PathfinderMob
{
    public static final int PLAY_DEAD_TIME = 200;

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(AmphibiousAnimal.class, EntityDataSerializers.BOOLEAN);

    public AmphibiousAnimal(EntityType<? extends AmphibiousAnimal> type, Level level)
    {
        super(type, level);
        moveControl = new MoveControl(this, level);
        lookControl = new SmoothSwimmingLookControl(this, 20);
        maxUpStep = 1.0F;
    }

    @Override
    protected Brain.Provider<AmphibiousAnimal> brainProvider()
    {
        return Brain.provider(AmphibianAi.MEMORY_TYPES, AmphibianAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return AmphibianAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    public void tick()
    {
        maxUpStep = isPlayingDead() ? 0.0F : 1.0F;
        super.tick();
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level, this);
        AmphibianAi.updateActivity(this);
        if (!isNoAi())
        {
            Optional<Integer> optionalTicks = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            setPlayingDead(optionalTicks.isPresent() && optionalTicks.get() > 0);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        boolean hurt = super.hurt(source, amount);
        if (!level.isClientSide && getHealth() > 0 && amount > 0.5F && !isPlayingDead())
        {
            brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, PLAY_DEAD_TIME);
        }
        return hurt;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level.getLevel().dimension(), blockPosition()));
        return super.finalizeSpawn(level, difficulty, type, data, tag);
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
        if (isPlayingDead())
        {
            movement = new Vec3(0, movement.y < 0 ? movement.y : 0, 0);
        }
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

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_PLAYING_DEAD, false);
    }

    public boolean isPlayingDead()
    {
        return entityData.get(DATA_PLAYING_DEAD);
    }

    public void setPlayingDead(boolean dead)
    {
        entityData.set(DATA_PLAYING_DEAD, dead);
    }

    class MoveControl extends SmoothSwimmingMoveControl
    {
        public MoveControl(AmphibiousAnimal animal, Level level)
        {
            super(animal, 85, 10, 0.1F, 0.5F, false);
        }

        @Override
        public void tick()
        {
            if (!AmphibiousAnimal.this.isPlayingDead()) super.tick();
        }
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
            if (Helpers.isFluid(level.getFluidState(mob.blockPosition()), FluidTags.WATER))
            {
                return Helpers.isFluid(level.getFluidState(pos), FluidTags.WATER);
            }
            return !level.getBlockState(pos.below()).isAir();
        }
    }
}
