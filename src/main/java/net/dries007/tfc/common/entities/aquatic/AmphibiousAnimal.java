/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.Optional;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.entities.Temptable;
import net.dries007.tfc.common.entities.ai.amphibian.AmphibianAi;
import net.dries007.tfc.common.entities.prey.WildAnimal;
import net.dries007.tfc.util.Helpers;

public abstract class AmphibiousAnimal extends WildAnimal implements Temptable
{
    public static final int PLAY_DEAD_TIME = 200;

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.STEP_HEIGHT, 1.0);
    }

    private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(AmphibiousAnimal.class, EntityDataSerializers.BOOLEAN);

    public AmphibiousAnimal(EntityType<? extends AmphibiousAnimal> type, Level level, TFCSounds.EntityId sound)
    {
        super(type, level, sound);
        this.setPathfindingMalus(PathType.WALKABLE, 0f);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        moveControl = new AmphibianMoveControl(this);
        lookControl = new SmoothSwimmingLookControl(this, 20);
    }

    public boolean isPlayingDeadEffective()
    {
        return true;
    }

    @Override
    public void playAmbientSound()
    {
        if (!isPlayingDead())
        {
            super.playAmbientSound();
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return 0.0F;
    }

    @Override
    protected Brain.Provider<? extends AmphibiousAnimal> brainProvider()
    {
        return Brain.provider(AmphibianAi.MEMORY_TYPES, AmphibianAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return AmphibianAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level(), this);
        AmphibianAi.updateActivity(this);
        if (!isNoAi() && !isInWaterOrBubble())
        {
            Optional<Integer> optionalTicks = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            setPlayingDead(optionalTicks.isPresent() && optionalTicks.get() > 0);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        boolean hurt = super.hurt(source, amount);
        if (!level().isClientSide && getHealth() > 0 && amount > 0.5F && !isPlayingDead())
        {
            brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, PLAY_DEAD_TIME);
        }
        return hurt;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level.getLevel().dimension(), blockPosition()));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<AmphibiousAnimal> getBrain()
    {
        return (Brain<AmphibiousAnimal>) super.getBrain();
    }

    @Override
    public boolean canDrownInFluidType(FluidType type)
    {
        return !canSwimInFluidType(type);
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
    public boolean isPushedByFluid(FluidType type)
    {
        return false;
    }

    @Override
    public boolean canBeLeashed()
    {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new AmphibianNavigation(this, level);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_PLAYING_DEAD, false);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (isFood(held) && !FoodCapability.isRotten(held) && getHealth() < getMaxHealth())
        {
            heal(1f);
            held.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean doHurtTarget(Entity entity)
    {
        final boolean hurt = super.doHurtTarget(entity);
        if (!entity.isAlive())
        {
            getBrain().setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, 1000);
        }
        return hurt;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos)
    {
        return Helpers.isBlock(level().getBlockState(pos.below()), BlockTags.SAND) ? 10f : super.getWalkTargetValue(pos);
    }

    public boolean isPlayingDead()
    {
        return entityData.get(DATA_PLAYING_DEAD);
    }

    public void setPlayingDead(boolean dead)
    {
        entityData.set(DATA_PLAYING_DEAD, dead);
    }

    public class AmphibianMoveControl extends TrueAmphibiousMoveControl
    {
        public AmphibianMoveControl(AmphibiousAnimal animal)
        {
            super(animal, 85, 10, 0.1F, 0.5F, false);
        }

        @Override
        public void tick()
        {
            if (!AmphibiousAnimal.this.isPlayingDead()) super.tick();
        }
    }

    public static class AmphibianNavigation extends WaterBoundPathNavigation
    {
        AmphibianNavigation(AmphibiousAnimal animal, Level level)
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
}
