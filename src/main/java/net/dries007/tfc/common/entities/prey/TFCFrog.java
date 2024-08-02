/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import java.util.Optional;
import com.mojang.serialization.Dynamic;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.entities.BrainAnimalBehavior;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.Temptable;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCFrog extends Frog implements Temptable, BrainAnimalBehavior
{
    public static final EntityDataAccessor<Boolean> DATA_IS_MALE = SynchedEntityData.defineId(TFCFrog.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_FAMILIARITY = SynchedEntityData.defineId(TFCFrog.class, EntityDataSerializers.FLOAT);

    private long lastMated = Long.MIN_VALUE;
    private long nextFeedTime = Long.MIN_VALUE;

    public TFCFrog(EntityType<? extends Frog> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected Brain.Provider<Frog> brainProvider()
    {
        return Brain.provider(MEMORY_TYPES, TFCFrogAi.SENSOR_TYPES);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return TFCFrogAi.makeBrain((Brain<? extends Frog>) super.makeBrain(dynamic));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_MALE, true);
        builder.define(DATA_FAMILIARITY, 0f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setFamiliarity(tag.getFloat("familiarity"));
        setIsMale(tag.getBoolean("male"));
        lastMated = tag.getLong("lastMated");
        nextFeedTime = tag.getLong("nextFeed");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("male", isMale());
        tag.putFloat("familiarity", getFamiliarity());
        tag.putLong("lastMated", lastMated);
        tag.putLong("nextFeed", nextFeedTime);
    }

    public float getFamiliarity()
    {
        return entityData.get(DATA_FAMILIARITY);
    }

    public void setFamiliarity(float familiarity)
    {
        entityData.set(DATA_FAMILIARITY, familiarity);
    }

    public void setIsMale(boolean male)
    {
        entityData.set(DATA_IS_MALE, male);
    }

    @Override
    public boolean isMale()
    {
        return entityData.get(DATA_IS_MALE);
    }

    @Override
    public void setLastMatedNow()
    {
        lastMated = Calendars.get(level()).getTicks();
    }

    @Override
    public boolean canMate(Animal animal)
    {
        if (animal != this && animal instanceof TFCFrog other)
        {
            final float min = TFCAnimalProperties.READY_TO_MATE_FAMILIARITY;
            return other.isMale() != isMale() && beenLongEnoughToMate() && other.beenLongEnoughToMate() && getFamiliarity() > min && other.getFamiliarity() > min && fedRecently() && other.fedRecently();
        }
        return false;
    }

    protected boolean beenLongEnoughToMate()
    {
        return Calendars.get(level()).getTicks() > lastMated + (ICalendar.TICKS_IN_DAY * 12);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return !FoodCapability.isRotten(stack)
            && Helpers.isItem(stack, TFCTags.Items.FROG_FOOD);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        final BlockPos pos = blockPosition();
        final ChunkData data = ChunkData.get(level, pos);
        final float temp = data.getAverageTemp(pos);

        setVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(temp < 0 ? FrogVariant.COLD : temp > 18 ? FrogVariant.WARM : FrogVariant.TEMPERATE));
        setIsMale(random.nextBoolean());
        return spawnData;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob)
    {
        if (mob == this)
        {
            TFCFrog frog = TFCEntities.FROG.get().create(level);
            if (frog != null)
            {
                frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, UniformInt.of(100, 140).sample(random));
            }
            return frog;
        }
        if (mob instanceof TFCFrog animal)
        {
            spawnChildFromBreeding(level, animal);
        }
        return null;
    }

    @Override
    public void finalizeSpawnChildFromBreeding(ServerLevel level, Animal animal, @Nullable AgeableMob mob)
    {
        Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable(animal.getLoveCause())).ifPresent((player) -> {
            player.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, this, animal, mob);
        });
        this.resetLove();
        animal.resetLove();
        level.broadcastEntityEvent(this, (byte) 18);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (isFood(held))
        {
            if (!level().isClientSide)
            {
                final long ticks = Calendars.SERVER.getTicks();
                if (ticks > nextFeedTime)
                {
                    setFamiliarity(getFamiliarity() + 0.1f);
                    nextFeedTime = ticks + ICalendar.TICKS_IN_DAY;
                    usePlayerItem(player, hand, held);
                    playSound(SoundEvents.FROG_EAT);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private boolean fedRecently()
    {
        return Calendars.get(level()).getTicks() < nextFeedTime;
    }
}
