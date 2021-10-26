/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import net.dries007.tfc.common.entities.TFCFishingHook;

public class TFCFishingRodItem extends FishingRodItem
{
    public TFCFishingRodItem(Properties properties)
    {
        super(properties);
    }

    /**
     * Copy of the super method to use our own entity
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.fishing != null)
        {
            if (!level.isClientSide)
            {
                int dmg = player.fishing.retrieve(itemstack);
                itemstack.hurtAndBreak(dmg, player, p -> p.broadcastBreakEvent(hand));
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            level.gameEvent(player, GameEvent.FISHING_ROD_REEL_IN, player);
        }
        else
        {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!level.isClientSide)
            {
                level.addFreshEntity(new TFCFishingHook(player, level));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            level.gameEvent(player, GameEvent.FISHING_ROD_CAST, player);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
