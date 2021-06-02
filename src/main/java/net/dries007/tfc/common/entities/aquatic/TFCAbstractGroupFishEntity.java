package net.dries007.tfc.common.entities.aquatic;

import java.util.Optional;
import java.util.Set;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.AquaticMovementController;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.mixin.entity.ai.goal.GoalSelectorAccessor;
import net.dries007.tfc.mixin.entity.passive.fish.AbstractFishEntityAccessor;

public abstract class TFCAbstractGroupFishEntity extends AbstractGroupFishEntity implements IBucketableOceanFish
{
    public TFCAbstractGroupFishEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn)
    {
        super(type, worldIn);
        moveControl = new AquaticMovementController(this, true, 1);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        Set<PrioritizedGoal> availableGoals = ((GoalSelectorAccessor) goalSelector).getAvailableGoals();
        availableGoals.removeIf(priority -> priority.getGoal() instanceof AvoidEntityGoal);

        goalSelector.addGoal(2, new TFCAvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 5.0D, 5.4D));
    }

    @Override
    public ItemStack getSaltyBucketItemStack()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return ItemStack.EMPTY;
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
    {
        return TFCAbstractGroupFishEntity.handleInteract(this, level, player, hand, getBucketItemStack(), getSaltyBucketItemStack()).orElseGet(() -> super.mobInteract(player, hand));
    }

    public static Optional<ActionResultType> handleInteract(AbstractFishEntity fish, World level, PlayerEntity player, Hand hand, ItemStack freshBucket, ItemStack saltyBucket)
    {
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.getItem() == TFCItems.SALT_WATER_BUCKET.get() && !saltyBucket.isEmpty() && fish.isAlive())
        {
            return Optional.of(TFCAbstractGroupFishEntity.handleBucketing(fish, level, player, hand, heldStack, saltyBucket));
        }
        else if (heldStack.getItem() == Items.WATER_BUCKET && freshBucket.isEmpty() && fish.isAlive())
        {
            return Optional.of(ActionResultType.sidedSuccess(level.isClientSide));
        }
        else
        {
            return Optional.empty();
        }
    }

    private static ActionResultType handleBucketing(AbstractFishEntity fish, World level, PlayerEntity player, Hand hand, ItemStack heldStack, ItemStack bucketStack)
    {
        fish.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0F, 1.0F);
        heldStack.shrink(1);
        ((AbstractFishEntityAccessor) fish).invoke$saveToBucketTag(bucketStack);
        if (!level.isClientSide) CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) player, bucketStack);

        if (heldStack.isEmpty())
        {
            player.setItemInHand(hand, bucketStack);
        }
        else if (!player.inventory.add(bucketStack))
        {
            player.drop(bucketStack, false);
        }

        fish.remove();
        return ActionResultType.sidedSuccess(level.isClientSide);
    }
}
