/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.PredicateMoveControl;
import net.dries007.tfc.common.entities.ai.TFCClimberNavigation;
import net.dries007.tfc.common.entities.ai.prey.PestAi;
import net.dries007.tfc.util.Helpers;

public class Pest extends Prey
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(Pest.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState sniffingAnimation = new AnimationState();
    public final AnimationState searchingAnimation = new AnimationState();
    public final AnimationState eatingAnimation = new AnimationState();
    public final AnimationState draggingAnimation = new AnimationState();

    private static final int DRAG_TIME = 200;
    private static final int EAT_TIME = 200;

    private int dragTicks = -1;

    public Pest(EntityType<? extends Prey> type, Level level, TFCSounds.EntityId sounds)
    {
        super(type, level, sounds);
        moveControl = new PredicateMoveControl<>(this, p -> p.dragTicks == -1 || p.dragTicks > DRAG_TIME + EAT_TIME);
    }

    @Override
    protected Brain.Provider<? extends Pest> brainProvider()
    {
        return Brain.provider(PestAi.MEMORY_TYPES, PestAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return PestAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_CLIMBING, false);
    }

    public void setClimbing(boolean climbing)
    {
        entityData.set(DATA_CLIMBING, climbing);
    }

    public boolean isClimbing()
    {
        return entityData.get(DATA_CLIMBING);
    }

    @Override
    public boolean onClimbable()
    {
        return isClimbing();
    }

    @Override
    public void tick()
    {
        final ItemStack held = getMainHandItem();
        if (!held.isEmpty() || eatingAnimation.isStarted())
        {
            dragTicks++;
            if (dragTicks < DRAG_TIME)
            {
                if (level().isClientSide)
                {
                    draggingAnimation.startIfStopped(tickCount);
                }
            }
            else
            {
                if (level().isClientSide)
                {
                    draggingAnimation.stop();
                    eatingAnimation.startIfStopped(tickCount);
                    level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, held), getX(), getEyeY(), getZ(), Helpers.triangle(random), -random.nextFloat(), Helpers.triangle(random));
                    if (random.nextInt(20) == 0)
                    {
                        playSound(SoundEvents.GENERIC_EAT, getSoundVolume(), getVoicePitch());
                    }
                }
                if (dragTicks > (DRAG_TIME + EAT_TIME))
                {
                    setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (level().isClientSide)
                    {
                        eatingAnimation.stop();
                    }
                    playSound(SoundEvents.PLAYER_BURP, getSoundVolume(), getVoicePitch());
                }
            }
        }
        else
        {
            dragTicks = -1;
        }
        if (level().isClientSide && dragTicks == -1)
        {
            if (!EntityHelpers.isMovingOnLand(this) && random.nextInt(20) == 0)
            {
                if (random.nextBoolean())
                {
                    sniffingAnimation.startIfStopped(tickCount);
                    playSound(SoundEvents.FOX_SNIFF, getSoundVolume(), getVoicePitch());
                }
                else
                {
                    searchingAnimation.startIfStopped(tickCount);
                    playSound(SoundEvents.FOX_SNIFF, getSoundVolume(), getVoicePitch());
                }
            }
        }
        super.tick();
        if (!level().isClientSide)
        {
            setClimbing(horizontalCollision);
        }
        if (tickCount > 20 * 60 * 3 && random.nextInt(500) == 0 && !isPersistenceRequired())
        {
            discard();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCClimberNavigation(this, level);
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return !isPersistenceRequired();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        if (random.nextInt(1000) == 0)
        {
            setCustomName(Component.literal("Pak"));
        }
        if (random.nextFloat() < 0.2f)
        {
            setBaby(true);
        }
        return spawnData;
    }
}
