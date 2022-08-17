/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.mixin.accessor.FlowingFluidAccessor;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public final class FluidHelpers
{
    /**
     * Forge removes this field in 1.19, so for easy porting use this instead
     */
    public static final int BUCKET_VOLUME = FluidAttributes.BUCKET_VOLUME;


    public static boolean transferBetweenWorldAndItem(ItemStack originalStack, Level level, BlockHitResult target, @Nullable Player player, @Nullable InteractionHand hand, boolean allowPlacingAnyLiquidBlocks, boolean allowPlacingSourceBlocks, boolean allowInfiniteSourceFilling)
    {
        return transferBetweenWorldAndItem(originalStack, level, target, new AfterTransferWithPlayer(player, hand), allowPlacingAnyLiquidBlocks, allowPlacingSourceBlocks, allowInfiniteSourceFilling);
    }

    /**
     * Invoked from an item, when it is interacting with an arbitrary position in the world, and might be trying a fluid transfer.
     * Based on the content of the item's handler (empty or not), will either try and fill or empty itself with whatever it can find. In order, it will try:
     * <ol>
     *     <li>Interacting with a fluid source block, {@link IFluidLoggable} or {@link BucketPickup} block, or placing the fluid in world directly.</li>
     *     <li>Interacting with a block entity that exposes a fluid handler, of which we assume is a continuous valued fluid tank</li>
     * </ol>
     *
     * @param target                      should be ray traced with {@link ClipContext.Fluid#SOURCE_ONLY}
     * @param allowPlacingAnyLiquidBlocks If {@code false}, when the fluid container is full, the empty-into-world attempt will be completely bypassed.
     * @param allowPlacingSourceBlocks    If {@code true}, when interacting directly with the world, this will be able to place source blocks, as opposed to transient flowing water blocks.
     * @param allowInfiniteSourceFilling  If {@code true}, when interacting directly with the world on a fluid source block, this will attempt to fill with {@code Integer.MAX_VALUE} fluid, if the fluid source block supports infinite sources.
     * @return {@code true} if a transfer occurred.
     */
    public static boolean transferBetweenWorldAndItem(ItemStack originalStack, Level level, BlockHitResult target, AfterTransfer after, boolean allowPlacingAnyLiquidBlocks, boolean allowPlacingSourceBlocks, boolean allowInfiniteSourceFilling)
    {
        if (target.getType() != HitResult.Type.BLOCK)
        {
            return false;
        }

        final BlockPos pos = target.getBlockPos();
        final BlockState state = level.getBlockState(pos);

        final ItemStack stack = originalStack.copy();
        final IFluidHandlerItem handler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (handler == null)
        {
            return false;
        }

        final FluidStack aggressiveDrained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (aggressiveDrained.isEmpty())
        {
            // Transfer block -> item
            // This will play a pickup sound, but we need to handle updating the container ourselves
            if (pickupFluidInto(level, pos, state, handler, allowInfiniteSourceFilling))
            {
                updateContainerItem(originalStack, handler, after);
                return true;
            }
        }
        else if (allowPlacingAnyLiquidBlocks)
        {
            // Transfer item -> block
            if (emptyFluidFrom(handler, level, pos, state, target, allowPlacingSourceBlocks))
            {
                updateContainerItem(originalStack, handler, after);
                return true;
            }
        }
        // Fallback to interacting with a block entity, if present. This occurs always, even if allowPlacingAnyLiquidBlocks = false
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null)
        {
            return transferBetweenBlockEntityAndItem(originalStack, entity, level, pos, after);
        }
        return false;
    }

    public static boolean transferBetweenBlockEntityAndItem(ItemStack originalStack, BlockEntity entity, Player player, InteractionHand hand)
    {
        return transferBetweenBlockEntityAndItem(originalStack, entity, player.level, player.blockPosition(), new AfterTransferWithPlayer(player, hand));
    }

    /**
     * Invoked from a block entity when an item is interacting with it. Assumes:
     * <ul>
     *     <li>The block entity has a arbitrary sized, continuous valued fluid tank</li>
     *     <li>The item has an arbitrary sized, <strong>possibly discrete</strong>, possibly continuous valued fluid tank.</li>
     * </ul>
     * <br>
     * If the item is empty, it will attempt to fill the item from the fluid tank. If the item contains any fluid, it will attempt to fill the fluid block entity from the item.
     *
     * @return {@code true} if a transfer occurred.
     */
    public static boolean transferBetweenBlockEntityAndItem(ItemStack originalStack, BlockEntity entity, Level level, BlockPos pos, AfterTransfer after)
    {
        return transferBetweenBlockHandlerAndItem(originalStack, Helpers.getCapability(entity, Capabilities.FLUID), level, pos, after);
    }

    /**
     * A variant of {@link #transferBetweenBlockEntityAndItem(ItemStack, BlockEntity, Level, BlockPos, AfterTransfer)} that doesn't require a block entity - the block handler can be from an explicit block based handler, for example.
     */
    public static boolean transferBetweenBlockHandlerAndItem(ItemStack originalStack, @Nullable IFluidHandler blockHandler, Level level, BlockPos pos, AfterTransfer after)
    {
        final ItemStack stack = originalStack.copy(); // Copy here, because the semantics of item fluid handlers require us to carefully manage the container
        final IFluidHandlerItem itemHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);

        if (itemHandler == null || blockHandler == null)
        {
            return false;
        }

        final FluidStack aggressiveDrained = itemHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (aggressiveDrained.isEmpty())
        {
            // Transfer block -> item.
            return transferBetweenItemAndOther(originalStack, itemHandler, blockHandler, itemHandler, Transfer.FILL, level, pos, after);
        }
        else
        {
            // Transfer item -> block
            return transferBetweenItemAndOther(originalStack, itemHandler, itemHandler, blockHandler, Transfer.DRAIN, level, pos, after);
        }
    }

    public static boolean transferBetweenItemAndOther(ItemStack originalStack, IFluidHandlerItem itemHandler, IFluidHandler from, IFluidHandler to, Transfer type, Level level, BlockPos pos, AfterTransfer after)
    {
        return transferBetweenItemAndOther(originalStack, itemHandler, from, to, fluid -> playTransferSound(level, pos, fluid, type), after);
    }

    /**
     * Transfers one way between two fluid handlers, where one of them is an item handler.
     * Handles updating the container item and optimistic / pessimistic transfer amounts.
     *
     * @return {@code true} if a transfer occurred.
     */
    public static boolean transferBetweenItemAndOther(ItemStack originalStack, IFluidHandlerItem itemHandler, IFluidHandler from, IFluidHandler to, Consumer<FluidStack> sound, AfterTransfer after)
    {
        final FluidStack aggressiveDrained = from.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

        final int maximumFilled = to.fill(aggressiveDrained, IFluidHandler.FluidAction.SIMULATE);
        if (maximumFilled <= 0)
        {
            return false;  // We were unable to transfer the maximum possible amount, so no transfer must be possible.
        }

        // Attempt 1: we know the block handler can accept *up to* maximumFilled, so we see if the item handler will let us extract that amount
        final FluidStack optimisticDrained = from.drain(maximumFilled, IFluidHandler.FluidAction.SIMULATE);
        if (optimisticDrained.getAmount() > 0)
        {
            // We managed to get an optimistic amount of fluid out! Now we try and transfer exactly this amount between the two handlers
            final int optimisticFilled = to.fill(optimisticDrained, IFluidHandler.FluidAction.SIMULATE);
            if (optimisticFilled > 0)
            {
                // Transaction complete
                to.fill(from.drain(optimisticDrained, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                updateContainerItem(originalStack, itemHandler, after);
                sound.accept(optimisticDrained);
                return true;
            }
            // We drained an optimistic amount, but for some reason, our destination fluid handler won't accept that amount.
            // This is a fall-through to Attempt 2
        }

        // Attempt 2: try and drain the *entire* item, and waste the rest.
        // This is for i.e. transferring out of a container that has a discrete size, i.e. buckets, when we want to transfer less than that amount.
        // We have already simulated both transactions here, so we just proceed to execution
        to.fill(from.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
        updateContainerItem(originalStack, itemHandler, after);
        sound.accept(aggressiveDrained);
        return true;
    }

    /**
     * After a fluid transfer has occurred involving an item fluid container, get the new stack that replaces the original stack.
     * This method also handles giving the player the excess container, in the event we had a stack size > 1 container that was modified.
     *
     * @param originalStack The item stack <strong>before</strong> any modifications were made to the fluid handler.
     * @param itemHandler   The fluid handler <strong>after</strong> modifications were made.
     */
    public static void updateContainerItem(ItemStack originalStack, IFluidHandlerItem itemHandler, AfterTransfer after)
    {
        if (originalStack.isEmpty())
        {
            after.updateContainerItem(ItemStack.EMPTY);
        }
        else if (originalStack.getCount() == 1)
        {
            // Single stack size, so get the current container of the modified fluid handler.
            after.updateContainerItem(itemHandler.getContainer());
        }
        else
        {
            // Decrement the original stack by one, but then we need to *also* return the container - which typically will involve dumping it into the player's inventory.
            originalStack.shrink(1);
            after.updateContainerItem(originalStack, itemHandler.getContainer());
        }
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

    public static boolean pickupFluidInto(Level level, BlockPos pos, BlockState state, IFluidHandler to, boolean allowInfiniteSourceFilling)
    {
        final FluidStack fluid = pickupFluid(level, pos, state, IFluidHandler.FluidAction.SIMULATE);
        if (fluid != null && !fluid.isEmpty())
        {
            if (allowInfiniteSourceFilling && fluid.getFluid() instanceof FlowingFluid flowing && ForgeEventFactory.canCreateFluidSource(level, pos, state, ((FlowingFluidAccessor) flowing).invoke$canConvertToSource()))
            {
                fluid.setAmount(Integer.MAX_VALUE);
            }
            final int filled = to.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0)
            {
                // Some fluid was filled, so we need to be aggressive about picking up the original fluid
                pickupFluid(level, pos, state, IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    /**
     * Pickup a fluid from a block in the world, leaving the block empty.
     */
    @Nullable
    public static FluidStack pickupFluid(Level level, BlockPos pos, BlockState state, IFluidHandler.FluidAction action)
    {
        final Block block = state.getBlock();
        if (block instanceof BucketPickupExtension pickup)
        {
            final FluidStack fluid = pickup.pickupBlock(level, pos, state, action);
            playTransferSound(level, pos, fluid, Transfer.FILL);
            return fluid;
        }
        if (block instanceof BucketPickup pickup)
        {
            if (action.execute())
            {
                // Directly execute, assuming that we can pickup into a bucket, then empty the bucket to obtain the contents
                final ItemStack stack = pickup.pickupBlock(level, pos, state);
                final FluidStack fluid = stack.getCapability(Capabilities.FLUID_ITEM)
                    .map(cap -> cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE))
                    .orElse(FluidStack.EMPTY);
                playTransferSound(level, pos, fluid, Transfer.FILL);
                return fluid;
            }
            else
            {
                // Can't simulate, so just infer what we think would've happened
                return new FluidStack(state.getFluidState().getType(), BUCKET_VOLUME);
            }
        }
        return null;
    }

    /**
     * This is based on {@link net.minecraft.world.item.BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     */
    public static boolean emptyFluidFrom(IFluidHandler handler, Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit, boolean allowPlacingSourceBlocks)
    {
        final Material material = state.getMaterial();
        final Block block = state.getBlock();
        final FluidStack simulatedDrained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        final Fluid fluid = simulatedDrained.getFluid();

        final boolean willReplace = state.isAir() || state.canBeReplaced(fluid) || (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, state, fluid));
        if (!willReplace)
        {
            if (hit == null)
            {
                return false;
            }
            final BlockPos relativePos = hit.getBlockPos().relative(hit.getDirection());
            return emptyFluidFrom(handler, level, relativePos, level.getBlockState(relativePos), null, allowPlacingSourceBlocks);
        }
        else if (level.dimensionType().ultraWarm() && Helpers.isFluid(fluid, FluidTags.WATER))
        {
            // Don't allow placing water type fluids in ultrawarm dimensions
            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
            for (int i = 0; i < 8; ++i)
            {
                level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0d, 0d, 0d);
            }
            return true;
        }
        else if (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, state, fluid) && simulatedDrained.getAmount() >= BUCKET_VOLUME)
        {
            // Delegate to the container to place the block
            container.placeLiquid(level, pos, state, fluid.defaultFluidState());
            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            playTransferSound(level, pos, simulatedDrained, Transfer.DRAIN);
            return true;
        }
        else
        {
            if (!level.isClientSide && state.canBeReplaced(fluid) && !material.isLiquid())
            {
                level.destroyBlock(pos, true);
            }

            // Are we allowed to create source blocks?
            final BlockState toPlace;
            if (allowPlacingSourceBlocks)
            {
                toPlace = fluid.defaultFluidState().createLegacyBlock();
            }
            else if (state.getFluidState().getType() == fluid)
            {
                // Special case - don't replace an existing fluid block with a non-source block
                // However, we still want to look as if we placed fluid here.
                toPlace = state;
            }
            else if (fluid instanceof FlowingFluid flowing)
            {
                // Not allowed to place source blocks, but we found a valid flowing fluid
                toPlace = flowing.getFlowing(1, false).createLegacyBlock();
            }
            else
            {
                return false;
            }

            level.setBlock(pos, toPlace, 3);
            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            playTransferSound(level, pos, simulatedDrained, Transfer.DRAIN);
            return true;
        }
    }

    public static void playTransferSound(Level level, BlockPos pos, FluidStack stack, Transfer type)
    {
        final FluidAttributes attributes = stack.getFluid().getAttributes();
        final SoundEvent sound = type == Transfer.FILL ? attributes.getFillSound(stack) : attributes.getEmptySound(stack);
        level.playSound(null, pos.getX(), pos.getY() + 0.5, pos.getZ(), sound, SoundSource.BLOCKS, 1f, 1f);
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
     * @param level              The world
     * @param pos                A position
     * @param blockStateIn       The current block state at that position
     * @param canConvertToSource The result of {@code self.canConvertToSource()} as it's protected
     * @param dropOff            The result of {@code self.getDropOff(worldIn)} as it's protected
     * @return The fluid state that should exist at that position
     * @see FlowingFluid#getNewLiquid(LevelReader, BlockPos, BlockState)
     */
    public static FluidState getNewFluidWithMixing(FlowingFluid self, LevelReader level, BlockPos pos, BlockState blockStateIn, boolean canConvertToSource, int dropOff)
    {
        int maxAdjacentFluidAmount = 0; // The maximum height of fluids flowing into this block from the sides
        FlowingFluid maxAdjacentFluid = self;

        int adjacentSourceBlocks = 0; // How many adjacent source blocks that could convert this into a source block
        Object2IntArrayMap<FlowingFluid> adjacentSourceBlocksByFluid = new Object2IntArrayMap<>(2);

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos offsetPos = pos.relative(direction);
            BlockState offsetState = level.getBlockState(offsetPos);
            FluidState offsetFluid = offsetState.getFluidState();

            // Look for adjacent fluids that are the same, for purposes of flow into this fluid
            // canPassThroughWall detects if a fluid state has a barrier - e.g. via a stair edge - that would prevent it from connecting to the current block.
            if (offsetFluid.getType() instanceof FlowingFluid && ((FlowingFluidAccessor) self).invoke$canPassThroughWall(direction, level, pos, blockStateIn, offsetPos, offsetState))
            {
                if (offsetFluid.isSource() && ForgeEventFactory.canCreateFluidSource(level, offsetPos, offsetState, canConvertToSource))
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
            BlockState belowState = level.getBlockState(pos.below());
            FluidState belowFluid = belowState.getFluidState();

            if (belowFluid.isSource() && belowFluid.getType() instanceof FlowingFluid belowFlowingFluid && adjacentSourceBlocksByFluid.getInt(belowFluid.getType()) >= 2)
            {
                // Try and create a source block of the same type as the below
                return FlowingFluidExtension.getSourceOrDefault(level, pos, belowFlowingFluid, false);
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
                    return FlowingFluidExtension.getSourceOrDefault(level, pos, maximumAdjacentSourceFluid, false);
                }
            }
        }

        // At this point, we haven't been able to convert into a source block
        // Check the block above to see if that is flowing downwards into this one (creating a level 8, falling, flowing block)
        // A fluid above, flowing down, will always replace an existing fluid block
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        FluidState aboveFluid = aboveState.getFluidState();
        if (!aboveFluid.isEmpty() && aboveFluid.getType() instanceof FlowingFluid && ((FlowingFluidAccessor) self).invoke$canPassThroughWall(Direction.UP, level, pos, blockStateIn, abovePos, aboveState))
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

    /**
     * Intended to be called from {@link Block#updateShape(BlockState, Direction, BlockState, LevelAccessor, BlockPos, BlockPos)} by blocks which support a fluid state.
     * This is responsible for causing fluid-logged blocks to spread fluid when they are updated.
     * <p>
     * Example implementation in vanilla is seen in {@link net.minecraft.world.level.block.SlabBlock#updateShape(BlockState, Direction, BlockState, LevelAccessor, BlockPos, BlockPos)}
     */
    @SuppressWarnings("deprecation")
    public static void tickFluid(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!state.getFluidState().isEmpty())
        {
            final Fluid fluid = state.getFluidState().getType();
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        }
    }

    @FunctionalInterface
    public interface AfterTransfer
    {
        default void updateContainerItem(ItemStack newOriginalStack)
        {
            updateContainerItem(newOriginalStack, ItemStack.EMPTY);
        }

        void updateContainerItem(ItemStack newOriginalStack, ItemStack newContainerStack);
    }

    public record AfterTransferWithPlayer(Player player, InteractionHand hand) implements AfterTransfer
    {
        @Override
        public void updateContainerItem(ItemStack newOriginalStack, ItemStack newContainerStack)
        {
            // If we're creative, then we don't modify the original stack (in our current hand)
            // We always accept the new container stack, by adding it to our inventory
            if (!player.isCreative())
            {
                player.setItemInHand(hand, newOriginalStack);
            }
            if (!newContainerStack.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, newContainerStack);
            }
        }
    }

    public enum Transfer
    {
        FILL, DRAIN
    }
}
