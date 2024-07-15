/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.Temptable;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.pet.Dog;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class PackPredator extends Predator implements Temptable
{
    public static PackPredator createWolf(EntityType<? extends Predator> type, Level level)
    {
        return new PackPredator(type, level, false, TFCSounds.TFC_WOLF, true);
    }
    public static PackPredator createHyena(EntityType<? extends Predator> type, Level level)
    {
        return new PackPredator(type, level, false, TFCSounds.HYENA, false);
    }
    public static PackPredator createDirewolf(EntityType<? extends Predator> type, Level level)
    {
        return new PackPredator(type, level, false, TFCSounds.DOG, false);
    }

    public static final EntityDataAccessor<Integer> DATA_RESPECT = SynchedEntityData.defineId(PackPredator.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DATA_FAMILIARITY = SynchedEntityData.defineId(PackPredator.class, EntityDataSerializers.FLOAT);

    private boolean howled;
    private long nextFeedTime = Long.MIN_VALUE;

    private final boolean tamable;

    public PackPredator(EntityType<? extends Predator> type, Level level, boolean diurnal, TFCSounds.EntityId sounds, boolean tamable)
    {
        super(type, level, diurnal, sounds);
        this.tamable = tamable;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        setRespect(random.nextInt(10));
        return spawnData;
    }

    public int getRespect()
    {
        return entityData.get(DATA_RESPECT);
    }

    public void setRespect(int amount)
    {
        entityData.set(DATA_RESPECT, amount);
    }

    public void addRespect(int amount)
    {
        setRespect(getRespect() + amount);
    }

    public float getFamiliarity()
    {
        return entityData.get(DATA_FAMILIARITY);
    }

    public void setFamiliarity(float amount)
    {
        entityData.set(DATA_FAMILIARITY, Mth.clamp(amount, 0f, 1f));
    }

    public void addFamiliarity(float amount)
    {
        setFamiliarity(getFamiliarity() + amount);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_RESPECT, 0);
        builder.define(DATA_FAMILIARITY, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("respect", getRespect());
        tag.putFloat("familiarity", getFamiliarity());
        tag.putLong("nextFeed", nextFeedTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setRespect(EntityHelpers.getIntOrDefault(tag, "respect", 0));
        setFamiliarity(EntityHelpers.getFloatOrDefault(tag, "familiarity", 0f));
        nextFeedTime = EntityHelpers.getLongOrDefault(tag, "nextFeed", Long.MIN_VALUE);
    }

    @Override
    protected Brain.Provider<? extends Predator> brainProvider()
    {
        return Brain.provider(PackPredatorAi.MEMORY_TYPES, PackPredatorAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return PackPredatorAi.makeBrain(brainProvider().makeBrain(dynamic), this);
    }

    @Override
    public boolean doHurtTarget(Entity target)
    {
        if (super.doHurtTarget(target, 10))
        {
            if (!target.isAlive())
            {
                addRespect(getRandom().nextInt(3));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!level().isClientSide && source.getDirectEntity() instanceof LivingEntity livingEntity && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity))
        {
            PackPredatorAi.alertOthers(this, livingEntity);
        }
        else if (!level().isClientSide && isSleeping())
        {
            PackPredatorAi.alertOthers(this, null);
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick()
    {
        super.tick();
        final long time = level().getDayTime() % 24000;
        if (!howled && time > 18000 && time < 19000 && random.nextInt(10) == 0)
        {
            playSound(SoundEvents.WOLF_HOWL, getSoundVolume() * 1.2f, getVoicePitch());
            howled = true;
        }
        if (time > 19000)
        {
            howled = false;
        }
    }

    public boolean isTamable()
    {
        return tamable;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return !FoodCapability.isRotten(stack)
            && isTamable()
            && Helpers.isItem(stack, TFCTags.Items.DOG_FOOD);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (isFood(held))
        {
            if (!level().isClientSide)
            {
                final long ticks = Calendars.SERVER.getTicks();
                if (ticks > nextFeedTime)
                {
                    addFamiliarity(0.1f);
                    nextFeedTime = ticks + ICalendar.TICKS_IN_DAY;
                    if (!player.isCreative()) held.shrink(1);
                    playSound(getEatingSound(held), getSoundVolume(), getVoicePitch());
                    if (getFamiliarity() > 0.99f)
                    {
                        final boolean wasBaby = isBaby();
                        final Dog dog = convertTo(TFCEntities.DOG.get(), false);
                        if (dog != null && level() instanceof ServerLevelAccessor server)
                        {
                            dog.finalizeSpawn(server, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.CONVERSION, null);
                            dog.setGender(isMale() ? TFCAnimalProperties.Gender.MALE : TFCAnimalProperties.Gender.FEMALE);
                            if (!wasBaby)
                            {
                                dog.setBirthDay(Calendars.get(level()).getTotalDays() - 120);
                            }
                        }
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
}
