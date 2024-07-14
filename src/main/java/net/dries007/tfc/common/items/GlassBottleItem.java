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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
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
        final @Nullable IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
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
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 32;
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
            if (!drinkable.mayDrinkWhenFull() && IPlayerInfo.get(player).getThirst() >= PlayerInfo.MAX_THIRST)
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
        final FluidStack fluid = FluidHelpers.getContainedFluid(stack);
        if (!fluid.isEmpty())
        {
            final int color = RenderHelpers.getFluidColor(fluid);
            final int r = FastColor.ARGB32.red(color);
            final int g = FastColor.ARGB32.green(color);
            final int b = FastColor.ARGB32.blue(color);
            return FastColor.ARGB32.color(0, r, g, b);
        }
        return 0xFFFFF;
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        final FluidStack fluid = FluidHelpers.getContainedFluid(stack);
        return fluid.isEmpty() ? 0 : (int) Mth.clamp((float) fluid.getAmount() / capacity.get() * 13, 1, 13);
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return !FluidHelpers.getContainedFluid(stack).isEmpty();
    }
}
