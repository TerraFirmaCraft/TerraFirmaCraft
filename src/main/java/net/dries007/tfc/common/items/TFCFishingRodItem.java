/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.dries007.tfc.common.entities.TFCFishingHook;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class TFCFishingRodItem extends FishingRodItem
{
    public static ItemStack getBaitItem(ItemStack rod)
    {
        if (!rod.isEmpty() && rod.getItem() instanceof TFCFishingRodItem)
        {
            CompoundTag tag = rod.getTagElement("bait");
            if (tag != null)
            {
                return ItemStack.of(tag);
            }
        }
        return ItemStack.EMPTY;
    }

    public static BaitType getBaitType(ItemStack bait)
    {
        if (!bait.isEmpty())
        {
            if (Helpers.isItem(bait, TFCTags.Items.SMALL_FISHING_BAIT))
            {
                return BaitType.SMALL;
            }
            else if (Helpers.isItem(bait, TFCTags.Items.LARGE_FISHING_BAIT))
            {
                return BaitType.LARGE;
            }
        }
        return BaitType.NONE;
    }

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
            int dmg = player.fishing.retrieve(rod);
            if (!level.isClientSide)
            {
                rod.hurtAndBreak(dmg, player, p -> p.broadcastBreakEvent(hand));
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            level.gameEvent(player, GameEvent.FISHING_ROD_REEL_IN, player);
        }
        else
        {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!level.isClientSide)
            {
                ItemStack bait = getBaitItem(rod);
                if (bait.isEmpty())
                {
                    player.displayClientMessage(Helpers.translatable("tfc.fishing.no_bait"), true);
                }
                else
                {
                    level.addFreshEntity(new TFCFishingHook(player, level, getFishingStrength(), bait.copy()));
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            level.gameEvent(player, GameEvent.FISHING_ROD_CAST, player);
        }

        return InteractionResultHolder.sidedSuccess(rod, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag)
    {
        ItemStack bait = TFCFishingRodItem.getBaitItem(stack);
        if (!bait.isEmpty())
        {
            text.add(Helpers.translatable("tfc.tooltip.fishing.bait").append(bait.getHoverName()));
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess carriedSlot)
    {
        final BaitType type = getBaitType(carried);
        if (type != BaitType.NONE && !player.isCreative() && action == ClickAction.SECONDARY)
        {
            if (type == BaitType.SMALL && Helpers.isItem(stack, TFCTags.Items.HOLDS_SMALL_FISHING_BAIT))
            {
                stack.getOrCreateTag().put("bait", carried.split(1).save(new CompoundTag()));
                return true;
            }
            else if (type == BaitType.LARGE && Helpers.isItem(stack, TFCTags.Items.HOLDS_LARGE_FISHING_BAIT))
            {
                stack.getOrCreateTag().put("bait", carried.split(1).save(new CompoundTag()));
                return true;
            }
        }
        return false;
    }

    public float getFishingStrength()
    {
        return tier.getSpeed() / 12f;
    }

    public enum BaitType
    {
        NONE,
        SMALL,
        LARGE;
    }
}
