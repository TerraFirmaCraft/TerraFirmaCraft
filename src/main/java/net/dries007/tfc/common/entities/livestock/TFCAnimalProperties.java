/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.Util;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.BowlComponent;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.entities.BrainBreeder;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.GenderedRenderAnimal;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public interface TFCAnimalProperties extends GenderedRenderAnimal, BrainBreeder
{
    long MATING_COOLDOWN_DEFAULT_TICKS = ICalendar.TICKS_IN_DAY;
    float READY_TO_MATE_FAMILIARITY = 0.3f;
    float[] AGE_SCALES = Util.make(() -> {
        final float[] scales = new float[32];
        for (int i = 0; i < scales.length; i++)
        {
            scales[i] = Mth.map(i + 1, 1, 32, 0.8f, 1.2f);
        }
        return scales;
    });

    default LivingEntity getEntity()
    {
        return (LivingEntity) this;
    }

    default ICalendar getCalendar()
    {
        return Calendars.get(getEntity().level());
    }

    private SynchedEntityData entityData()
    {
        return getEntity().getEntityData();
    }

    CommonAnimalData animalData();

    AnimalConfig animalConfig();

    long getLastFamiliarityDecay();

    void setLastFamiliarityDecay(long days);

    default void setLastFed(long fed)
    {
        entityData().set(animalData().lastFed(), fed);
    }

    default long getLastFed()
    {
        return entityData().get(animalData().lastFed());
    }

    void setMated(long time);

    long getMated();

    Age getLastAge();

    void setLastAge(Age age);

    /**
     * Is this animal hungry?
     *
     * @return true if this animal can be fed by player
     */
    default boolean isHungry()
    {
        return getLastFed() < getCalendar().getTotalDays();
    }

    /**
     * Default tag checked by isFood (edible items)
     */
    TagKey<Item> getFoodTag();

    default EntityType<?> getEntityTypeForBaby()
    {
        return getEntity().getType();
    }

    /**
     * Is it time to decay familiarity?
     * If this entity was never fed(eg: newborn, wild) or wasn't fed yesterday (this is the starting of the second day)
     */
    default void tickAnimalData()
    {
        if (getLastFamiliarityDecay() > -1 && getLastFamiliarityDecay() + 1 < getCalendar().getTotalDays())
        {
            float familiarity = getFamiliarity();
            if (familiarity < TFCConfig.SERVER.familiarityDecayLimit.get())
            {
                familiarity -= 0.02 * (getCalendar().getTotalDays() - getLastFamiliarityDecay());
                setLastFamiliarityDecay(getCalendar().getTotalDays());
                this.setFamiliarity(familiarity);
            }
        }
        final Age age = getAgeType();
        if (age != getLastAge())
        {
            setLastAge(age);
            getEntity().refreshDimensions();
        }
        // because this is a random value it's not deterministic, we will allow the entity to sync it on its own
        if (!getEntity().level().isClientSide && age == Age.ADULT && getUses() > getUsesToElderly() && getOldDay() == -1L)
        {
            final long oldDay = getCalendar().getTotalDays() + 1 + getEntity().getRandom().nextInt(5);
            setOldDay(oldDay);
        }
    }

    default InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        Level level = player.level();
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof SpawnEggItem)
            {
                return InteractionResult.PASS; // let vanilla spawn a baby
            }
            else if (this.isFood(stack))
            {
                if (this.isHungry())
                {
                    return eatFood(stack, hand, player);
                }
                else
                {
                    if (!level.isClientSide())
                    {
                        showExtraClickInfo(player);
                    }
                }
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    default void showExtraClickInfo(Player player)
    {

    }

    default InteractionResult eatFood(@Nonnull ItemStack stack, InteractionHand hand, Player player)
    {
        final LivingEntity entity = getEntity();
        final Level level = entity.level();
        final RandomSource random = entity.getRandom();

        for (int i = 0; i < 5; i++)
        {
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), entity.getX() + 0.5, entity.getEyeY(), entity.getZ(), Helpers.triangle(random, 0.1f), Helpers.triangle(random, 0.1f), Helpers.triangle(random, 0.1f));
        }

        entity.heal(1f);
        if (!level.isClientSide)
        {
            final long days = getCalendar().getTotalDays();
            setLastFed(days);
            setLastFamiliarityDecay(days); // no decay today
            if (!player.isCreative())
            {
                final @Nullable BowlComponent bowl = stack.get(TFCComponents.BOWL);
                if (bowl != null)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, bowl.bowl().copy());
                }
                if (stack.hasCraftingRemainingItem())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, stack.getCraftingRemainingItem());
                }
                stack.shrink(1);
            }
            if (getAgeType() == Age.CHILD || getFamiliarity() < getAdultFamiliarityCap())
            {
                float familiarity = getFamiliarity() + 0.06f;
                if (getAgeType() != Age.CHILD)
                {
                    familiarity = Math.min(familiarity, getAdultFamiliarityCap());
                }
                setFamiliarity(familiarity);
                if (player instanceof ServerPlayer serverPlayer)
                {
                    TFCAdvancements.FED_ANIMAL.trigger(serverPlayer, entity);
                }
            }
            entity.playSound(eatingSound(stack), 1f, 1f);
        }
        return InteractionResult.SUCCESS;
    }

    default SoundEvent eatingSound(ItemStack food)
    {
        return SoundEvents.PLAYER_BURP;
    }

    default void registerCommonData(SynchedEntityData.Builder builder)
    {
        builder.define(animalData().gender(), true);
        builder.define(animalData().birthday(), 0L);
        builder.define(animalData().familiarity(), 0f);
        builder.define(animalData().uses(), 0);
        builder.define(animalData().fertilized(), false);
        builder.define(animalData().oldDay(), -1L);
        builder.define(animalData().geneticSize(), 16);
        builder.define(animalData().lastFed(), Long.MIN_VALUE);
    }

    default void saveCommonAnimalData(CompoundTag nbt)
    {
        nbt.putBoolean("gender", getGender().toBool());
        nbt.putLong("birth", getBirthDay());
        nbt.putBoolean("fertilized", isFertilized());
        nbt.putFloat("familiarity", getFamiliarity());
        nbt.putInt("uses", getUses());
        nbt.putLong("fed", getLastFed());
        nbt.putLong("decay", getLastFamiliarityDecay());
        nbt.putLong("mating", getMated());
        nbt.putInt("lastAge", getLastAge().ordinal());
        nbt.putLong("oldDay", getOldDay());
        nbt.putInt("geneticSize", getGeneticSize());
    }

    default void readCommonAnimalData(CompoundTag nbt)
    {
        setGender(Gender.valueOf(nbt.getBoolean("gender")));
        setBirthDay(nbt.getLong("birth"));
        setFertilized(nbt.getBoolean("fertilized"));
        setFamiliarity(nbt.getFloat("familiarity"));
        setUses(nbt.getInt("uses"));
        setLastFed(nbt.getLong("fed"));
        setLastFamiliarityDecay(nbt.getLong("decay"));
        setMated(nbt.getLong("mating"));
        setLastAge(Age.valueOf(nbt.getInt("lastAge")));
        setOldDay(nbt.getLong("oldDay"));
        setGeneticSize(EntityHelpers.getIntOrDefault(nbt, "geneticSize", 16));
    }

    default void initCommonAnimalData(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason)
    {
        final var random = getEntity().getRandom();
        setGender(Gender.valueOf(random.nextBoolean()));
        setBirthDay(EntityHelpers.getRandomGrowth(getEntity(), random, getDaysToAdulthood()));
        setFamiliarity(0);
        setOldDay(-1L);
        setUses(0);
        setGeneticSize(Mth.nextInt(random, 4, 18));
        setFertilized(false);
        if (getEntity() instanceof AgeableMob mob)
        {
            mob.setAge(0);
        }
    }

    default boolean isReadyToMate()
    {
        return getAgeType() == Age.ADULT && getFamiliarity() >= READY_TO_MATE_FAMILIARITY && !isFertilized() && !isHungry() && getMated() + MATING_COOLDOWN_DEFAULT_TICKS <= getCalendar().getTicks();
    }

    /**
     * Get this animal gender, female or male
     *
     * @return Gender of this animal
     */
    default Gender getGender()
    {
        return Gender.valueOf(entityData().get(animalData().gender()));
    }

    default boolean isMale()
    {
        return getGender().toBool();
    }

    /**
     * Set this animal gender, used on spawn/birth
     *
     * @param gender the Gender to set to
     */
    default void setGender(Gender gender)
    {
        entityData().set(animalData().gender(), gender.toBool());
    }

    /**
     * Returns the birth day of this animal. Determines how old this animal is
     *
     * @return returns the day this animal has been birth
     */
    default long getBirthDay()
    {
        return entityData().get(animalData().birthday());
    }

    /**
     * Sets the birth day of this animal. Used to determine how old this animal is
     *
     * @param value the day this animal has been birth. Used when this animal spawns.
     */
    default void setBirthDay(long value)
    {
        entityData().set(animalData().birthday(), value);
    }

    default long getOldDay()
    {
        return entityData().get(animalData().oldDay());
    }

    default void setOldDay(long day)
    {
        entityData().set(animalData().oldDay(), day);
    }

    /**
     * Returns the familiarity of this animal
     *
     * @return float value between 0-1.
     */
    default float getFamiliarity()
    {
        return entityData().get(animalData().familiarity());
    }

    /**
     * Set this animal familiarity
     *
     * @param value float value between 0-1.
     */
    default void setFamiliarity(float value)
    {
        entityData().set(animalData().familiarity(), Mth.clamp(value, 0f, 1f));
    }

    /**
     * @return A value [1, 32] for the genetic size scale of the animal.
     */
    default int getGeneticSize()
    {
        return entityData().get(animalData().geneticSize());
    }

    default void setGeneticSize(int size)
    {
        entityData().set(animalData().geneticSize(), Mth.clamp(size, 1, 32));
    }

    default float getAgeScale()
    {
        return AGE_SCALES[getGeneticSize() - 1];
    }

    /**
     * Add a 'use' to the animal
     */
    default void addUses(int uses)
    {
        setUses(getUses() + uses);
    }

    default void setUses(int uses)
    {
        entityData().set(animalData().uses(), uses);
    }

    /**
     * Get the uses this animal has
     */
    default int getUses()
    {
        return entityData().get(animalData().uses());
    }

    /**
     * Returns true if this female is pregnant, or the next time it ovulates, eggs are fertilized.
     *
     * @return true if this female has been fertilized.
     */
    default boolean isFertilized()
    {
        return entityData().get(animalData().fertilized());
    }

    /**
     * Set if this female is fertilized
     *
     * @param value true on fertilization (mating)
     */
    default void setFertilized(boolean value)
    {
        entityData().set(animalData().fertilized(), value);
    }

    /**
     * Do things on fertilization of females (ie: save the male genes for some sort of genetic selection)
     */
    default void onFertilized(TFCAnimalProperties male)
    {
        setFertilized(true);
        setLastFed(getLastFed() - 1);
        male.setLastFed(getLastFed() - 1);
        male.addUses(5); // wear out the male
    }

    default void setBabyTraits(TFCAnimalProperties baby)
    {
        baby.setGender(Gender.valueOf(getEntity().getRandom().nextBoolean()));
        baby.setBirthDay(Calendars.SERVER.getTotalDays());
        baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
    }

    @Nullable
    default AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        // This method may be called multiple times from BreedGoal so we need to check !isFertilized to prevent spammy addition of uses
        if (other != this && this.getGender() == Gender.FEMALE && other instanceof TFCAnimalProperties otherFertile && !isFertilized())
        {
            this.onFertilized(otherFertile);
        }
        else if (other == this)
        {
            final Entity baby = getEntityTypeForBaby().create(level);
            if (baby instanceof TFCAnimalProperties properties && baby instanceof AgeableMob ageable)
            {
                setBabyTraits(properties);
                return ageable;
            }
        }
        return null;
    }

    /**
     * Used to check if breeding is possible without actually needing to be in love
     * Used for animals like horses that can breed across entity types.
     */
    default boolean checkExtraBreedConditions(TFCAnimalProperties other)
    {
        return true;
    }


    /**
     * Get this entity's age, based on birthday and old day. Old Day is set in the animal data tick.
     *
     * @return the Age enum of this entity
     */
    default Age getAgeType()
    {
        final long totalDays = getCalendar().getTotalDays();
        final long oldDay = getOldDay();
        if (oldDay != -1L && totalDays > oldDay)
        {
            return Age.OLD;
        }
        final long adulthoodDays = totalDays - this.getBirthDay();
        if (adulthoodDays > getDaysToAdulthood())
        {
            return Age.ADULT;
        }
        return Age.CHILD;
    }

    /**
     * What is the maximum familiarity obtainable for adults of this animal?
     *
     * @return 0 if not familiarizable at all, [0, 1] for a cap
     */
    default float getAdultFamiliarityCap()
    {
        return animalConfig().familiarityCap().get().floatValue();
    }

    /**
     * Get the number of days needed for this animal to be adult
     *
     * @return number of days
     */
    default int getDaysToAdulthood()
    {
        return animalConfig().adulthoodDays().get();
    }

    /**
     * Get the number of uses for this animal to become old
     *
     * @return number of uses, 0 to disable
     */
    default int getUsesToElderly()
    {
        return animalConfig().uses().get();
    }

    default boolean eatsRottenFood()
    {
        return animalConfig().eatsRottenFood().get();
    }

    /**
     * Some animals can give products (eg: Milk, Wool and Eggs)
     * This function returns if said animal is ready to be worked upon
     * (or if it is ready to lay eggs on it's own)
     *
     * ** Check for everything **
     * this function should return only true if the animal will give it's products upon work
     * (so TOP integration could show this animal is ready)
     *
     * @return true if it is ready for product production
     */
    default boolean isReadyForAnimalProduct()
    {
        return false;
    }

    /**
     * Weaker sub-check of isReadyForAnimalProduct that isn't concerned with familiarity
     */
    default boolean hasProduct()
    {
        return false;
    }

    /**
     * Set this animal on produce cooldown
     * This means that you just sheared a sheep, your chicken just laid eggs, or you just milked your cow
     */
    default void setProductsCooldown()
    {
    }

    /**
     * Returns the number of ticks remaining for this animal to finish its produce cooldown
     *
     * @return ticks remaining to finish cooldown
     */
    default long getProductsCooldown()
    {
        return 0;
    }


    @Override
    default boolean displayMaleCharacteristics()
    {
        return !getEntity().isBaby() && getGender() == TFCAnimalProperties.Gender.MALE;
    }

    @Override
    default boolean displayFemaleCharacteristics()
    {
        return !getEntity().isBaby() && getGender() == TFCAnimalProperties.Gender.FEMALE;
    }

    default boolean isFood(ItemStack stack)
    {
        return (eatsRottenFood() || !FoodCapability.isRotten(stack))
            && Helpers.isItem(stack, getFoodTag());
    }

    default Component getGenderedTypeName()
    {
        return Component.translatable(getEntity().getType().getDescriptionId() + "." + getGender().name().toLowerCase(Locale.ROOT));
    }

    default MutableComponent getProductReadyName()
    {
        return Component.translatable("tfc.jade.product.generic");
    }

    enum Age
    {
        CHILD, ADULT, OLD;

        public static Age valueOf(int value)
        {
            return value == 0 ? CHILD : value == 1 ? ADULT : OLD;
        }
    }

    enum Gender
    {
        MALE, FEMALE;

        public static Gender valueOf(boolean value)
        {
            return value ? MALE : FEMALE;
        }

        public boolean toBool()
        {
            return this == MALE;
        }
    }
}
