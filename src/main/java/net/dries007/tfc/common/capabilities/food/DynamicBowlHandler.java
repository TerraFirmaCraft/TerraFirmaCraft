/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class DynamicBowlHandler extends FoodHandler.Dynamic
{
    private final ItemStack stack;
    private ItemStack bowl;

    public DynamicBowlHandler(ItemStack stack)
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
        this.bowl = bowl.copyWithCount(1);
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

    public static ItemStack onItemUse(ItemStack original, ItemStack result, LivingEntity entity)
    {
        // This is a rare stackable-with-remainder-after-finished-using item
        // See: vanilla honey bottles
        if (entity instanceof ServerPlayer player)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, original);
            player.awardStat(Stats.ITEM_USED.get(original.getItem()));
        }

        // Pull the bowl out first, before we shrink the stack in super.finishUsingItem()
        final ItemStack bowl = FoodCapability.get(original) instanceof DynamicBowlHandler handler ? handler.getBowl() : ItemStack.EMPTY;

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
}
