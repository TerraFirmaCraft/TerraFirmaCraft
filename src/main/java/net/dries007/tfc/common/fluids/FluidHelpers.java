/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.function.Consumer;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.AqueductBlock;
import net.dries007.tfc.mixin.accessor.FlowingFluidAccessor;
import net.dries007.tfc.util.Helpers;

public final class FluidHelpers
{
    public static final int BUCKET_VOLUME = 1000;

    public static boolean canFluidExtinguishFire(Fluid fluid)
    {
        return fluid != Fluids.EMPTY && fluid.getFluidType().getTemperature() < 400; // 400 K ~ 127 C, reasonable heuristic
    }

    /**
     * See Issues:
     * <ul>
     *     <li><a href="https://github.com/MinecraftForge/MinecraftForge/issues/9052">Minecraft Forge#9052</a></li>
     *     <li><a href="https://github.com/MinecraftForge/MinecraftForge/issues/8897">Minecraft Forge#8897</a></li>
     * </ul>
     * In lack of any real support for making fluids behave like water, we hack around some of the {@link net.minecraft.world.entity.Entity} fluid checks to treat salt water and spring water, both of which exist in the world as water.
     */
    public static boolean isInWaterLikeFluid(Entity entity)
    {
        return entity.isInFluidType((fluidType, value) -> fluidType == TFCFluids.SALT_WATER.type().get() || fluidType == TFCFluids.SPRING_WATER.type().get());
    }

    /**
     * @see #isInWaterLikeFluid(Entity)
     */
    public static boolean isEyeInWaterLikeFluid(Entity entity)
    {
        return entity.isEyeInFluidType(TFCFluids.SALT_WATER.type().get()) || entity.isEyeInFluidType(TFCFluids.SALT_WATER.type().get());
    }


    public static boolean transferBetweenWorldAndItem(ItemStack originalStack, Level level, BlockHitResult target, Player player, InteractionHand hand, boolean allowPlacingAnyLiquidBlocks, boolean allowPlacingSourceBlocks, boolean allowInfiniteSourceFilling)
    {
        return transferBetweenWorldAndItem(originalStack, level, target, with(player, hand), allowPlacingAnyLiquidBlocks, allowPlacingSourceBlocks, allowInfiniteSourceFilling);
    }

    public static boolean transferBetweenWorldAndItem(ItemStack originalStack, Level level, BlockHitResult target, AfterTransfer after, boolean allowPlacingAnyLiquidBlocks, boolean allowPlacingSourceBlocks, boolean allowInfiniteSourceFilling)
    {
        return target.getType() == HitResult.Type.BLOCK && transferBetweenWorldAndItem(originalStack, level, target.getBlockPos(), target, after, allowPlacingAnyLiquidBlocks, allowPlacingSourceBlocks, allowInfiniteSourceFilling);
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
    public static boolean transferBetweenWorldAndItem(ItemStack originalStack, Level level, BlockPos pos, @Nullable BlockHitResult target, AfterTransfer after, boolean allowPlacingAnyLiquidBlocks, boolean allowPlacingSourceBlocks, boolean allowInfiniteSourceFilling)
    {
        final BlockState state = level.getBlockState(pos);
        final ItemStack stack = originalStack.copyWithCount(1);
        final IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
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
        return transferBetweenBlockEntityAndItem(originalStack, entity, player.level(), player.blockPosition(), with(player, hand));
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
        return transferBetweenBlockHandlerAndItem(originalStack, Helpers.getCapability(Capabilities.FluidHandler.BLOCK, entity), level, pos, after);
    }

    /**
     * A variant of {@link #transferBetweenBlockEntityAndItem(ItemStack, BlockEntity, Level, BlockPos, AfterTransfer)} that doesn't require a block entity - the block handler can be from an explicit block based handler, for example.
     */
    public static boolean transferBetweenBlockHandlerAndItem(ItemStack originalStack, @Nullable IFluidHandler blockHandler, Level level, BlockPos pos, AfterTransfer after)
    {
        final ItemStack stack = originalStack.copyWithCount(1);
        final IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

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
     * After a fluid transfer has occurred involving an item fluid container, get the new unsealedStack that replaces the original unsealedStack.
     * This method also handles giving the player the excess container, in the event we had a unsealedStack size > 1 container that was modified.
     *
     * @param originalStack The item unsealedStack <strong>before</strong> any modifications were made to the fluid handler.
     * @param itemHandler   The fluid handler <strong>after</strong> modifications were made. This fluid handler was obtained from a copy of {@code originalStack} with unsealedStack size = 1.
     */
    public static void updateContainerItem(ItemStack originalStack, IFluidHandlerItem itemHandler, AfterTransfer after)
    {
        if (originalStack.isEmpty())
        {
            after.updateContainerItem(ItemStack.EMPTY);
        }
        else if (originalStack.getCount() == 1)
        {
            // Single unsealedStack size, so get the current container of the modified fluid handler.
            after.updateContainerItem(itemHandler.getContainer());
        }
        else
        {
            // Decrement the original unsealedStack by one, but then we need to *also* return the container - which typically will involve dumping it into the player's inventory.
            // Note that in practice, a vanilla bucket **does not return a fluid handler** if there is a unsealedStack size > 1
            // This is just really annoyingly complicated, because unsealedStack size > 1 capabilities in general, don't make any sense and are ripe for dupe glitches.
            originalStack.shrink(1);
            after.updateContainerItem(originalStack, itemHandler.getContainer());
        }
    }

    /**
     * Transfer exactly {@code amount} between two fluid handlers.
     * @return {@code true} if the action succeeded
     */
    public static boolean transferExact(IFluidHandler from, IFluidHandler to, int amount)
    {
        final FluidStack drained = from.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        if (drained.getAmount() == amount)
        {
            if (to.fill(drained, IFluidHandler.FluidAction.SIMULATE) == amount)
            {
                to.fill(from.drain(amount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    public static boolean pickupFluidInto(Level level, BlockPos pos, final BlockState state, IFluidHandler to, boolean allowInfiniteSourceFilling)
    {
        final FluidStack fluid = pickupFluid(level, pos, state, IFluidHandler.FluidAction.SIMULATE);
        if (fluid != null && !fluid.isEmpty())
        {
            if (allowInfiniteSourceFilling && fluid.getFluid() instanceof FlowingFluid)
            {
                // Note that this check will be cancelled, if the block is an aqueduct, which is required.
                // However, for QoL, we can bypass that and let aqueducts count as an infinite source **in this case only!**
                // We still want to check the event though, so instead, we fake it by passing in the water block instead.
                BlockState queryState = state;
                if (state.getBlock() instanceof AqueductBlock)
                {
                    queryState = state.getFluidState().createLegacyBlock();
                }
                if (EventHooks.canCreateFluidSource(level, pos, queryState))
                {
                    fluid.setAmount(Integer.MAX_VALUE);
                }
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
        return pickupFluid(level, pos, state, action, fluid -> playTransferSound(level, pos, fluid, Transfer.FILL));
    }

    /**
     * Pickup a fluid from a block in the world, leaving the block empty.
     */
    @Nullable
    public static FluidStack pickupFluid(Level level, BlockPos pos, BlockState state, IFluidHandler.FluidAction action, Consumer<FluidStack> sound)
    {
        final Block block = state.getBlock();
        if (block instanceof BucketPickupExtension pickup)
        {
            final FluidStack fluid = pickup.pickupBlock(level, pos, state, action);
            sound.accept(fluid);
            return fluid;
        }
        if (block instanceof BucketPickup pickup)
        {
            if (action.execute())
            {
                // Directly execute, assuming that we can pickup into a bucket, then empty the bucket to obtain the contents
                final ItemStack stack = pickup.pickupBlock(null, level, pos, state);
                final @Nullable IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                final FluidStack fluid = fluidHandler != null ? fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE) : FluidStack.EMPTY;
                sound.accept(fluid);
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
     * This is based on {@link BucketItem#emptyContents}
     */
    @SuppressWarnings("deprecation")
    public static boolean emptyFluidFrom(IFluidHandler handler, Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit, boolean allowPlacingSourceBlocks)
    {
        final Block block = state.getBlock();
        final FluidStack simulatedDrained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        final Fluid fluid = simulatedDrained.getFluid();

        final boolean willReplace = state.isAir() || state.canBeReplaced(fluid) || (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(null, level, pos, state, fluid) && allowPlacingSourceBlocks);
        if (!willReplace)
        {
            if (hit == null)
            {
                return false;
            }
            final BlockPos relativePos = hit.getBlockPos().relative(hit.getDirection());
            return emptyFluidFrom(handler, level, relativePos, level.getBlockState(relativePos), null, allowPlacingSourceBlocks);
        }
        else if (fluid.getFluidType().isVaporizedOnPlacement(level, pos, simulatedDrained))
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
        else if (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(null, level, pos, state, fluid) && simulatedDrained.getAmount() >= BUCKET_VOLUME)
        {
            if (allowPlacingSourceBlocks)
            {
                // Delegate to the container to place the block
                container.placeLiquid(level, pos, state, fluid.defaultFluidState());
                handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                playTransferSound(level, pos, simulatedDrained, Transfer.DRAIN);
                return true;
            }
            // The iteration would've been one with a fluid container / waterloggable block, but we are not allowed to place source blocks
            // So, we deny the behavior entirely.
            return false;
        }
        else
        {
            // Are we allowed to create source blocks?
            final BlockState toPlace;
            if (allowPlacingSourceBlocks && simulatedDrained.getAmount() >= BUCKET_VOLUME)
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

            if (state.getBlock() != toPlace.getBlock())
            {
                if (!level.isClientSide && state.canBeReplaced(fluid) && !state.liquid())
                {
                    level.destroyBlock(pos, true);
                }
                level.setBlock(pos, toPlace, 3);
            }

            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            playTransferSound(level, pos, simulatedDrained, Transfer.DRAIN);
            return true;
        }
    }

    public static void playTransferSound(Level level, BlockPos pos, FluidStack stack, Transfer type)
    {
        // Forge doesn't register a sound for milk! Pretend it's water. See MinecraftForge#9316
        if (stack.getFluid() == NeoForgeMod.MILK.get())
        {
            stack = new FluidStack(Fluids.WATER, stack.getAmount());
        }
        final FluidType fluidType = stack.getFluid().getFluidType();
        final SoundEvent sound = fluidType.getSound(stack, type == Transfer.FILL ? SoundActions.BUCKET_FILL : SoundActions.BUCKET_EMPTY);
        if (sound != null)
        {
            level.playSound(null, pos.getX(), pos.getY() + 0.5, pos.getZ(), sound, SoundSource.BLOCKS, 1f, 1f);
        }
    }

    /**
     * Checks if a block state is empty other than a provided fluid
     *
     * @return true if the provided state is a source block of its current fluid
     */
    public static boolean isAirOrEmptyFluid(BlockState state)
    {
        return state.isAir() || state.getBlock() == state.getFluidState().getType().defaultFluidState().createLegacyBlock().getBlock();
    }

    public static boolean isEmptyFluid(BlockState state)
    {
        return !state.getFluidState().isEmpty() && state.getBlock() == state.getFluidState().getType().defaultFluidState().createLegacyBlock().getBlock();
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
        // If the state is already filled. Also handles cases where a block is unable to be filled, and we're filling with empty fluid
        if (state.getFluidState().getType() == fluid)
        {
            return state;
        }

        // We can fill air with a fluid source block
        if (state.isAir())
        {
            return fluid.defaultFluidState().createLegacyBlock();
        }

        // And we can fill a flowing fluid block of the same fluid with a source
        if (fluid instanceof FlowingFluid flowing && flowing.isSame(state.getFluidState().getType()))
        {
            return fluid.defaultFluidState().createLegacyBlock();
        }

        final Block block = state.getBlock();
        if (block instanceof IFluidLoggable fluidBlock)
        {
            final FluidProperty property = fluidBlock.getFluidProperty();
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

    /**
     * Remove all fluid from a {@code state} and return it. This is intended to be used in forceful situations where we need to remove *any possibility* of a source block, as it would otherwise enable exploits.
     */
    public static BlockState emptyFluidFrom(BlockState state)
    {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        if (state.getBlock() instanceof IFluidLoggable fluidBlock)
        {
            final FluidProperty property = fluidBlock.getFluidProperty();
            state = state.setValue(property, property.keyFor(Fluids.EMPTY));
        }
        if (isAirOrEmptyFluid(state))
        {
            state = Blocks.AIR.defaultBlockState();
        }
        if (isMeltableIce(state))
        {
            state = Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    /**
     * @return The contained fluid in an arbitrary fluid handler, determined by simulating a drain. This is more generic for use with
     * arbitrary fluid handlers, but <strong>will not work</strong> with handlers that have contained fluids, but prevent modification
     * (i.e. molds when solid)
     * @see #getContainedFluidInTank(ItemStack)
     */
    public static FluidStack getContainedFluid(ItemStack stack)
    {
        final @Nullable IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return handler != null ? handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE) : FluidStack.EMPTY;
    }

    /**
     * @return The contained fluid in a fluid unsealedStack with a single tank. This is implementation-specific, but works for cases like molds
     * that have potential to have a contained fluid but be solid
     */
    public static FluidStack getContainedFluidInTank(ItemStack stack)
    {
        final @Nullable IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return handler != null ? handler.getFluidInTank(0) : FluidStack.EMPTY;
    }

    /**
     * Returns if this is ice that is possible of melting into a fluid source.
     */
    public static boolean isMeltableIce(BlockState state)
    {
        return state.getBlock() == Blocks.ICE || state.getBlock() == TFCBlocks.SEA_ICE.get() || state.getBlock() == TFCBlocks.ICE_PILE.get();
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
     * This is the main logic from {@link FlowingFluid#getNewLiquid(Level, BlockPos, BlockState)}, but modified to support fluid mixing, and extracted into a static helper method to allow other {@link FlowingFluid} classes (namely, vanilla water) to be modified.
     *
     * @param self               The fluid instance this would've been called upon
     * @param level              The world
     * @param pos                A position
     * @param blockStateIn       The current block state at that position
     * @param dropOff            The result of {@code self.getDropOff(worldIn)} as it's protected
     * @return The fluid state that should exist at that position
     * @see FlowingFluid#getNewLiquid(Level, BlockPos, BlockState)
     */
    @SuppressWarnings("deprecation")
    public static FluidState getNewFluidWithMixing(FlowingFluid self, Level level, BlockPos pos, BlockState blockStateIn, int dropOff)
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
                if (offsetFluid.isSource() && EventHooks.canCreateFluidSource(level, offsetPos, offsetState))
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
            else if (belowState.isSolid())
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
     *
     * @param tickWhenEmpty If when the fluid state is empty, this should still schedule a block tick. This is for blocks that want to be removed, generally, when fluid is removed.
     */
    public static void tickFluid(LevelAccessor level, BlockPos pos, BlockState state, boolean tickWhenEmpty)
    {
        if (!state.getFluidState().isEmpty())
        {
            final Fluid fluid = state.getFluidState().getType();
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        }
        else if (tickWhenEmpty)
        {
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }

    public static void tickFluid(LevelAccessor level, BlockPos pos, BlockState state)
    {
        tickFluid(level, pos, state, false);
    }

    /**
     * Handles a fluid transfer with a player, holding the fluid item in their {@code hand}
     */
    public static AfterTransfer with(Player player, InteractionHand hand)
    {
        return (newOriginalStack, newContainerStack) -> {
            // If we're creative, then we don't modify the original unsealedStack (in our current hand)
            // We always accept the new container unsealedStack, by adding it to our inventory
            if (!player.isCreative())
            {
                player.setItemInHand(hand, newOriginalStack);
            }
            if (!newContainerStack.isEmpty())
            {
                // Always ensure that we've only created one new container unsealedStack.
                ItemHandlerHelper.giveItemToPlayer(player, newContainerStack.copyWithCount(1));
            }
        };
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

    public enum Transfer
    {
        FILL, DRAIN
    }
}
