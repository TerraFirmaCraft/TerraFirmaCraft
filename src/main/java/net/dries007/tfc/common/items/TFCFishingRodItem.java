/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.BaitComponent;
import net.dries007.tfc.common.component.BaitType;
import net.dries007.tfc.common.entities.misc.TFCFishingHook;
import net.dries007.tfc.util.Helpers;

public class TFCFishingRodItem extends FishingRodItem
{
    public static boolean isThisTheHeldRod(Player player, ItemStack stack)
    {
        boolean main = player.getMainHandItem() == stack;
        boolean off = player.getOffhandItem() == stack;
        if (player.getMainHandItem().getItem() instanceof FishingRodItem)
        {
            off = false;
        }
        return main || off;
    }

    private final Tier tier;

    public TFCFishingRodItem(Properties properties, Tier tier)
    {
        super(properties);
        this.tier = tier;
    }

    /**
     * Copy of the super method to use our own entity
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack rod = player.getItemInHand(hand);
        if (player.fishing != null)
        {
            final int dmg = player.fishing.retrieve(rod);
            Helpers.damageItem(rod, dmg, player, hand);

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }
        else
        {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!level.isClientSide)
            {
                ItemStack bait = BaitComponent.getBait(rod);
                if (bait.isEmpty())
                {
                    player.displayClientMessage(Component.translatable("tfc.fishing.no_bait"), true);
                }
                else
                {
                    level.addFreshEntity(new TFCFishingHook(player, level, getFishingStrength(), bait.copy()));
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(rod, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        final ItemStack bait = BaitComponent.getBait(stack);
        if (!bait.isEmpty())
        {
            tooltip.add(Component.translatable("tfc.tooltip.fishing.bait").append(bait.getHoverName()));
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess carriedSlot)
    {
        final BaitType type = BaitType.getType(carried);
        if (type != BaitType.NONE && !player.isCreative() && action == ClickAction.SECONDARY && slot.allowModification(player))
        {
            if ((type == BaitType.SMALL && Helpers.isItem(stack, TFCTags.Items.HOLDS_SMALL_FISHING_BAIT)) ||
                (type == BaitType.LARGE && Helpers.isItem(stack, TFCTags.Items.HOLDS_LARGE_FISHING_BAIT))
            )
            {
                BaitComponent.setBait(stack, carried.split(1));
                return true;
            }
        }
        return false;
    }

    public float getFishingStrength()
    {
        return tier.getSpeed() / 12f + 1f;
    }
}
