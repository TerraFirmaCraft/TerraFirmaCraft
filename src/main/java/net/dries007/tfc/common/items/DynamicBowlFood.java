/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;


import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicBowlFood extends DecayingItem
{
    public DynamicBowlFood(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new DynamicBowlHandler(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        // This is a rare stackable-with-remainder-after-finished-using item
        // See: vanilla honey bottles
        if (entity instanceof ServerPlayer player)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        // Pull the bowl out first, before we shrink the stack in super.finishUsingItem()
        final ItemStack bowl = stack.getCapability(FoodCapability.CAPABILITY)
            .map(cap -> cap instanceof DynamicBowlHandler handler ? handler.getBowl() : ItemStack.EMPTY)
            .orElse(ItemStack.EMPTY);
        final ItemStack result = super.finishUsingItem(stack.copy(), level, entity); // Copy the stack, so we can still refer to the original

        if (result.isEmpty())
        {
            return bowl;
        }
        else if (entity instanceof Player player && !player.getAbilities().instabuild)
        {
            // In non-creative, we still need to give the player an empty bowl, but we must also return the result here, as it is non-empty
            // The super() call to finishUsingItem will handle decrementing the stack - only in non-creative - for us already.
            ItemHandlerHelper.giveItemToPlayer(player, bowl);
        }
        return result;
    }

    public static class DynamicBowlHandler extends FoodHandler.Dynamic
    {
        private final ItemStack stack;
        private ItemStack bowl;

        protected DynamicBowlHandler(ItemStack stack)
        {
            this.stack = stack;
            final CompoundTag tag = stack.getOrCreateTag();
            bowl = tag.contains("bowl") ? ItemStack.of(tag.getCompound("bowl")) : ItemStack.EMPTY;
        }

        public ItemStack getBowl()
        {
            return bowl.copy();
        }

        public void setBowl(ItemStack bowl)
        {
            this.bowl = Helpers.copyWithSize(bowl, 1);
            save();
        }

        private void save()
        {
            final CompoundTag tag = stack.getOrCreateTag();
            if (!bowl.isEmpty())
            {
                tag.put("bowl", bowl.save(new CompoundTag()));
            }
        }
    }
}
