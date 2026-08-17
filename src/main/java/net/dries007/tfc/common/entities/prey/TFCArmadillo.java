/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.Scareable;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.Temptable;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class TFCArmadillo extends Armadillo implements Temptable, Scareable
{
    public static final EntityDataAccessor<Boolean> DATA_IS_MALE = SynchedEntityData.defineId(TFCArmadillo.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.defineId(TFCArmadillo.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Long> DATA_PRODUCED = SynchedEntityData.defineId(TFCArmadillo.class, EntityDataSerializers.LONG);

    protected int produceTicks;
    protected int predatorLoseInterestTime;

    public TFCArmadillo(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.produceTicks = 30 * ICalendar.PLAYER_TICKS_IN_DEFAULT_HOUR;
    }

    @Override
    protected Brain.Provider<Armadillo> brainProvider()
    {
        return (Brain.Provider<Armadillo>) TFCArmadilloAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return TFCArmadilloAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob)
    {
        return TFCEntities.ARMADILLO.get().create(level);
    }

    @Override
    public boolean isScared()
    {
        return super.isScared();
    }

    @Override
    public boolean isScaredBy(LivingEntity entity)
    {
        if (entity.getType().is(TFCTags.Entities.LAND_PREDATORS) && !entity.isSleeping())
        {
            return true;
        }
        return super.isScaredBy(entity);
    }

    @Override
    public boolean isCurrentlyScareable()
    {
        return this.canStayRolledUp();
    }

    @Override
    protected void customServerAiStep()
    {
        ((Brain<TFCArmadillo>) getBrain()).tick((ServerLevel) level(), this);
        TFCArmadilloAi.updateActivity(this);
        if (this.isScared() && predatorLoseInterestTime > 0)
        {
            predatorLoseInterestTime -= random.nextInt(5);
        }

        // Scute Reimplementation based on TFC Calendar
        if (this.isAlive() && !this.isBaby() && getProductsCooldown() == 0)
        {
            this.playSound(SoundEvents.ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Items.ARMADILLO_SCUTE);
            this.gameEvent(GameEvent.ENTITY_PLACE);
            setProductsCooldown();
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 4000 == 0 && random.nextInt(2000) == 0)
        {
            setBaby(false);
        }
        if (!isScared() && predatorLoseInterestTime < 180)
        {
            predatorLoseInterestTime = random.nextInt(180, 361);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        return InteractionResult.PASS;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_MALE, true);
        builder.define(DATA_IS_BABY, false);
        builder.define(DATA_PRODUCED, 0L);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        super.onSyncedDataUpdated(key);
        if (DATA_IS_BABY.equals(key))
        {
            refreshDimensions();
        }
    }

    public boolean isMale()
    {
        return entityData.get(DATA_IS_MALE);
    }

    public void setIsMale(boolean male)
    {
        entityData.set(DATA_IS_MALE, male);
    }

    @Override
    public boolean isBaby()
    {
        return entityData.get(DATA_IS_BABY);
    }

    @Override
    public void setBaby(boolean baby)
    {
        entityData.set(DATA_IS_BABY, baby);
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0);
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Override
    public boolean canBeSeenAsEnemy()
    {
        if (predatorLoseInterestTime <= 0)
        {
            return false;
        }
        else return super.canBeSeenAsEnemy();
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (this.isScared())
        {
            amount -= 1.0f;
            // in super.hurt : amount = (amount - 1.0F) / 2.0F
        }
        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        setIsMale(level.getRandom().nextBoolean());
        setBaby(random.nextFloat() < 0.1F);
        setProducedTick(Calendars.get(level()).getTicks() - this.random.nextInt(ICalendar.CALENDAR_TICKS_IN_HOUR * 20));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("male", isMale());
        tag.putBoolean("baby", isBaby());
        tag.putLong("produced", getProducedTick());
        tag.putInt("PredatorLoseInterestTime", predatorLoseInterestTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setIsMale(tag.getBoolean("male"));
        setBaby(tag.getBoolean("baby"));
        setProducedTick(tag.getLong("produced"));
        predatorLoseInterestTime = tag.getInt("PredatorLoseInterestTime");
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCGroundPathNavigation(this, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON) ? 10.0F : level.getPathfindingCostFromLightLevels(pos) - 0.5F;
    }

    public void setProductsCooldown()
    {
        setProducedTick(Calendars.get(level()).getTicks());
    }

    public long getProductsCooldown()
    {
        return Math.max(0, this.produceTicks + getProducedTick() - Calendars.get(level()).getTicks());
    }

    public long getProducedTick()
    {
        return entityData.get(DATA_PRODUCED);
    }

    public void setProducedTick(long producedTick)
    {
        entityData.set(DATA_PRODUCED, producedTick);
    }
}
