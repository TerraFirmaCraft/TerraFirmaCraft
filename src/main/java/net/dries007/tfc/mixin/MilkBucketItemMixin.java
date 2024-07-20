/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.config.TFCConfig;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin extends Item
{
    private MilkBucketItemMixin(Properties properties)
    {
        super(properties);
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void changeBehaviorOfDrinkingMilk(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir)
    {
        if (!TFCConfig.SERVER.enableVanillaDrinkingMilkClearsPotionEffects.get())
        {
            if (entity instanceof Player player)
            {
                IPlayerInfo.get(player).eat(FoodData.MILK);
            }
            if (entity instanceof ServerPlayer player)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
                player.awardStat(Stats.ITEM_USED.get(this));
            }

            if (entity instanceof Player player && !player.getAbilities().instabuild)
            {
                stack.shrink(1);
            }

            cir.setReturnValue(stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack);
        }
    }
}
