/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.misc.ThrownJavelin;
import net.dries007.tfc.util.Helpers;

/**
 * Implementation based on {@link TridentItem}
 */
public class JavelinItem extends SwordItem
{
    public JavelinItem(Tier tier, Properties properties)
    {
        super(tier, properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int ticksLeft)
    {
        if (entity instanceof Player player)
        {
            int i = this.getUseDuration(stack, entity) - ticksLeft;
            if (i >= 10)
            {
                if (!level.isClientSide)
                {
                    Helpers.damageItem(stack, player, entity.getUsedItemHand());

                    ThrownJavelin javelin = new ThrownJavelin(level, player, stack);
                    javelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                    if (player.getAbilities().instabuild)
                    {
                        javelin.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    level.addFreshEntity(javelin);
                    level.playSound(null, javelin, TFCSounds.JAVELIN_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild)
                    {
                        player.getInventory().removeItem(stack);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        if (held.getDamageValue() >= held.getMaxDamage() - 1)
        {
            return InteractionResultHolder.fail(held);
        }
        else
        {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(held);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        tooltip.add(Component.translatable("tfc.tooltip.javelin.thrown_damage", String.format("%.0f", getThrownDamage())).withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction)
    {
        return super.canPerformAction(stack, toolAction) && toolAction != ItemAbilities.SWORD_SWEEP;
    }

    public float getThrownDamage()
    {
        return 1.5f * getTier().getAttackDamageBonus();
    }
}