package net.dries007.tfc.common.entities;

import java.util.List;
import java.util.Optional;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.land.TFCAnimal;
import net.dries007.tfc.common.entities.land.TFCAnimalProperties;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.util.calendar.Calendars;

public class EntityHelpers
{
    public static final EntityDataSerializer<Long> LONG_ENTITY_SERIALIZER = new EntityDataSerializer<>()
    {
        @Override
        public void write(FriendlyByteBuf pBuffer, Long pValue)
        {
            pBuffer.writeVarLong(pValue);
        }
        @Override
        public Long read(FriendlyByteBuf pBuffer)
        {
            return pBuffer.readVarLong();
        }
        @Override
        public Long copy(Long pValue)
        {
            return pValue;
        }
    };

    public static void insertTFCAvoidGoal(PathfinderMob mob, GoalSelector selector, int priority)
    {
        selector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof AvoidEntityGoal);
        selector.addGoal(priority, new TFCAvoidEntityGoal<>(mob, Player.class, 8.0F, 5.0D, 5.4D));
    }

    public static void insertCommonPreyAI(TFCAnimal animal, GoalSelector goalSelector)
    {
        goalSelector.addGoal(0, new FloatGoal(animal));
        goalSelector.addGoal(1, new PanicGoal(animal, 1.25D));
        goalSelector.addGoal(3, new BreedGoal(animal, 1.0D));
        goalSelector.addGoal(4, new TemptGoal(animal, 1.2D, Ingredient.of(animal.getFoodTag()), false));
        goalSelector.addGoal(5, new FollowParentGoal(animal, 1.1D));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(animal, 1.0D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(animal, Player.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(animal));
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

    /**
     * Gets a random growth for this animal
     * ** Static ** So it can be used by class constructor
     *
     * @param daysToAdult number of days needed for this animal to be an adult
     * @return a random long value containing the days of growth for this animal to spawn
     * **Always spawn adults** (so vanilla respawn mechanics only creates adults of this animal)
     */
    public static int getRandomGrowth(Level level, int daysToAdult)
    {
        int lifeTimeDays = daysToAdult + level.random.nextInt(daysToAdult * 4);
        return (int) Calendars.get(level).getTotalDays() - lifeTimeDays;
    }

    /**
     * Find and charms a near female animal of this animal
     * Used by males to try mating with females
     */
    public static <T extends Animal & TFCAnimalProperties> void findFemaleMate(T maleAnimal)
    {
        List<? extends Animal> list = maleAnimal.level.getEntitiesOfClass(maleAnimal.getClass(), maleAnimal.getBoundingBox().inflate(8.0D));
        for (Animal femaleAnimal : list)
        {
            TFCAnimalProperties female = (TFCAnimalProperties) femaleAnimal;
            if (female.getGender() == TFCAnimalProperties.Gender.FEMALE && !femaleAnimal.isInLove() && female.isReadyToMate())
            {
                femaleAnimal.setInLove(null);
                maleAnimal.setInLove(null);
                break;
            }
        }
    }
}
