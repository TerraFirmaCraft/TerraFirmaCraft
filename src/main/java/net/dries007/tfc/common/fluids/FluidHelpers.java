/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.items.CapabilityItemHandler;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.mixin.accessor.FlowingFluidAccessor;
import net.dries007.tfc.util.Helpers;

public final class FluidHelpers
{
    /**
     * Transfer an amount up to and inclusive of {@code amount} between two fluid handlers.
     */
    public static boolean transferUpTo(IFluidHandler from, IFluidHandler to, int amount)
    {
        final FluidStack drained = from.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        if (!drained.isEmpty())
        {
            final int filled = to.fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if (filled > 0)
            {
                return transferExact(from, to, filled);
            }
        }
        return false;
    }

    /**
     * Simpler version of FluidUtil#interactWithFluidHandler. Returns true if we think a transfer occurred.
     *
     * The major motivation:
     * Vanilla / forge buckets require at least 1000mB to be drained, or else nothing happens, so we have to pretend we are asking for that much if that's the case.
     *
     * Stuff like pots that are either always 1000mB or 0mb should not need this.
     */
    public static boolean itemInteractsWithFluidHandler(ItemStack stack, BlockEntity blockEntity, int preferredAmount, Player player)
    {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(itemCap -> {
            return blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(blockEntityCap -> {
                if (itemCap.getFluidInTank(0).isEmpty()) // try to fill it from the fluid tank
                {
                    return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inv -> {
                        FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack, blockEntityCap, inv, preferredAmount, player, true);
                        if (result.isSuccess())
                        {
                            player.setItemInHand(player.getUsedItemHand(), result.getResult());
                            return true;
                        }
                        return false;
                    }).orElse(false);
                }
                else
                {
                    final boolean bucketTransfer = itemCap instanceof FluidBucketWrapper && preferredAmount < FluidAttributes.BUCKET_VOLUME; // todo: does this suck?
                    return tryEmptyItem(player, itemCap, blockEntityCap, bucketTransfer ? FluidAttributes.BUCKET_VOLUME : preferredAmount, !bucketTransfer);
                }
            }).orElse(false);
        }).orElse(false);
    }

    /**
     * See javadoc above. Bucket-safe version of tryEmptyContainer and related functions.
     */
    public static boolean tryEmptyItem(Player player, IFluidHandlerItem from, IFluidHandler to, int amount, boolean exact)
    {
        if (exact) return transferExact(from, to, amount);
        final FluidStack drained = from.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        if (drained.getAmount() == amount)
        {
            final int filled = to.fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if (filled > 0) // we only need it to be filled *at all* rather than the exact amount
            {
                to.fill(from.drain(amount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                if (!player.isCreative()) player.setItemInHand(player.getUsedItemHand(), from.getContainer());
                return true;
            }
        }
        return false;
    }

    /**
     * Transfer exactly {@code amount} between two fluid handlers.
     */
    public static boolean transferExact(IFluidHandler from, IFluidHandler to, int amount)
    {
        final FluidStack drained = from.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        if (drained.getAmount() == amount)
        {
            final int filled = to.fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if (filled == amount)
            {
                to.fill(from.drain(amount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    public static boolean pickupFluidInto(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Player player, IFluidHandler to)
    {
        final FluidStack fluid = pickupFluid(level, pos, state, player, IFluidHandler.FluidAction.SIMULATE);
        if (fluid != null && !fluid.isEmpty())
        {
            final int filled = to.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0)
            {
                // Some fluid was filled, so we need to be aggressive about picking up the original fluid
                pickupFluid(level, pos, state, player, IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    /**
     * Pickup a fluid fluid from a block in the world, leaving the block empty.
     */
    @Nullable
    public static FluidStack pickupFluid(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Player player, IFluidHandler.FluidAction action)
    {
        final Block block = state.getBlock();
        if (block instanceof BucketPickup pickup)
        {
            if (action.execute())
            {
                final ItemStack stack = pickup.pickupBlock(level, pos, state);
                final FluidStack fluid = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(cap -> cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE)).orElse(FluidStack.EMPTY);

                pickup.getPickupSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f));
                return fluid;
            }
            else
            {
                return new FluidStack(state.getFluidState().getType(), FluidAttributes.BUCKET_VOLUME);
            }
        }
        return null;
    }

    @Nullable
    public static IFluidHandler getBlockEntityFluidHandler(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (state.hasBlockEntity())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null)
            {
                return entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve().orElse(null);
            }
        }
        return null;
    }

    /**
     * Checks if a block state is empty other than a provided fluid
     *
     * @return true if the provided state is a source block of it's current fluid
     */
    public static boolean isAirOrEmptyFluid(BlockState state)
    {
        return state.isAir() || state.getBlock() == state.getFluidState().getType().defaultFluidState().createLegacyBlock().getBlock();
    }

    /**
     * Any fluid block, given that it's an empty one.
     */
    public static boolean isAnEmptyFluid(BlockState state)
    {
        final FluidState fluid = state.getFluidState();
        if (fluid.isEmpty()) return false;
        return state.getBlock() == fluid.getType().defaultFluidState().createLegacyBlock().getBlock();
    }

    /**
     * Given a block state and a fluid, attempts to fill the block state with the fluid
     * Returns null if the provided combination cannot be filled
     *
     * @param state The state to fill
     * @param fluid The fluid to fill with
     * @return The fluid-logged state, or null if impossible
     */
    @Nullable
    public static BlockState fillWithFluid(BlockState state, Fluid fluid)
    {
        // If the state is already filled. Also handles cases where a block is unable to be filled and we're filling with empty fluid
        if (state.getFluidState().getType() == fluid)
        {
            return state;
        }

        final Block block = state.getBlock();
        if (block instanceof IFluidLoggable)
        {
            final FluidProperty property = ((IFluidLoggable) block).getFluidProperty();
            if (property.canContain(fluid))
            {
                return state.setValue(property, property.keyFor(fluid));
            }
        }
        else if (state.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            if (fluid == Fluids.WATER)
            {
                return state.setValue(BlockStateProperties.WATERLOGGED, true);
            }
            else if (fluid == Fluids.EMPTY)
            {
                return state.setValue(BlockStateProperties.WATERLOGGED, false);
            }
        }
        return null;
    }

    public static boolean isSame(FluidState state, Fluid expected)
    {
        return state.getType().isSame(expected);
    }

    public static boolean canMixFluids(Fluid left, Fluid right)
    {
        return canMixFluids(left) && canMixFluids(right);
    }

    /**
     * If the two fluids are allowed to be considered for mixing
     * This is more lenient than {@link FlowingFluid#isSame(Fluid)} but must assume a few things:
     * - only works with fluids which are an instance of {@link FlowingFluid} (should be all fluids)
     * - assumes that fluid source / flowing handling works like vanilla
     * - fluids must be added to the {@link net.dries007.tfc.common.TFCTags.Fluids#MIXABLE} tag
     *
     * @param fluid A fluid
     * @return true if the fluid should use fluid mixing mechanics
     */
    public static boolean canMixFluids(Fluid fluid)
    {
        return fluid instanceof FlowingFluid && Helpers.isFluid(fluid, TFCTags.Fluids.MIXABLE);
    }

    /**
     * This is the main logic from {@link FlowingFluid#getNewLiquid(LevelReader, BlockPos, BlockState)}, but modified to support fluid mixing, and extracted into a static helper method to allow other {@link FlowingFluid} classes (namely, vanilla water) to be modified.
     *
     * @param self               The fluid instance this would've been called upon
     * @param worldIn            The world
     * @param pos                A position
     * @param blockStateIn       The current block state at that position
     * @param canConvertToSource The result of {@code self.canConvertToSource()} as it's protected
     * @param dropOff            The result of {@code self.getDropOff(worldIn)} as it's protected
     * @return The fluid state that should exist at that position
     * @see FlowingFluid#getNewLiquid(LevelReader, BlockPos, BlockState)
     */
    public static FluidState getNewFluidWithMixing(FlowingFluid self, LevelReader worldIn, BlockPos pos, BlockState blockStateIn, boolean canConvertToSource, int dropOff)
    {
        int maxAdjacentFluidAmount = 0; // The maximum height of fluids flowing into this block from the sides
        FlowingFluid maxAdjacentFluid = self;

        int adjacentSourceBlocks = 0; // How many adjacent source blocks that could convert this into a source block
        Object2IntArrayMap<FlowingFluid> adjacentSourceBlocksByFluid = new Object2IntArrayMap<>(2);

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos offsetPos = pos.relative(direction);
            BlockState offsetState = worldIn.getBlockState(offsetPos);
            FluidState offsetFluid = offsetState.getFluidState();

            // Look for adjacent fluids that are the same, for purposes of flow into this fluid
            // canPassThroughWall detects if a fluid state has a barrier - e.g. via a stair edge - that would prevent it from connecting to the current block.
            if (offsetFluid.getType() instanceof FlowingFluid && ((FlowingFluidAccessor) self).invoke$canPassThroughWall(direction, worldIn, pos, blockStateIn, offsetPos, offsetState))
            {
                if (offsetFluid.isSource() && ForgeEventFactory.canCreateFluidSource(worldIn, offsetPos, offsetState, canConvertToSource))
                {
                    adjacentSourceBlocks++;
                    adjacentSourceBlocksByFluid.mergeInt((FlowingFluid) offsetFluid.getType(), 1, Integer::sum);
                }
                // Also record the maximum adjacent fluid, breaking ties with the current fluid
                if (offsetFluid.getAmount() > maxAdjacentFluidAmount || (offsetFluid.getAmount() == maxAdjacentFluidAmount && self.isSame(offsetFluid.getType())))
                {
                    maxAdjacentFluidAmount = offsetFluid.getAmount();
                    maxAdjacentFluid = (FlowingFluid) offsetFluid.getType();
                }
            }
        }

        if (adjacentSourceBlocks >= 2)
        {
            // There are two adjacent source blocks (although potentially of different kinds) - check if the below block is also a source, or if it's a solid block
            // If true, then this block should be converted to a source block as well
            BlockState belowState = worldIn.getBlockState(pos.below());
            FluidState belowFluid = belowState.getFluidState();

            if (belowFluid.isSource() && belowFluid.getType() instanceof FlowingFluid && adjacentSourceBlocksByFluid.getInt(belowFluid.getType()) >= 2)
            {
                // Try and create a source block of the same type as the below
                return ((FlowingFluid) belowFluid.getType()).getSource(false);
            }
            else if (belowState.getMaterial().isSolid())
            {
                // This could potentially form fluid blocks from multiple blocks. It can only override the current source if there's three adjacent equal sources, or form a source if this is the same as three adjacent sources
                FlowingFluid maximumAdjacentSourceFluid = self;
                int maximumAdjacentSourceBlocks = 0;
                for (Object2IntMap.Entry<FlowingFluid> entry : adjacentSourceBlocksByFluid.object2IntEntrySet())
                {
                    if (entry.getIntValue() > maximumAdjacentSourceBlocks || entry.getKey() == self)
                    {
                        maximumAdjacentSourceBlocks = entry.getIntValue();
                        maximumAdjacentSourceFluid = entry.getKey();
                    }
                }

                // Three adjacent (if not same), or two (if same)
                if (maximumAdjacentSourceBlocks >= 3 || (maximumAdjacentSourceBlocks >= 2 && self.isSame(maximumAdjacentSourceFluid)))
                {
                    return maximumAdjacentSourceFluid.getSource(false);
                }
            }
        }

        // At this point, we haven't been able to convert into a source block
        // Check the block above to see if that is flowing downwards into this one (creating a level 8, falling, flowing block)
        // A fluid above, flowing down, will always replace an existing fluid block
        BlockPos abovePos = pos.above();
        BlockState aboveState = worldIn.getBlockState(abovePos);
        FluidState aboveFluid = aboveState.getFluidState();
        if (!aboveFluid.isEmpty() && aboveFluid.getType() instanceof FlowingFluid && ((FlowingFluidAccessor) self).invoke$canPassThroughWall(Direction.UP, worldIn, pos, blockStateIn, abovePos, aboveState))
        {
            return ((FlowingFluid) aboveFluid.getType()).getFlowing(8, true);
        }
        else
        {
            // Nothing above that can flow downwards, so use the highest adjacent fluid amount, after subtracting the drop off (1 for water, 2 for lava)
            int selfFluidAmount = maxAdjacentFluidAmount - dropOff;
            if (selfFluidAmount <= 0)
            {
                // No flow amount into this block
                return Fluids.EMPTY.defaultFluidState();
            }
            // Cause the maximum adjacent fluid to flow into this block
            return maxAdjacentFluid.getFlowing(selfFluidAmount, false);
        }
    }

    public static void setSourceBlock(Level level, BlockPos pos, Fluid fluid)
    {
        if (fluid instanceof FlowingFluid flow)
        {
            level.setBlock(pos, flow.getSource().defaultFluidState().createLegacyBlock(), 3);
        }
        else
        {
            level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 3);
        }
    }

    public static void tickFluid(LevelAccessor level, BlockPos pos, BlockState state, IFluidLoggable loggable)
    {
        final Fluid contained = state.getValue(loggable.getFluidProperty()).getFluid();
        if (contained.isSame(Fluids.EMPTY)) return;
        level.scheduleTick(pos, contained, contained.getTickDelay(level));
    }
}
