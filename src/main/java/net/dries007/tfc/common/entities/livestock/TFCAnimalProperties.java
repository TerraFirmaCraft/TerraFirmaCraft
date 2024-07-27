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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.ItemStackComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.entities.BrainAnimalBehavior;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.GenderedRenderAnimal;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.calendar.ICalendar;

public interface TFCAnimalProperties extends GenderedRenderAnimal, BrainAnimalBehavior, CommonAnimalBehavior
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

    /**
     * Is this animal hungry?
     *
     * @return true if this animal can be fed by player
     */
    default boolean isHungry()
    {
        return calendar().getTicks() > getLastFedTick() + ICalendar.TICKS_IN_DAY;
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
        final float familiarity = getFamiliarity();
        final long currentTick = calendar().getTicks();
        final long familiarityDecayTick = getLastFamiliarityTick();

        if (
            familiarity > 0 && // There is familiarity to decay,
            familiarityDecayTick != -1 && // And we have a record of a date
            currentTick > familiarityDecayTick + ICalendar.TICKS_IN_DAY && // And it's been at least a day since we decayed previously
            familiarity < TFCConfig.SERVER.familiarityDecayLimit.get() // And our familiarity is below the level where it won't decay
        )
        {
            // Then familiarity decays, which is based on the last time this animal was familiarized vs. the current time. Modifying
            // the familiarity will reset the last decay tick
            setFamiliarity(familiarity - 0.02f * (currentTick - familiarityDecayTick));
        }
        final Age age = getAgeType();
        if (age != getLastAge())
        {
            setLastAge(age);
            getEntity().refreshDimensions();
        }
        // because this is a random value it's not deterministic, we will allow the entity to sync it on its own
        if (!level().isClientSide && age == Age.ADULT && getUses() > getUsesToElderly() && getOldTick() == -1L)
        {
            setOldTick(calendar().getTicks() + (1L + getEntity().getRandom().nextInt(5)) * ICalendar.TICKS_IN_DAY);
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

    default void showExtraClickInfo(Player player) {}

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
            setLastFedNow();
            if (!player.isCreative())
            {
                final @Nullable ItemStackComponent bowl = stack.get(TFCComponents.BOWL);
                if (bowl != null)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, bowl.stack().copy());
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

    default void saveCommonAnimalData(CompoundTag nbt)
    {
        nbt.putBoolean("gender", isMale());
        nbt.putByte("lastAge", (byte) getLastAge().ordinal());
        nbt.putInt("geneticSize", getGeneticSize());
        nbt.putInt("uses", getUses());
        nbt.putBoolean("fertilized", isFertilized());
        nbt.putFloat("familiarity", getFamiliarity());
        nbt.putLong("lastFamiliarityTick", getEntityData().get(animalData().lastFamiliarityTick()));
        nbt.putLong("birthTick", getBirthTick());
        nbt.putLong("oldTick", getOldTick());
        nbt.putLong("lastFedTick", getLastFedTick());
        nbt.putLong("lastMateTick", getLastMateTick());
    }

    default void readCommonAnimalData(CompoundTag nbt)
    {
        setGender(nbt.getBoolean("gender") ? Gender.MALE : Gender.FEMALE);
        setLastAge(Age.valueOf(nbt.getInt("lastAge")));
        setGeneticSize(EntityHelpers.getIntOrDefault(nbt, "geneticSize", 16));
        setUses(nbt.getInt("uses"));
        setFertilized(nbt.getBoolean("fertilized"));
        getEntityData().set(animalData().familiarity(), nbt.getFloat("familiarity")); // Don't use the behavior method, it updates familiarity
        getEntityData().set(animalData().lastFamiliarityTick(), nbt.getLong("lastFamiliarityTick"));
        setBirthTick(nbt.getLong("birthTick"));
        setOldTick(nbt.getLong("oldTick"));
        getEntityData().set(animalData().lastFedTick(), nbt.getLong("lastFedTick")); // Don't use the behavior method, it updates familiarity
        getEntityData().set(animalData().lastMateTick(), nbt.getLong("lastMateTick"));
    }

    default void initCommonAnimalData(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason)
    {
        final var random = getEntity().getRandom();

        setGender(Gender.valueOf(random.nextBoolean()));
        setBirthTick(EntityHelpers.getRandomGrowth(getEntity(), random, getDaysToAdulthood()));
        setOldTick(-1L);

        setFamiliarity(0);
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
        return getAgeType() == Age.ADULT
            && getFamiliarity() >= READY_TO_MATE_FAMILIARITY
            && !isFertilized()
            && !isHungry()
            && getLastMateTick() + MATING_COOLDOWN_DEFAULT_TICKS <= calendar().getTicks();
    }

    /**
     * @return A value [1, 32] for the genetic size scale of the animal.
     */
    default int getGeneticSize()
    {
        return getEntityData().get(animalData().geneticSize());
    }

    default void setGeneticSize(int size)
    {
        getEntityData().set(animalData().geneticSize(), (byte) Mth.clamp(size, 1, 32));
        final var instance = getEntity().getAttribute(Attributes.SCALE);
        if (instance != null)
        {
            instance.setBaseValue(AGE_SCALES[getGeneticSize() - 1]);
        }
    }

    default float getAgeScale()
    {
        return AGE_SCALES[getGeneticSize() - 1];
    }

    /**
     * Do things on fertilization of females (ie: save the male genes for some sort of genetic selection)
     */
    default void onFertilized(TFCAnimalProperties male)
    {
        setFertilized(true);
        setLastFedYesterday();
        male.setLastFedYesterday();
        male.addUses(5); // wear out the male
    }

    default void setBabyTraits(TFCAnimalProperties baby)
    {
        baby.setGender(Gender.valueOf(getEntity().getRandom().nextBoolean()));
        baby.setBirthTickNow();
        baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
    }

    @Nullable
    default AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        // This method may be called multiple times from BreedGoal so we need to check !isFertilized to prevent spammy addition of uses
        if (other != this && other instanceof TFCAnimalProperties otherFertile && !isFertilized())
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
        final long currentTick = calendar().getTicks();
        final long oldTick = getOldTick();
        if (oldTick != -1 && currentTick > oldTick)
        {
            return Age.OLD;
        }
        final long adultTick = getBirthTick() + (long) animalConfig().adulthoodDays().get() * ICalendar.TICKS_IN_DAY;
        if (currentTick > adultTick)
        {
            return Age.ADULT;
        }
        return Age.CHILD;
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
    default void setProductsCooldown() {}

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
        return !getEntity().isBaby() && isMale();
    }

    @Override
    default boolean displayFemaleCharacteristics()
    {
        return !getEntity().isBaby() && isFemale();
    }

    default boolean isFood(ItemStack stack)
    {
        return (eatsRottenFood() || !FoodCapability.isRotten(stack)) && Helpers.isItem(stack, getFoodTag());
    }

    default Component getGenderedTypeName()
    {
        return Component.translatable(getEntity().getType().getDescriptionId() + "." + getGender().name().toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings("unused") // used by Jade
    default MutableComponent getProductReadyName()
    {
        return Component.translatable("tfc.jade.product.generic");
    }
}
