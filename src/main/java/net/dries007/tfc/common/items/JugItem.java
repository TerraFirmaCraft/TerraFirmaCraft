/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Drinkable;
import org.jetbrains.annotations.Nullable;

public class JugItem extends WoodenBucketItem
{
    public JugItem(Item.Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist)
    {
        super(properties, capacity, whitelist);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        final IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().orElse(null);
        if (handler != null)
        {
            final FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            final Player player = entity instanceof Player ? (Player) entity : null;
            if (player != null)
            {
                final Drinkable drinkable = Drinkable.get(drained.getFluid());
                if (drinkable != null)
                {
                    drinkable.onDrink(player, drained.getAmount());
                }
            }

            if (entity.getRandom().nextFloat() < TFCConfig.SERVER.jugBreakChance.get())
            {
                stack.shrink(1);
                level.playSound(player, entity.blockPosition(), TFCSounds.CERAMIC_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
        return stack;
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
    protected TagKey<Fluid> getWhitelistTag()
    {
        return TFCTags.Fluids.USABLE_IN_JUG;
    }

    @Override
    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        level.playSound(player, player.blockPosition(), TFCSounds.JUG_BLOW.get(), SoundSource.PLAYERS, 1.0f, 0.8f + (float) (player.getLookAngle().y / 2f));
        return InteractionResultHolder.success(stack);
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
            return ItemUtils.startUsingInstantly(level, player, hand);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean emptyContents(IFluidHandler handler, Player player, Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult result)
    {
        return false;
    }
}
