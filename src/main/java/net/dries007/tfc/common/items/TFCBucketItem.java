/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public abstract class TFCBucketItem extends Item
{
    private final Supplier<Integer> capacity;

    public TFCBucketItem(Properties properties, Supplier<Integer> capacity)
    {
        super(properties);
        this.capacity = capacity;
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
                if (blockHandler != null && FluidHelpers.transferExact(blockHandler, handler, getCapacity()))
                {
                    return InteractionResultHolder.success(stack);
                }
            }
            return afterFillFailed(handler, level, player, stack, hand);
        }
        else
        {
            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK)
            {
                final BlockPos pos = hit.getBlockPos();
                final BlockState state = level.getBlockState(pos);
                final IFluidHandler blockHandler = FluidHelpers.getBlockEntityFluidHandler(level, pos, state);
                if (blockHandler != null && FluidHelpers.transferExact(handler, blockHandler, getCapacity()))
                {
                    return InteractionResultHolder.success(stack);
                }
                if (emptyContents(handler, player, level, pos, state, hit))
                {
                    return InteractionResultHolder.sidedSuccess(player.isCreative() ? stack : new ItemStack(this), level.isClientSide);
                }
            }
            return afterEmptyFailed(handler, level, player, stack, hand);
        }
    }

    /**
     * This is based on {@link net.minecraft.world.item.BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     * With the change that the fluid in the tank is the one we are using
     */
    public boolean emptyContents(IFluidHandler handler, @Nullable Player player, Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit)
    {
        Material material = state.getMaterial();
        Block block = state.getBlock();
        Fluid content = handler.getFluidInTank(0).getFluid();

        boolean replaceable = state.canBeReplaced(content);
        boolean willReplace = state.isAir() || replaceable || block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(level, pos, state, content);
        if (!willReplace)
        {
            if (hit == null) return false;
            BlockPos relativePos = hit.getBlockPos().relative(hit.getDirection());
            return this.emptyContents(handler, player, level, relativePos, level.getBlockState(relativePos), null);
        }
        else if (level.dimensionType().ultraWarm() && Helpers.isFluid(content, FluidTags.WATER))
        {
            level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

            for (int i = 0; i < 8; ++i)
            {
                level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
            }
            return true;
        }
        else if (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, state, content))
        {
            container.placeLiquid(level, pos, state, content.defaultFluidState());
            playEmptySound(content, player, level, pos);
            return true;
        }
        else
        {
            if (!level.isClientSide && replaceable && !material.isLiquid())
            {
                level.destroyBlock(pos, true);
            }

            if (!level.setBlock(pos, content.defaultFluidState().createLegacyBlock(), 11) && !state.getFluidState().isSource())
            {
                return false;
            }
            else
            {
                Helpers.playSound(level, pos, SoundEvents.BUCKET_EMPTY);
                this.playEmptySound(content, player, level, pos);
                return true;
            }
        }
    }

    protected void playEmptySound(Fluid content, @Nullable Player player, LevelAccessor level, BlockPos pos)
    {
        SoundEvent sound = content.getAttributes().getEmptySound();
        if (sound == null)
        {
            sound = Helpers.isFluid(content, FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    public int getCapacity()
    {
        return capacity.get();
    }

    abstract TagKey<Fluid> getWhitelistTag();

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new ItemStackFluidHandler(stack, getWhitelistTag(), getCapacity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        FluidStack fluid = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(cap -> cap.getFluidInTank(0)).orElse(FluidStack.EMPTY);
        if (!fluid.isEmpty()) Helpers.addFluidStackTooltipInfo(fluid, tooltip);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(cap -> !cap.getFluidInTank(0).isEmpty()).orElse(false);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        return new ItemStack(this);
    }
}
