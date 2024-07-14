/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public final class EntityHelpers
{
    public static void replaceAvoidEntityGoal(PathfinderMob mob, GoalSelector selector, int priority)
    {
        selector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof AvoidEntityGoal);
        selector.addGoal(priority, new TFCAvoidEntityGoal<>(mob, Player.class, 8.0F, 5.0D, 5.4D));
    }

    public static void removeGoalOfPriority(GoalSelector selector, int priority)
    {
        selector.getAvailableGoals().removeIf(wrapped -> wrapped.getPriority() == priority);
    }

    public static void removeGoalOfClass(GoalSelector selector, Class<?> clazz)
    {
        selector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal().getClass() == clazz);
    }

    public static ChunkData getChunkDataForSpawning(ServerLevelAccessor level, BlockPos pos)
    {
        return level instanceof WorldGenRegion worldGenLevel ?
            ChunkDataProvider.get(worldGenLevel).get(worldGenLevel.getChunk(pos)) :
            ChunkData.get(level, pos);
    }

    /**
     * Fluid Sensitive version of {@link Bucketable#bucketMobPickup}
     */
    public static <T extends LivingEntity & Bucketable> Optional<InteractionResult> bucketMobPickup(Player player, InteractionHand hand, T entity)
    {
        ItemStack held = player.getItemInHand(hand);
        ItemStack bucketItem = entity.getBucketItemStack();
        if (bucketItem.getItem() instanceof MobBucketItem && held.getItem() instanceof BucketItem)
        {
            // Verify that the one you're holding and the corresponding mob bucket contain the same fluid
            if (FluidStack.isSameFluidSameComponents(
                FluidHelpers.getContainedFluid(bucketItem),
                FluidHelpers.getContainedFluid(held)
            ) && entity.isAlive())
            {
                entity.playSound(entity.getPickupSound(), 1.0F, 1.0F);
                entity.saveToBucketTag(bucketItem);
                final ItemStack filledStack = ItemUtils.createFilledResult(held, player, bucketItem, false);
                player.setItemInHand(hand, filledStack);
                if (player instanceof ServerPlayer serverPlayer)
                {
                    CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, bucketItem);
                }

                entity.discard();
                return Optional.of(InteractionResult.sidedSuccess(entity.level().isClientSide));
            }
        }
        return Optional.empty();
    }

    /**
     * Gets a random growth for this animal
     * ** Static ** So it can be used by class constructor
     *
     * @param daysToAdult number of days needed for this animal to be an adult
     * @return a random long value containing the days of growth for this animal to spawn
     */
    public static long getRandomGrowth(Entity entity, RandomSource random, int daysToAdult)
    {
        if (random.nextFloat() < 0.05f) // baby chance
        {
            return Calendars.get(entity.level()).getTotalDays() + random.nextInt(10);
        }
        int lifeTimeDays = daysToAdult + random.nextInt(daysToAdult);
        return Calendars.get(entity.level()).getTotalDays() - lifeTimeDays;
    }

    public static void setNullableAttribute(LivingEntity entity, Holder<Attribute> attribute, double baseValue)
    {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null)
        {
            instance.setBaseValue(baseValue);
        }
    }

    public static int getIntOrDefault(CompoundTag nbt, String key, int defaultInt)
    {
        return nbt.contains(key, Tag.TAG_INT) ? nbt.getInt(key) : defaultInt;
    }

    public static String getStringOrDefault(CompoundTag nbt, String key, String defaultString)
    {
        return nbt.contains(key, Tag.TAG_STRING) ? nbt.getString(key) : defaultString;
    }

    public static float getFloatOrDefault(CompoundTag nbt, String key, float defaultFloat)
    {
        return nbt.contains(key, Tag.TAG_FLOAT) ? nbt.getFloat(key) : defaultFloat;
    }

    public static long getLongOrDefault(CompoundTag nbt, String key, long defaultLong)
    {
        return nbt.contains(key, Tag.TAG_LONG) ? nbt.getLong(key) : defaultLong;
    }

    /**
     * Find and charms a near female animal of this animal
     * Used by males to try mating with females
     *
     * This MUST be implemented for animals that DO NOT use Brain AI, and need Goal AI (like horses)
     * See also {@link TFCAnimalProperties#checkExtraBreedConditions(TFCAnimalProperties)}
     */
    public static <T extends Animal & TFCAnimalProperties> void findFemaleMate(T maleAnimal)
    {
        List<? extends Animal> list = maleAnimal.level().getEntitiesOfClass(Animal.class, maleAnimal.getBoundingBox().inflate(8.0D));
        for (Animal femaleAnimal : list)
        {
            if (femaleAnimal instanceof TFCAnimalProperties femaleData && femaleData.getGender() == TFCAnimalProperties.Gender.FEMALE && !femaleAnimal.isInLove() && femaleData.isReadyToMate() && femaleData.checkExtraBreedConditions(maleAnimal))
            {
                femaleAnimal.setInLove(null);
                maleAnimal.setInLove(null);
                break;
            }
        }
    }

    public static boolean isMovingOnLand(Entity entity)
    {
        return entity.onGround() && entity.getDeltaMovement().lengthSqr() > 1.0E-6D && !entity.isInWaterOrBubble();
    }

    public static boolean isMovingInWater(Entity entity)
    {
        return entity.isInWaterOrBubble();
    }

    public static boolean startOrStop(AnimationState state, boolean go, int tickCount)
    {
        if (go)
        {
            state.startIfStopped(tickCount);
        }
        else
        {
            state.stop();
        }
        return go;
    }

    public static double createOffspringAttribute(double value1, double value2, double min, double max, RandomSource random)
    {
        if (max <= min)
        {
            throw new IllegalArgumentException("Incorrect range for an attribute");
        }
        else
        {
            value1 = Mth.clamp(value1, min, max);
            value2 = Mth.clamp(value2, min, max);
            double wiggleRoom = 0.15 * (max - min);
            double randomRange = Math.abs(value1 - value2) + wiggleRoom * 2.0;
            double average = (value1 + value2) / 2.0;
            double gaussian = (random.nextDouble() + random.nextDouble() + random.nextDouble()) / 3.0 - 0.5;
            double newValue = average + randomRange * gaussian;
            double diff;
            if (newValue > max)
            {
                diff = newValue - max;
                return max - diff;
            }
            else if (newValue < min)
            {
                diff = min - newValue;
                return min + diff;
            }
            else
            {
                return newValue;
            }
        }
    }


}
