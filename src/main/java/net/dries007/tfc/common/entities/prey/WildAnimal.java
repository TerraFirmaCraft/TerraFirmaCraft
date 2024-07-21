/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.GenderedRenderAnimal;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;
import net.dries007.tfc.util.Helpers;

public class WildAnimal extends AgeableMob implements GenderedRenderAnimal
{
    public static final EntityDataAccessor<Boolean> DATA_IS_MALE = SynchedEntityData.defineId(WildAnimal.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.defineId(WildAnimal.class, EntityDataSerializers.BOOLEAN);

    protected final Supplier<SoundEvent> ambient;
    protected final Supplier<SoundEvent> death;
    protected final Supplier<SoundEvent> hurt;
    protected final Supplier<SoundEvent> step;

    public WildAnimal(EntityType<? extends AgeableMob> type, Level level, TFCSounds.EntityId sounds)
    {
        super(type, level);
        this.ambient = sounds.ambient();
        this.death = sounds.death();
        this.hurt = sounds.hurt();
        this.step = sounds.step();
        getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 4000 == 0 && random.nextInt(2000) == 0)
        {
            setBaby(false);
        }
    }

    @Override
    public boolean displayMaleCharacteristics()
    {
        return isMale() && !isBaby();
    }

    @Override
    public boolean displayFemaleCharacteristics()
    {
        return !isMale();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_MALE, true);
        builder.define(DATA_IS_BABY, false);
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
        super.setAge(0); // no-op vanilla aging
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        setIsMale(level.getRandom().nextBoolean());
        setBaby(random.nextFloat() < 0.1f);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("male", isMale());
        tag.putBoolean("baby", isBaby());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setIsMale(tag.getBoolean("male"));
        setBaby(tag.getBoolean("baby"));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob)
    {
        return (AgeableMob) getType().create(level);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock)
    {
        this.playSound(step.get(), 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        return hurt.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return death.get();
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return false;
    }

    @Override
    public boolean canBeLeashed()
    {
        return Helpers.isEntity(this, TFCTags.Entities.LEASHABLE_WILD_ANIMALS);
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
}
