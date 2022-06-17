/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.GenderedRenderAnimal;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public interface TFCAnimalProperties extends GenderedRenderAnimal
{
    long MATING_COOLDOWN_DEFAULT_TICKS = ICalendar.TICKS_IN_HOUR * 2;
    float READY_TO_MATE_FAMILIARITY = 0.3f;
    float FAMILIARITY_DECAY_LIMIT = 0.3f;

    default LivingEntity getEntity()
    {
        return (LivingEntity) this;
    }

    private SynchedEntityData entityData()
    {
        return getEntity().getEntityData();
    }

    private ICalendar getCalendar()
    {
        return Calendars.get(getEntity().level);
    }

    CommonAnimalData animalData();

    AnimalConfig animalConfig();

    long getLastFamiliarityDecay();

    void setLastFamiliarityDecay(long days);

    void setLastFed(long fed);

    long getLastFed();

    void setMated(long time);

    long getMated();

    /**
     * Is this animal hungry?
     * @return true if this animal can be fed by player
     */
    default boolean isHungry()
    {
        return getLastFed() < Calendars.get().getTotalDays();
    }

    /**
     * Default tag checked by isFood (edible items)
     */
    TagKey<Item> getFoodTag();

    /**
     * Is it time to decay familiarity?
     * If this entity was never fed(eg: newborn, wild) or wasn't fed yesterday (this is the starting of the second day)
     */
    default void tickFamiliarity()
    {
        Level level = getEntity().level;
        if (!level.isClientSide() && level.getGameTime() % 20 == 0)
        {
            if (getLastFamiliarityDecay() > -1 && getLastFamiliarityDecay() + 1 < Calendars.get().getTotalDays())
            {
                float familiarity = getFamiliarity();
                if (familiarity < FAMILIARITY_DECAY_LIMIT)
                {
                    familiarity -= 0.02 * (Calendars.get().getTotalDays() - getLastFamiliarityDecay());
                    setLastFamiliarityDecay(Calendars.get().getTotalDays());
                    this.setFamiliarity(familiarity);
                }
            }
        }
    }

    default InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        Level level = player.level;
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof SpawnEggItem)
            {
                return InteractionResult.PASS; // let vanilla spawn a baby
            }
            else if (this.isFood(stack) && player.isShiftKeyDown())
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

    private InteractionResult eatFood(@Nonnull ItemStack stack, InteractionHand hand, Player player)
    {
        Level level = getEntity().level;
        getEntity().heal(1f);
        if (!level.isClientSide)
        {
            final long days = Calendars.get().getTotalDays();
            setLastFed(days);
            setLastFamiliarityDecay(days); // no decay today
            if (!player.isCreative()) stack.shrink(1);
            if (getAgeType() == Age.CHILD || getFamiliarity() < getAdultFamiliarityCap())
            {
                float familiarity = getFamiliarity() + 0.06f;
                if (getAgeType() != Age.CHILD)
                {
                    familiarity = Math.min(familiarity, getAdultFamiliarityCap());
                }
                setFamiliarity(familiarity);
            }
            getEntity().playSound(SoundEvents.PLAYER_BURP, 1f, 1f);
        }
        return InteractionResult.SUCCESS;
    }


    default void registerCommonData()
    {
        entityData().define(animalData().gender(), true);
        entityData().define(animalData().birthday(), 0);
        entityData().define(animalData().familiarity(), 0f);
        entityData().define(animalData().uses(), 0);
        entityData().define(animalData().fertilized(), false);
    }

    default void saveCommonAnimalData(CompoundTag nbt)
    {
        nbt.putBoolean("gender", getGender().toBool());
        nbt.putInt("birth", getBirthDay());
        nbt.putBoolean("fertilized", isFertilized());
        nbt.putFloat("familiarity", getFamiliarity());
        nbt.putInt("uses", getUses());
        nbt.putLong("fed", getLastFed());
        nbt.putLong("decay", getLastFamiliarityDecay());
        nbt.putLong("mating", getMated());
    }

    default void readCommonAnimalData(CompoundTag nbt)
    {
        setGender(Gender.valueOf(nbt.getBoolean("gender")));
        setBirthDay(nbt.getInt("birth"));
        setFertilized(nbt.getBoolean("fertilized"));
        setFamiliarity(nbt.getFloat("familiarity"));
        setUses(nbt.getInt("uses"));
        setLastFed(nbt.getLong("fed"));
        setLastFamiliarityDecay(nbt.getLong("decay"));
    }

    default void initCommonAnimalData()
    {
        setGender(Gender.valueOf(getEntity().getRandom().nextBoolean()));
        setBirthDay(EntityHelpers.getRandomGrowth(getEntity().getRandom(), getDaysToAdulthood()));
        setFamiliarity(0);
        setFertilized(false);
        if (getEntity() instanceof AgeableMob mob)
        {
            mob.setAge(0);
        }
    }

    default boolean isReadyToMate()
    {
        return getAgeType() == Age.ADULT && getFamiliarity() >= 0.3f && isFertilized() && !isHungry() && getMated() + MATING_COOLDOWN_DEFAULT_TICKS <= Calendars.SERVER.getTicks();
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
    default int getBirthDay()
    {
        return entityData().get(animalData().birthday());
    }

    /**
     * Sets the birth day of this animal. Used to determine how old this animal is
     *
     * @param value the day this animal has been birth. Used when this animal spawns.
     */
    default void setBirthDay(int value)
    {
        entityData().set(animalData().birthday(), value);
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
    default void onFertilized(@Nonnull TFCAnimalProperties male)
    {
        setFertilized(true);
    }

    /**
     * //todo IMPLEMENT??? MIGHT NEED HACKS??? Used by model renderer to scale the size of the animal
     *
     * @return double value between 0(birthday) to 1(full grown adult)
     */
    default double getPercentToAdulthood()
    {
        long deltaDays = getCalendar().getTotalDays() - this.getBirthDay();
        long adulthoodDay = this.getDaysToAdulthood();
        return Math.max(0, Math.min(1, (double) deltaDays / adulthoodDay));
    }

    /**
     * Get this entity age, based on birth
     *
     * @return the Age enum of this entity
     */
    default Age getAgeType()
    {
        long deltaDays = getCalendar().getTotalDays() - this.getBirthDay();
        long adulthoodDay = this.getDaysToAdulthood();
        if (getUses() > getUsesToElderly())
        {
            return Age.OLD; // if enabled, only for familiarizable animals
        }
        else if (deltaDays > adulthoodDay)
        {
            return Age.ADULT;
        }
        else
        {
            return Age.CHILD;
        }
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
        if (!eatsRottenFood())
        {
            Optional<Boolean> rot = stack.getCapability(FoodCapability.CAPABILITY).map(IFood::isRotten);
            if (rot.isPresent() && rot.get())
            {
                return false;
            }
        }
        return Helpers.isItem(stack, getFoodTag());
    }

    default Component getGenderedTypeName()
    {
        return new TranslatableComponent(getEntity().getType().getDescriptionId() + "." + getGender().name().toLowerCase(Locale.ROOT));
    }

    enum Age
    {
        CHILD, ADULT, OLD
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
