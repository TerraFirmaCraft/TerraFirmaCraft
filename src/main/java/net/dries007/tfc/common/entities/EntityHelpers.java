/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.events.AnimalProductEvent;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public final class EntityHelpers
{
    public static final EntityDataSerializer<Long> LONG_SERIALIZER = new EntityDataSerializer<>()
    {
        @Override
        public void write(FriendlyByteBuf buf, Long value)
        {
            buf.writeVarLong(value);
        }
        @Override
        public Long read(FriendlyByteBuf buf)
        {
            return buf.readVarLong();
        }
        @Override
        public Long copy(Long value)
        {
            return value;
        }
    };

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
            ChunkDataProvider.get(worldGenLevel).get(new ChunkPos(pos)) :
            ChunkData.get(level, pos);
    }

    /**
     * Fluid Sensitive version of Bucketable#bucketMobPickup
     */
    public static <T extends LivingEntity & Bucketable> Optional<InteractionResult> bucketMobPickup(Player player, InteractionHand hand, T entity)
    {
        ItemStack held = player.getItemInHand(hand);
        ItemStack bucketItem = entity.getBucketItemStack();
        if (bucketItem.getItem() instanceof MobBucketItem mobBucket && held.getItem() instanceof BucketItem heldBucket)
        {
            // Verify that the one you're holding and the corresponding mob bucket contain the same fluid
            if (mobBucket.getFluid().isSame(heldBucket.getFluid()) && entity.isAlive())
            {
                entity.playSound(entity.getPickupSound(), 1.0F, 1.0F);
                entity.saveToBucketTag(bucketItem);
                ItemStack itemstack2 = ItemUtils.createFilledResult(held, player, bucketItem, false);
                player.setItemInHand(hand, itemstack2);
                Level level = entity.level;
                if (!level.isClientSide)
                {
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucketItem);
                }

                entity.discard();
                return Optional.of(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
        return Optional.empty();
    }

    @Deprecated
    public static long getRandomGrowth(Random random, int daysToAdult)
    {
        if (random.nextFloat() < 0.05f) // baby chance
        {
            return Calendars.get().getTotalDays() + random.nextInt(10);
        }
        int lifeTimeDays = daysToAdult + random.nextInt(daysToAdult);
        return Calendars.get().getTotalDays() - lifeTimeDays;
    }

    /**
     * Gets a random growth for this animal
     * ** Static ** So it can be used by class constructor
     *
     * @param daysToAdult number of days needed for this animal to be an adult
     * @return a random long value containing the days of growth for this animal to spawn
     */
    public static long getRandomGrowth(Entity entity, Random random, int daysToAdult)
    {
        if (random.nextFloat() < 0.05f) // baby chance
        {
            return Calendars.get(entity.level).getTotalDays() + random.nextInt(10);
        }
        int lifeTimeDays = daysToAdult + random.nextInt(daysToAdult);
        return Calendars.get(entity.level).getTotalDays() - lifeTimeDays;
    }

    public static void setNullableAttribute(LivingEntity entity, Attribute attribute, double baseValue)
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
        List<? extends Animal> list = maleAnimal.level.getEntitiesOfClass(Animal.class, maleAnimal.getBoundingBox().inflate(8.0D));
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
        return entity.isOnGround() && entity.getDeltaMovement().lengthSqr() > 1.0E-6D && !entity.isInWaterOrBubble();
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

    public static boolean pluck(Player player, InteractionHand hand, LivingEntity entity)
    {
        if (player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown() && !entity.level.isClientSide && (entity.getHealth() / entity.getMaxHealth() > 0.15001f))
        {
            entity.hurt(TFCDamageSources.PLUCK, entity.getMaxHealth() * 0.15f);
            ItemStack feather = new ItemStack(Items.FEATHER, Mth.nextInt(entity.getRandom(), 1, 3));
            if (entity instanceof TFCAnimalProperties properties && properties.getAgeType() == TFCAnimalProperties.Age.ADULT)
            {
                AnimalProductEvent event = new AnimalProductEvent(entity.level, entity.blockPosition(), player, properties, feather, ItemStack.EMPTY, 1);
                if (!MinecraftForge.EVENT_BUS.post(event))
                {
                    properties.addUses(event.getUses());
                    ItemHandlerHelper.giveItemToPlayer(player, event.getProduct());
                }
            }
            else
            {
                ItemHandlerHelper.giveItemToPlayer(player, feather);
            }
            return true;
        }
        return false;
    }
}
