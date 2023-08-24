/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.common.capabilities.glass.GlassWorkData;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;

public class GlassBlowpipeItem extends BlowpipeItem
{
    private static ItemStack getOtherHandItem(Player player)
    {
        return player.getItemInHand(player.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
    }

    private static ItemStack getOtherHandItem(Player player, InteractionHand hand)
    {
        return player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
    }

    private final float breakChance;

    public GlassBlowpipeItem(Properties properties,float breakChance)
    {
        super(properties);
        this.breakChance = breakChance;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        final ItemStack otherItem = getOtherHandItem(player, hand);
        final GlassOperation op = GlassOperation.get(otherItem, player);
        if (op != null)
        {
            if (!op.hasRequiredTemperature(held))
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.glass.not_hot_enough"), true);
                return InteractionResultHolder.fail(held);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(held);
        }
        return InteractionResultHolder.pass(held);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticksLeft)
    {
        if (ticksLeft % 30 == 0 && entity instanceof Player player)
        {
            final GlassOperation op = GlassOperation.get(getOtherHandItem(player), player);
            if (op != null)
            {
                level.playSound(null, entity.blockPosition(), op.getSound(), SoundSource.PLAYERS, 1f, 0.8f + (float) (player.getLookAngle().y / 2f));
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if (entity instanceof Player player)
        {
            stopUsing(player, stack);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    protected void stopUsing(LivingEntity entity, ItemStack stack)
    {
        if (entity instanceof Player player)
        {
            final ItemStack otherHand = getOtherHandItem(player);
            final GlassOperation op = GlassOperation.get(otherHand, player);
            if (op != null && stack.getItem() instanceof GlassBlowpipeItem)
            {
                GlassWorkData.apply(stack, op);

                final Level level = entity.level();
                level.getRecipeManager().getRecipeFor(TFCRecipeTypes.GLASSWORKING.get(), new ItemStackInventory(stack), level).ifPresent(recipe -> {
                    final boolean broken = level.random.nextFloat() < breakChance;
                    entity.setItemInHand(player.getUsedItemHand(), broken ? ItemStack.EMPTY : BlowpipeItem.transform(stack.getItem()).getDefaultInstance());
                    ItemHandlerHelper.giveItemToPlayer(player, recipe.getResultItem(level.registryAccess()));
                    level.playSound(null, entity.blockPosition(), broken ? SoundEvents.ITEM_BREAK : SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);
                });
            }
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            Helpers.getAllTagValues(TFCTags.Items.ALL_BLOWPIPES, ForgeRegistries.ITEMS).forEach(item -> player.getCooldowns().addCooldown(item, 80));
        }
    }
}
