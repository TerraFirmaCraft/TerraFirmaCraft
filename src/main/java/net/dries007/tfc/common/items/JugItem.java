/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Drinkable;
import net.dries007.tfc.util.Helpers;

public class JugItem extends Item
{
    public JugItem(Item.Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().orElse(null);
        if (handler == null)
        {
            return InteractionResultHolder.pass(stack);
        }
        if (handler.getFluidInTank(0).isEmpty())
        {
            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK)
            {
                final BlockPos pos = hit.getBlockPos();
                final BlockState state = level.getBlockState(pos);
                if (FluidHelpers.pickupFluidInto(level, pos, state, player, handler))
                {
                    return InteractionResultHolder.success(stack);
                }
                final IFluidHandler blockHandler = FluidHelpers.getBlockEntityFluidHandler(level, pos, state);
                if (blockHandler != null && FluidHelpers.transferExact(blockHandler, handler, TFCConfig.SERVER.jugCapacity.get()))
                {
                    return InteractionResultHolder.success(stack);
                }
            }

            level.playSound(player, player.blockPosition(), TFCSounds.JUG_BLOW.get(), SoundSource.PLAYERS, 1.0f, 0.8f + (float) (player.getLookAngle().y / 2f));
            return InteractionResultHolder.success(stack);
        }
        else
        {
            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK)
            {
                final BlockPos pos = hit.getBlockPos();
                final BlockState state = level.getBlockState(pos);
                final IFluidHandler blockHandler = FluidHelpers.getBlockEntityFluidHandler(level, pos, state);
                if (blockHandler != null && FluidHelpers.transferExact(handler, blockHandler, TFCConfig.SERVER.jugCapacity.get()))
                {
                    return InteractionResultHolder.success(stack);
                }
            }

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
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        final IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().orElse(null);
        if (handler != null)
        {
            final Player player = entity instanceof Player ? (Player) entity : null;
            if (player != null)
            {
                final Drinkable drinkable = Drinkable.get(handler.getFluidInTank(0).getFluid());
                if (drinkable != null)
                {
                    drinkable.onDrink(player);
                }
            }

            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new ItemStackFluidHandler(stack, TFCTags.Fluids.USABLE_IN_JUG, TFCConfig.SERVER.jugCapacity.get());
    }
}
