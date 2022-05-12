/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.capabilities.*;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarrelBlockEntity extends TickableInventoryBlockEntity<BarrelBlockEntity.BarrelInventory> implements ICalendarTickable
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    public static final int SLOTS = 3;

    private static final Component NAME = new TranslatableComponent("tfc.block_entity.barrel");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BarrelBlockEntity barrel)
    {
        barrel.checkForLastTickSync();
        barrel.checkForCalendarUpdate();

        if (barrel.needsRecipeUpdate)
        {
            barrel.updateRecipe();
        }
        if (level.getGameTime() % 5 == 0) barrel.updateFluidIOSlots();
        List<ItemStack> excess = barrel.inventory.excess;
        if (!excess.isEmpty() && barrel.inventory.getStackInSlot(SLOT_ITEM).isEmpty())
        {
            barrel.inventory.setStackInSlot(SLOT_ITEM, excess.remove(0));
        }

        final SealedBarrelRecipe recipe = barrel.recipe;
        if (recipe != null && state.getValue(BarrelBlock.SEALED))
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
            }
        }

        if (barrel.needsInstantRecipeUpdate)
        {
            barrel.needsInstantRecipeUpdate = false;
            if (barrel.inventory.excess.isEmpty()) // Excess must be empty for instant recipes to apply
            {
                level.getRecipeManager().getRecipeFor(TFCRecipeTypes.BARREL_INSTANT.get(), barrel.inventory, level)
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
    }

    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;

    @Nullable private SealedBarrelRecipe recipe;
    private long lastUpdateTick; // The last tick this barrel was updated in serverTick()
    private long sealedTick; // The tick this barrel was sealed
    private long recipeTick; // The tick this barrel started working on the current recipe
    private int soundCooldownTicks = 0;

    private boolean needsRecipeUpdate;
    private boolean needsInstantRecipeUpdate; // If the instant recipe needs to be checked again

    public BarrelBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BARREL.get(), pos, state, BarrelInventory::new, NAME);

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(SLOT_FLUID_CONTAINER_IN).extract(SLOT_FLUID_CONTAINER_OUT), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).insert(SLOT_ITEM), Direction.UP)
            .on(new PartialItemHandler(inventory).extract(SLOT_ITEM), Direction.DOWN);

        sidedFluidInventory = new SidedHandler.Builder<IFluidHandler>(inventory)
            .on(new PartialFluidHandler(inventory).insert(), Direction.UP)
            .on(new PartialFluidHandler(inventory).extract(), Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
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
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsRecipeUpdate = needsInstantRecipeUpdate = true;
        if (level != null && level.isClientSide) updateRecipe();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return switch (slot) {
            case SLOT_FLUID_CONTAINER_IN -> stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent() || stack.getItem() instanceof BucketItem;
            case SLOT_ITEM -> ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.HUGE);
            default -> true;
        };
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        if (level.isClientSide)
        {
            return;
        }
        while (ticks > 0)
        {
            ticks = 0;
            if (recipe != null && !recipe.isInfinite() && getBlockState().getValue(BarrelBlock.SEALED))
            {
                final long finishTick = sealedTick + recipe.getDuration();
                if (finishTick <= Calendars.SERVER.getTicks())
                {
                    // Mark to run this transaction again in case this recipe produces valid output for another which could potentially finish in this time period.
                    ticks = 1;
                    final long offset = finishTick - Calendars.SERVER.getTicks();
                    Calendars.SERVER.runTransaction(offset, offset, () -> {
                        final BarrelRecipe recipe = this.recipe;
                        if (recipe.matches(inventory, null))
                        {
                            // Recipe completed, so fill outputs
                            recipe.assembleOutputs(inventory);
                            this.recipe = null;
                        }
                        updateRecipe();
                    });
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("sealedTick", sealedTick);
        nbt.putLong("recipeTick", recipeTick);
        super.saveAdditional(nbt);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        sealedTick = nbt.getLong("sealedTick");
        recipeTick = nbt.getLong("recipeTick");
        super.loadAdditional(nbt);
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
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

    public void onSeal()
    {
        assert level != null;
        if (!level.isClientSide())
        {
            // Drop container items, but allow the main slot to be filled
            for (int slot : new int[] { SLOT_FLUID_CONTAINER_IN, SLOT_FLUID_CONTAINER_OUT })
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
    }

    public void onUnseal()
    {
        sealedTick = recipeTick = 0;
        if (recipe != null)
        {
            recipe.onUnsealed(inventory);
        }
        updateRecipe();
        markForSync();
    }

    protected void updateRecipe()
    {
        assert level != null;

        final SealedBarrelRecipe oldRecipe = recipe;
        if (inventory.excess.isEmpty())
        {
            // Will only work on a recipe as long as the 'excess' is empty
            recipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.BARREL_SEALED.get(), inventory, level).orElse(null);
            if (recipe != null && oldRecipe != recipe)
            {
                // The recipe has changed to a new one, so update the recipe ticks
                recipeTick = Calendars.get(level).getTicks();
                markForSync();
            }
        }

        needsRecipeUpdate = false;
    }

    private void updateFluidIOSlots()
    {
        assert level != null;
        ItemStack input = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
        if (!input.isEmpty() && inventory.getStackInSlot(SLOT_FLUID_CONTAINER_OUT).isEmpty())
        {
            input.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(itemCap -> {
                final int amount = itemCap.getFluidInTank(0).getAmount();
                if (amount > 0 && FluidHelpers.transferUpTo(itemCap, inventory.tank, amount))
                {
                    Helpers.playSound(level, getBlockPos(), SoundEvents.BUCKET_EMPTY);
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_OUT, itemCap.getContainer());
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, ItemStack.EMPTY);
                }
                else
                {
                    FluidActionResult result = FluidUtil.tryFillContainer(input, inventory, TFCConfig.SERVER.barrelCapacity.get(), null, true);
                    if (result.isSuccess())
                    {
                        Helpers.playSound(level, getBlockPos(), SoundEvents.BUCKET_FILL);
                        inventory.setStackInSlot(SLOT_FLUID_CONTAINER_OUT, result.getResult());
                        inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, ItemStack.EMPTY);
                    }
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

    public static class BarrelInventory implements DelegateItemHandler, DelegateFluidHandler, INBTSerializable<CompoundTag>, EmptyInventory
    {
        private final BarrelBlockEntity barrel;
        private final InventoryItemHandler inventory;
        private final List<ItemStack> excess;
        private final FluidTank tank;
        private boolean mutable; // If the inventory is pretending to be mutable, despite the barrel being sealed and preventing extractions / insertions

        BarrelInventory(InventoryBlockEntity<?> entity)
        {
            barrel = (BarrelBlockEntity) entity;
            inventory = new InventoryItemHandler(entity, SLOTS);
            excess = new ArrayList<>();
            tank = new FluidTank(TFCConfig.SERVER.barrelCapacity.get(), stack -> Helpers.isFluid(stack.getFluid(), TFCTags.Fluids.USABLE_IN_BARREL));
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

        private boolean canModify()
        {
            return mutable || !barrel.getBlockState().getValue(BarrelBlock.SEALED);
        }
    }
}
