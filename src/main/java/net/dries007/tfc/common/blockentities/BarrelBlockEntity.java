/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.FluidParticleOption;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.capabilities.*;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarrelBlockEntity extends TickableInventoryBlockEntity<BarrelBlockEntity.BarrelInventory> implements ICalendarTickable, BarrelInventoryCallback
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    public static final int SLOTS = 3;

    private static final Component NAME = Helpers.translatable("tfc.block_entity.barrel");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BarrelBlockEntity barrel)
    {
        // Must run before checkForCalendarUpdate(), as this sets the current recipe.
        if (barrel.recipeName != null)
        {
            barrel.recipe = level.getRecipeManager().byKey(barrel.recipeName)
                .map(b -> b instanceof SealedBarrelRecipe r ? r : null)
                .orElse(null);
            barrel.recipeName = null;
        }

        barrel.checkForLastTickSync();
        barrel.checkForCalendarUpdate();

        if (level.getGameTime() % 5 == 0)
        {
            barrel.updateFluidIOSlots();
        }

        final List<ItemStack> excess = barrel.inventory.excess;
        if (!excess.isEmpty() && barrel.inventory.getStackInSlot(SLOT_ITEM).isEmpty())
        {
            barrel.inventory.setStackInSlot(SLOT_ITEM, excess.remove(0));
        }

        final SealedBarrelRecipe recipe = barrel.recipe;
        final boolean sealed = state.getValue(BarrelBlock.SEALED);
        final Direction facing = state.getValue(BarrelBlock.FACING);
        if (recipe != null && sealed)
        {
            final int durationSealed = (int) (Calendars.SERVER.getTicks() - barrel.recipeTick);
            if (!recipe.isInfinite() && durationSealed > recipe.getDuration())
            {
                if (recipe.matches(barrel.inventory, level))
                {
                    // Recipe completed, so fill outputs
                    recipe.assembleOutputs(barrel.inventory);
                    Helpers.playSound(level, barrel.getBlockPos(), recipe.getCompleteSound());
                }

                // In both cases, update the recipe and sync
                barrel.updateRecipe();
                barrel.markForSync();

                // Re-check the recipe. If we have an invalid or infinite recipe, then exit simulation. Otherwise, jump forward to the next recipe completion
                // This handles the case where multiple sequential recipes, such as brining -> pickling -> vinegar preservation would've occurred.
                final SealedBarrelRecipe knownRecipe = barrel.recipe;
                if (knownRecipe != null)
                {
                    knownRecipe.onSealed(barrel.inventory); // We're in a sequential recipe, so apply sealed affects to the new recipe
                }
            }
        }

        if (barrel.needsInstantRecipeUpdate)
        {
            barrel.needsInstantRecipeUpdate = false;
            if (barrel.inventory.excess.isEmpty()) // Excess must be empty for instant recipes to apply
            {
                final RecipeManager recipeManager = level.getRecipeManager();
                Optional.<BarrelRecipe>empty() // For type erasure
                    .or(() -> recipeManager.getRecipeFor(TFCRecipeTypes.BARREL_INSTANT.get(), barrel.inventory, level))
                    .or(() -> recipeManager.getRecipeFor(TFCRecipeTypes.BARREL_INSTANT_FLUID.get(), barrel.inventory, level))
                    .ifPresent(instantRecipe -> {
                        instantRecipe.assembleOutputs(barrel.inventory);
                        if (barrel.soundCooldownTicks == 0)
                        {
                            Helpers.playSound(level, barrel.getBlockPos(), instantRecipe.getCompleteSound());
                            barrel.soundCooldownTicks = 5;
                        }
                    });
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



    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;

    @Nullable private ResourceLocation recipeName;
    @Nullable private SealedBarrelRecipe recipe;
    private long lastUpdateTick = Integer.MIN_VALUE; // The last tick this barrel was updated in serverTick()
    private long sealedTick; // The tick this barrel was sealed
    private long recipeTick; // The tick this barrel started working on the current recipe
    private int soundCooldownTicks = 0;
    @Nullable private BlockPos pourPos = null;

    private boolean needsInstantRecipeUpdate; // If the instant recipe needs to be checked again

    public BarrelBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BARREL.get(), pos, state, BarrelInventory::new, NAME);

        sidedFluidInventory = new SidedHandler.Builder<>(inventory);

        if (TFCConfig.SERVER.barrelEnableAutomation.get())
        {
            final Direction facing = state.hasProperty(BarrelBlock.FACING) ? state.getValue(BarrelBlock.FACING) : Direction.UP;
            final boolean vertical = facing == Direction.UP;
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(SLOT_FLUID_CONTAINER_IN).extract(SLOT_FLUID_CONTAINER_OUT), vertical ? Direction.Plane.HORIZONTAL : d -> d.getAxis() != facing.getAxis() && d.getAxis().isHorizontal())
                .on(new PartialItemHandler(inventory).insert(SLOT_ITEM), facing)
                .on(new PartialItemHandler(inventory).extract(SLOT_ITEM), facing.getOpposite());
            sidedFluidInventory
                .on(new PartialFluidHandler(inventory).insert(), vertical ? Direction.UP : facing.getOpposite())
                .on(new PartialFluidHandler(inventory).extract(), vertical ? d -> d != Direction.UP : d -> d == facing);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return BarrelContainer.create(this, player.getInventory(), containerId);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == Capabilities.FLUID)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsInstantRecipeUpdate = true;
        updateRecipe();
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
                case SLOT_FLUID_CONTAINER_IN -> Helpers.mightHaveCapability(stack, Capabilities.FLUID_ITEM);
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

                final BarrelRecipe recipe = this.recipe;
                if (recipe.matches(inventory, null))
                {
                    recipe.assembleOutputs(inventory);
                }
                updateRecipe();
                markForSync();
            }

            // Re-check the recipe. If we have an invalid or infinite recipe, then exit simulation. Otherwise, jump forward to the next recipe completion
            // This handles the case where multiple sequential recipes, such as brining -> pickling -> vinegar preservation would've occurred.
            final SealedBarrelRecipe knownRecipe = recipe;
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
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("sealedTick", sealedTick);
        nbt.putLong("recipeTick", recipeTick);
        if (recipe != null)
        {
            // Recipe saved to sync to client
            nbt.putString("recipe", recipe.getId().toString());
        }
        else if (recipeName != null)
        {
            nbt.putString("recipeName", recipeName.toString());
        }
        super.saveAdditional(nbt);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        sealedTick = nbt.getLong("sealedTick");
        recipeTick = nbt.getLong("recipeTick");

        recipe = null;
        recipeName = null;
        if (nbt.contains("recipe", Tag.TAG_STRING))
        {
            recipeName = new ResourceLocation(nbt.getString("recipe"));
            if (level != null)
            {
                recipe = level.getRecipeManager().byKey(recipeName)
                    .map(b -> b instanceof SealedBarrelRecipe r ? r : null)
                    .orElse(null);
            }
        }
        super.loadAdditional(nbt);
    }

    @Override
    @Deprecated
    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastUpdateTick(long tick)
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
        if (level.getGameTime() % 20 == 0)
        {
            if (!sealed && !this.inventory.tank.isEmpty() && facing != Direction.UP)
            {
                final BlockPos faucetPos = pos.relative(facing);
                if (level.getBlockState(faucetPos).isAir())
                {
                    final BlockPos pourPos = faucetPos.below();
                    final BlockEntity blockEntity = level.getBlockEntity(pourPos);
                    if (blockEntity != null)
                    {
                        blockEntity.getCapability(Capabilities.FLUID, Direction.UP).ifPresent(cap -> {
                            if (FluidHelpers.couldTransferExact(this.inventory.tank, cap, 1))
                            {
                                this.pourPos = pourPos;
                            }
                        });
                    }
                }
            }
        }
        if (this.pourPos != null && !sealed)
        {
            final BlockEntity blockEntity = level.getBlockEntity(this.pourPos);
            if (blockEntity != null)
            {
                final Fluid fluid = inventory.tank.getFluid().getFluid();
                if (blockEntity.getCapability(Capabilities.FLUID, Direction.UP).map(cap -> FluidHelpers.transferExact(this.inventory.tank, cap, 1)).orElse(false))
                {
                    if (level.getGameTime() % 12 == 0 && level instanceof ServerLevel server)
                    {
                        final double offset = 0.6;
                        final double dx = facing.getStepX() > 0 ? offset : facing.getStepX() < 0 ? -offset : 0;
                        final double dz = facing.getStepZ() > 0 ? offset : facing.getStepZ() < 0 ? -offset : 0;
                        final double x = pos.getX() + 0.5f + dx;
                        final double y = pos.getY() + 0.125f;
                        final double z = pos.getZ() + 0.5f + dz;

                        Helpers.playSound(level, pos, TFCSounds.BARREL_DRIP.get());
                        server.sendParticles(new FluidParticleOption(TFCParticles.BARREL_DRIP.get(), fluid), x, y, z, 1, 0, 0, 0, 1f);
                    }
                }
                else
                {
                    this.pourPos = null;
                }
            }
            else
            {
                this.pourPos = null;
            }
        }
    }

    public void onSeal()
    {
        assert level != null;
        if (!level.isClientSide())
        {
            // Drop container items, but allow the main slot to be filled
            for (int slot : new int[] {SLOT_FLUID_CONTAINER_IN, SLOT_FLUID_CONTAINER_OUT})
            {
                Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(slot));
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }

        sealedTick = Calendars.get(level).getTicks();
        updateRecipe();
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
        sealedTick = recipeTick = 0;
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

    protected void updateRecipe()
    {
        assert level != null;

        final SealedBarrelRecipe oldRecipe = recipe;
        if (inventory.excess.isEmpty())
        {
            // Will only work on a recipe as long as the 'excess' is empty
            recipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.BARREL_SEALED.get(), inventory, level).orElse(null);
            if (recipe != null && oldRecipe != recipe && (oldRecipe == null || !oldRecipe.getId().equals(recipe.getId())))
            {
                // The recipe has changed to a new one, so update the recipe ticks
                recipeTick = Calendars.get(level).getTicks();
                markForSync();
            }
        }
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

    @Nullable
    public BarrelRecipe getRecipe()
    {
        return recipe;
    }

    public long getSealedTick()
    {
        return sealedTick;
    }

    public long getRecipeTick()
    {
        return recipeTick;
    }

    public static class BarrelInventory implements DelegateItemHandler, DelegateFluidHandler, INBTSerializable<CompoundTag>, EmptyInventory, FluidTankCallback
    {
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

        public boolean isInventoryEmpty()
        {
            return tank.getFluid().isEmpty() && excess.isEmpty() && Helpers.isEmpty(inventory);
        }

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
        public CompoundTag serializeNBT()
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));

            if (!excess.isEmpty())
            {
                final ListTag excessNbt = new ListTag();
                for (ItemStack stack : excess)
                {
                    excessNbt.add(stack.save(new CompoundTag()));
                }
                nbt.put("excess", excessNbt);
            }

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            tank.readFromNBT(nbt.getCompound("tank"));

            excess.clear();
            if (nbt.contains("excess"))
            {
                final ListTag excessNbt = nbt.getList("excess", Tag.TAG_COMPOUND);
                for (int i = 0; i < excessNbt.size(); i++)
                {
                    excess.add(ItemStack.of(excessNbt.getCompound(i)));
                }
            }
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
