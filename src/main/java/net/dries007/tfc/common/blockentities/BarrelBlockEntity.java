/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.FluidParticleOption;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.component.CachedMut;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.block.BarrelComponent;
import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.input.NonEmptyInput;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public class BarrelBlockEntity extends TickableInventoryBlockEntity<BarrelBlockEntity.BarrelInventory> implements ICalendarTickable, BarrelInventoryCallback
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    public static final int SLOTS = 3;

    public static void serverTick(Level level, BlockPos pos, BlockState state, BarrelBlockEntity barrel)
    {
        barrel.getRecipe(); // Cache the recipe, so any further operations are done on a loaded recipe cache
        barrel.checkForLastTickSync();
        barrel.checkForCalendarUpdate();

        // Fill / drain from the fluid IO slots every 5 ticks
        if (level.getGameTime() % 5 == 0)
        {
            barrel.updateFluidIOSlots();
        }

        // If the barrel contains excess stacks, and the main slot is empty, populate the main slot from the excess
        final List<ItemStack> excess = barrel.inventory.excess;
        if (!excess.isEmpty() && barrel.inventory.getStackInSlot(SLOT_ITEM).isEmpty())
        {
            barrel.inventory.setStackInSlot(SLOT_ITEM, excess.remove(0));
        }

        final SealedBarrelRecipe recipe = barrel.getRecipe();
        final boolean sealed = state.getValue(BarrelBlock.SEALED);
        final Direction facing = state.getValue(BarrelBlock.FACING);
        if (recipe != null && sealed)
        {
            final int durationSealed = (int) (Calendars.SERVER.getTicks() - barrel.recipeTick);
            if (!recipe.isInfinite() && durationSealed > recipe.getDuration())
            {
                if (recipe.matches(barrel.inventory))
                {
                    // Recipe completed, so fill outputs
                    recipe.assembleOutputs(barrel.inventory);
                    Helpers.playSound(level, barrel.getBlockPos(), recipe.getCompleteSound());
                }

                // In both cases, update the recipe and sync
                barrel.updateRecipe();
                barrel.markForSync();

                // If a new recipe exists, then apply onSeal effects. This is for cases such as pickling -> vinegar preservation
                final @Nullable SealedBarrelRecipe nextRecipe = barrel.getRecipe();
                if (nextRecipe != null)
                {
                    nextRecipe.onSealed(barrel.inventory); // We're in a sequential recipe, so apply sealed affects to the new recipe
                }
            }
        }

        if (barrel.needsInstantRecipeUpdate)
        {
            barrel.needsInstantRecipeUpdate = false;
            if (barrel.inventory.excess.isEmpty()) // Excess must be empty for instant recipes to apply
            {
                RecipeHolder<? extends BarrelRecipe> instantRecipe = BarrelRecipe.get(level, TFCRecipeTypes.BARREL_INSTANT, barrel.inventory);
                if (instantRecipe == null)
                {
                    instantRecipe = BarrelRecipe.get(level, TFCRecipeTypes.BARREL_INSTANT_FLUID, barrel.inventory);
                }
                if (instantRecipe != null)
                {
                    instantRecipe.value().assembleOutputs(barrel.inventory);
                    if (barrel.soundCooldownTicks == 0)
                    {
                        Helpers.playSound(level, barrel.getBlockPos(), instantRecipe.value().getCompleteSound());
                        barrel.soundCooldownTicks = 5;
                        if (instantRecipe.value().getCompleteSound() == SoundEvents.FIRE_EXTINGUISH && level instanceof ServerLevel server)
                        {
                            final double x = pos.getX() + 0.5;
                            final double y = pos.getY();
                            final double z = pos.getZ() + 0.5;
                            final RandomSource random = level.getRandom();
                            server.sendParticles(TFCParticles.BUBBLE.get(), x + random.nextFloat() * 0.375 - 0.1875, y + 15f / 16f, z + random.nextFloat() * 0.375 - 0.1875, 6, 0, 0, 0, 1);
                            server.sendParticles(TFCParticles.STEAM.get(), x + random.nextFloat() * 0.375 - 0.1875, y + 15f / 16f, z + random.nextFloat() * 0.375 - 0.1875, 6, 0, 0, 0, 1);
                        }
                    }
                }
                barrel.markForSync();
            }
        }

        if (barrel.soundCooldownTicks > 0)
        {
            barrel.soundCooldownTicks--;
        }

        if (level.getGameTime() % 20 == 0 && !sealed && facing == Direction.UP)
        {
            Helpers.gatherAndConsumeItems(level, new AABB(0.25f, 0.0625f, 0.25f, 0.75f, 0.9375f, 0.75f).move(pos), barrel.inventory, SLOT_ITEM, SLOT_ITEM);
        }
        barrel.tickPouring(level, pos, sealed, facing);

        if (!sealed && facing == Direction.UP && level.getGameTime() % 4 == 0 && level.isRainingAt(pos.above()))
        {
            // Fill with water from rain
            barrel.inventory.fill(new FluidStack(Fluids.WATER, 1), IFluidHandler.FluidAction.EXECUTE);
            barrel.markForSync();
        }
    }


    private final SidedHandler<IFluidHandler> sidedFluidInventory;
    private final CachedMut<RecipeHolder<SealedBarrelRecipe>> recipe = CachedMut.empty();
    private long lastUpdateTick = Integer.MIN_VALUE; // The last tick this barrel was updated in serverTick()
    private long sealedTick; // The tick this barrel was sealed
    private long recipeTick; // The tick this barrel started working on the current recipe
    private int soundCooldownTicks = 0;

    private boolean needsInstantRecipeUpdate; // If the instant recipe needs to be checked again

    public BarrelBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BARREL.get(), pos, state, BarrelBlockEntity.BarrelInventory::new);

        sidedFluidInventory = new SidedHandler<>(inventory);

        if (TFCConfig.SERVER.barrelEnableAutomation.get())
        {
            final Direction facing = state.hasProperty(BarrelBlock.FACING) ? state.getValue(BarrelBlock.FACING) : Direction.UP;
            final boolean vertical = facing == Direction.UP;
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(SLOT_FLUID_CONTAINER_IN).extract(SLOT_FLUID_CONTAINER_OUT), vertical ? Direction.Plane.HORIZONTAL : d -> d.getAxis() != facing.getAxis() && d.getAxis().isHorizontal())
                .on(new PartialItemHandler(inventory).insert(SLOT_ITEM), facing)
                .on(new PartialItemHandler(inventory).extract(SLOT_ITEM), facing.getOpposite());
            sidedFluidInventory
                .on(PartialFluidHandler::insertOnly, vertical ? Direction.UP : facing.getOpposite())
                .on(PartialFluidHandler::extractOnly, vertical ? d -> d != Direction.UP : d -> d == facing);
        }
    }

    @Nullable
    public IFluidHandler getSidedFluidInventory(@Nullable Direction context)
    {
        return sidedFluidInventory.get(context);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return BarrelContainer.create(this, player.getInventory(), containerId);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        needsInstantRecipeUpdate = true;
        updateRecipe();
        setChanged();
    }

    @Override
    public void fluidTankChanged()
    {
        needsInstantRecipeUpdate = true;
        updateRecipe();
        setChanged();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return switch (slot)
            {
                case SLOT_FLUID_CONTAINER_IN -> Helpers.mightHaveCapability(stack, Capabilities.FluidHandler.ITEM);
                case SLOT_ITEM -> {
                    // We only want to deny heavy/huge (aka things that can hold inventory).
                    // Other than that, barrels don't need a size restriction, and should in general be unrestricted, so we can allow any kind of recipe input (i.e. unfired large vessel)
                    final IItemSize size = ItemSizeManager.get(stack);
                    yield size.getSize(stack).isSmallerThan(Size.HUGE) || size.getWeight(stack).isSmallerThan(Weight.VERY_HEAVY);
                }
                default -> true;
            };
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            tr.add(-ticks); // Perform the recipe update in the past
            updateRecipe();
        }

        @Nullable SealedBarrelRecipe recipe = getRecipe();
        if (!getBlockState().getValue(BarrelBlock.SEALED) || recipe == null || recipe.isInfinite())
        {
            return; // No simulation occurs if we were not sealed, or if we had no recipe, or if we had an infinite recipe.
        }

        // Otherwise, begin simulation by jumping to the end tick of the current recipe. If that was in the past, we simulate and retry.
        final long currentTick = Calendars.SERVER.getTicks();
        long lastKnownTick = recipeTick + recipe.getDuration();
        while (lastKnownTick < currentTick)
        {
            // Need to run the recipe completion, as it occurred in the past
            final long offset = currentTick - lastKnownTick;
            assert offset >= 0; // This event should be in the past

            try (CalendarTransaction tr = Calendars.SERVER.transaction())
            {
                tr.add(-offset);

                if (recipe.matches(inventory))
                {
                    recipe.assembleOutputs(inventory);
                }
                updateRecipe();
                markForSync();
            }

            // Re-check the recipe. If we have an invalid or infinite recipe, then exit simulation. Otherwise, jump forward to the next recipe completion
            // This handles the case where multiple sequential recipes, such as brining -> pickling -> vinegar preservation would've occurred.
            final @Nullable SealedBarrelRecipe knownRecipe = getRecipe();
            if (knownRecipe == null)
            {
                return;
            }

            knownRecipe.onSealed(inventory); // We're in a sequential recipe, so apply sealed affects to the new recipe
            if (knownRecipe.isInfinite())
            {
                return; // No more simulation can occur
            }
            lastKnownTick += recipe.getDuration();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("sealedTick", sealedTick);
        nbt.putLong("recipeTick", recipeTick);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        sealedTick = nbt.getLong("sealedTick");
        recipeTick = nbt.getLong("recipeTick");
        recipe.unload();
        super.loadAdditional(nbt, provider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        final BarrelComponent barrel = components.getOrDefault(TFCComponents.BARREL, BarrelComponent.EMPTY);
        if (!barrel.isEmpty())
        {
            final Iterator<ItemStack> iter = barrel.itemContent().iterator();
            inventory.setStackInSlot(SLOT_ITEM, iter.next()); // First slot goes into the item slot
            while (iter.hasNext()) inventory.excess.add(iter.next()); // Any others go into excess
            inventory.tank.setFluid(barrel.fluidContent().copy());
        }
        super.applyImplicitComponents(components);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        if (getBlockState().getValue(BarrelBlock.SEALED))
        {
            final ImmutableList.Builder<ItemStack> inventoryList = ImmutableList.builderWithExpectedSize(1 + inventory.excess.size());

            // A sealed barrel can only contain the main slot, and excess. Fluid containers are dumped out on seal
            // Thus, the first slot here holds the sealed value, the remaining slots are for excess inventory
            inventoryList.add(inventory.getStackInSlot(SLOT_ITEM).copy());
            Helpers.copyTo(inventoryList, inventory.excess);

            builder.set(TFCComponents.BARREL, new BarrelComponent(inventoryList.build(), inventory.tank.getFluid().copy(), sealedTick, recipeTick));
        }
        super.collectImplicitComponents(builder);
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    @Override
    public void ejectInventory()
    {
        super.ejectInventory();
        assert level != null;
        inventory.excess.stream().filter(item -> !item.isEmpty()).forEach(item -> Helpers.spawnItem(level, worldPosition, item));
    }

    public void tickPouring(Level level, BlockPos pos, boolean sealed, Direction facing)
    {
        if (!sealed
            && inventory.tank.isEmpty()
            && facing != Direction.UP)
        {
            final BlockPos faucetPos = pos.relative(facing);
            if (level.getBlockState(faucetPos).isAir())
            {
                final @Nullable IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, faucetPos.below(), Direction.UP);

                if (fluidHandler != null && FluidHelpers.transferExact(this.inventory.tank, fluidHandler, 1))
                {
                    if (level.getGameTime() % 12 == 0 && level instanceof ServerLevel server)
                    {
                        final Fluid fluid = inventory.tank.getFluid().getFluid();
                        final double offset = 0.6;
                        final double dx = facing.getStepX() > 0 ? offset : facing.getStepX() < 0 ? -offset : 0;
                        final double dz = facing.getStepZ() > 0 ? offset : facing.getStepZ() < 0 ? -offset : 0;

                        Helpers.playSound(level, pos, TFCSounds.BARREL_DRIP.get());
                        server.sendParticles(new FluidParticleOption(TFCParticles.BARREL_DRIP.get(), fluid), pos.getX() + 0.5f + dx, pos.getY() + 0.125f, pos.getZ() + 0.5f + dz, 1, 0, 0, 0, 1f);
                    }
                }
            }
            }
    }

    public void onSeal()
    {
        assert level != null;
        if (!level.isClientSide())
        {
            // Drop container items, but allow the main slot to be filled
            Helpers.spawnItem(level, worldPosition, Helpers.removeStack(inventory, SLOT_FLUID_CONTAINER_IN));
            Helpers.spawnItem(level, worldPosition, Helpers.removeStack(inventory, SLOT_FLUID_CONTAINER_OUT));
        }

        sealedTick = Calendars.get(level).getTicks();
        updateRecipe();

        final @Nullable SealedBarrelRecipe recipe = getRecipe();
        if (recipe != null)
        {
            recipe.onSealed(inventory);
            recipeTick = sealedTick;
        }

        markForSync();
        Helpers.playSound(level, worldPosition, TFCSounds.CLOSE_BARREL.get());
    }

    public void onUnseal()
    {
        assert level != null;

        sealedTick = 0L;
        recipeTick = 0L;

        final @Nullable SealedBarrelRecipe recipe = getRecipe();
        if (recipe != null)
        {
            recipe.onUnsealed(inventory);
        }

        updateRecipe();
        markForSync();
        Helpers.playSound(level, worldPosition, TFCSounds.OPEN_BARREL.get());
    }

    @Override
    public boolean canModify()
    {
        return !getBlockState().getValue(BarrelBlock.SEALED);
    }

    private void updateFluidIOSlots()
    {
        assert level != null;
        final ItemStack input = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
        if (!input.isEmpty() && inventory.getStackInSlot(SLOT_FLUID_CONTAINER_OUT).isEmpty())
        {
            FluidHelpers.transferBetweenBlockEntityAndItem(input, this, level, worldPosition, (newOriginalStack, newContainerStack) -> {
                if (newContainerStack.isEmpty())
                {
                    // No new container was produced, so shove the first stack in the output, and clear the input
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, ItemStack.EMPTY);
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_OUT, newOriginalStack);
                }
                else
                {
                    // We produced a new container - this will be the 'filled', so we need to shove *that* in the output
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, newOriginalStack);
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_OUT, newContainerStack);
                }
            });
        }
    }

    private void updateRecipe()
    {
        assert level != null;

        final @Nullable SealedBarrelRecipe oldRecipe = RecipeHelpers.unbox(recipe.value());
        final @Nullable SealedBarrelRecipe newRecipe = getRecipe(); // Trigger the update

        if (oldRecipe != null && newRecipe != null && oldRecipe != newRecipe)
        {
            // The recipe has changed to a new one, so update the recipe ticks
            recipeTick = Calendars.get(level).getTicks();
            markForSync();
        }
    }

    /**
     * Returns the current sealed barrel recipe. This might cause a recipe lookup if the recipe name is currently cached.
     */
    @Nullable
    public SealedBarrelRecipe getRecipe()
    {
        assert level != null;
        if (!recipe.isLoaded())
        {
            // Only find a recipe if we have an empty excess inventory
            recipe.load(inventory.excess.isEmpty()
                ? BarrelRecipe.get(level, TFCRecipeTypes.BARREL_SEALED, inventory)
                : null);
        }
        return RecipeHelpers.unbox(recipe.value());
    }

    @Nullable
    public Component getRecipeTooltip()
    {
        getRecipe(); // Load recipe if present
        return recipe.value() != null
            ? Component.translatable("tfc.recipe.barrel." + recipe.value().id())
            : null;
    }

    public long getSealedTick()
    {
        return sealedTick;
    }

    public long getRecipeTick()
    {
        return recipeTick;
    }

    public long getRemainingTicks()
    {
        assert level != null;

        final @Nullable SealedBarrelRecipe recipe = getRecipe();
        return recipe != null
            ? recipe.getDuration() - (Calendars.get(level).getTicks() - recipeTick)
            : 0;
    }

    public static class BarrelInventory implements DelegateItemHandler, DelegateFluidHandler, NonEmptyInput, FluidTankCallback, net.dries007.tfc.common.recipes.input.BarrelInventory, INBTSerializable<CompoundTag>
    {
        public static final FluidContainerInfo INFO = new FluidContainerInfo() {
            @Override
            public boolean canContainFluid(Fluid input)
            {
                return Helpers.isFluid(input, TFCTags.Fluids.USABLE_IN_BARREL);
            }

            @Override
            public int fluidCapacity()
            {
                return TFCConfig.SERVER.barrelCapacity.get();
            }
        };

        private final BarrelInventoryCallback callback;
        private final InventoryItemHandler inventory;
        private final List<ItemStack> excess;
        private final InventoryFluidTank tank;
        private boolean mutable; // If the inventory is pretending to be mutable, despite the barrel being sealed and preventing extractions / insertions

        BarrelInventory(InventoryBlockEntity<?> entity)
        {
            this((BarrelInventoryCallback) entity);
        }

        public BarrelInventory(BarrelInventoryCallback callback)
        {
            this.callback = callback;
            inventory = new InventoryItemHandler(callback, SLOTS);
            excess = new ArrayList<>();
            tank = new InventoryFluidTank(TFCConfig.SERVER.barrelCapacity.get(), stack -> Helpers.isFluid(stack.getFluid(), TFCTags.Fluids.USABLE_IN_BARREL), this);
        }

        @Override
        public void whileMutable(Runnable action)
        {
            try
            {
                mutable = true;
                action.run();
            }
            finally
            {
                mutable = false;
            }
        }

        @Override
        public void insertItemWithOverflow(ItemStack stack)
        {
            final ItemStack remainder = inventory.insertItem(SLOT_ITEM, stack, false);
            if (!remainder.isEmpty())
            {
                excess.add(remainder);
            }
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            return canModify() ? tank.fill(resource, action) : 0;
        }

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            return canModify() ? tank.drain(resource, action) : FluidStack.EMPTY;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            return canModify() ? tank.drain(maxDrain, action) : FluidStack.EMPTY;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            return canModify() ? inventory.insertItem(slot, stack, simulate) : stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return canModify() ? inventory.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return canModify() && DelegateItemHandler.super.isItemValid(slot, stack);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider)
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT(provider));
            nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("excess", Helpers.writeItemStacksToNbt(provider, excess));
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
        {
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            tank.readFromNBT(provider, nbt.getCompound("tank"));
            Helpers.readItemStacksFromNbt(provider, excess, nbt.getList("excess", Tag.TAG_COMPOUND));
        }

        @Override
        public void fluidTankChanged()
        {
            callback.fluidTankChanged();
        }

        private boolean canModify()
        {
            return mutable || callback.canModify();
        }
    }
}
