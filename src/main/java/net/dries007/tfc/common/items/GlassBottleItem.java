/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.util.data.Drinkable;

public class GlassBottleItem extends FluidContainerItem
{
    public static final int DRINK_AMOUNT = 100;

    private final Supplier<Double> breakChance;

    public GlassBottleItem(Properties properties, Supplier<Integer> capacity, Supplier<Double> breakChance, TagKey<Fluid> whitelist)
    {
        super(properties, capacity, whitelist, false, false);
        this.breakChance = breakChance;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        final IFluidHandler handler = stack.getCapability(Capabilities.FLUID_ITEM).resolve().orElse(null);
        if (handler != null)
        {
            final FluidStack drained = handler.drain(DRINK_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            if (entity instanceof Player player)
            {
                final Drinkable drinkable = Drinkable.get(drained.getFluid());
                if (drinkable != null && !level.isClientSide)
                {
                    drinkable.onDrink(player, drained.getAmount());
                }
            }
            if (entity.getRandom().nextFloat() < breakChance.get())
            {
                stack.shrink(1);
                level.playSound(null, entity.blockPosition(), TFCSounds.CERAMIC_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
        return stack;
    }


    @Override
    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        level.playSound(player, player.blockPosition(), TFCSounds.JUG_BLOW.get(), SoundSource.PLAYERS, 1.0f, 1.3f + (float) (player.getLookAngle().y / 2f));
        return InteractionResultHolder.success(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return PotionItem.EAT_DURATION;
    }

    @Override
    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        if (player.isShiftKeyDown())
        {
            level.playSound(player, player.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 0.5f, 1.2f);
            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            return InteractionResultHolder.consume(stack);
        }
        final Drinkable drinkable = Drinkable.get(handler.getFluidInTank(0).getFluid());
        if (drinkable != null)
        {
            if (!drinkable.mayDrinkWhenFull() && player.getFoodData() instanceof TFCFoodData food && food.getThirst() >= TFCFoodData.MAX_THIRST)
            {
                return InteractionResultHolder.fail(stack);
            }
            return ItemUtils.startUsingInstantly(level, player, hand);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> {
            final int color = RenderHelpers.getFluidColor(cap.getFluidInTank(0));
            final int r = FastColor.ARGB32.red(color);
            final int g = FastColor.ARGB32.green(color);
            final int b = FastColor.ARGB32.blue(color);
            return FastColor.ARGB32.color(0, r, g, b);
        }).orElse(0xFFFFF);
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> (int) Mth.clamp((float) cap.getFluidInTank(0).getAmount() / capacity.get() * 13, 1, 13)).orElse(0);
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> !cap.getFluidInTank(0).isEmpty()).orElse(false);
    }
}
