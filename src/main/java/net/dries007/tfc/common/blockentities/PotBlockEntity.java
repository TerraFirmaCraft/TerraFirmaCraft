/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.*;
import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PotBlockEntity extends AbstractFirepitBlockEntity<PotBlockEntity.PotInventory>
{
    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.pot");

    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;
    @Nullable private PotRecipe.Output output;
    @Nullable private PotRecipe cachedRecipe;
    private int boilingTicks;

    public PotBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.POT.get(), pos, state, PotInventory::new, NAME);

        output = null;
        cachedRecipe = null;
        boilingTicks = 0;

        // Items in top, Fuel and fluid in sides, items and fluid out sides
        sidedInventory
            .on(new PartialItemHandler(inventory).insert(SLOT_FUEL_INPUT).extract(4, 5, 6, 7, 8), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).insert(4, 5, 6, 7, 8), Direction.UP);

        sidedFluidInventory = new SidedHandler.Builder<IFluidHandler>(inventory)
            .on(inventory, Direction.Plane.HORIZONTAL);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        if (nbt.contains("output"))
        {
            output = PotRecipe.Output.read(nbt.getCompound("output"));
        }
        boilingTicks = nbt.getInt("boilingTicks");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        if (output != null)
        {
            nbt.put("output", PotRecipe.Output.write(output));
        }
        nbt.putInt("boilingTicks", boilingTicks);
        super.saveAdditional(nbt);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_FUEL_INPUT)
        {
            return Fuel.get(stack) != null;
        }
        return slot >= SLOT_EXTRA_INPUT_START && slot <= SLOT_EXTRA_INPUT_END;
    }

    @Override
    protected void handleCooking()
    {
        if (isBoiling())
        {
            assert cachedRecipe != null;
            if (boilingTicks < cachedRecipe.getDuration())
            {
                boilingTicks++;
                if (boilingTicks == 1) markForSync();
            }
            else
            {
                // Create output
                // Save the recipe here, as setting inventory will call setAndUpdateSlots, which will clear the cached recipe before output is created
                final PotRecipe recipe = cachedRecipe;
                final PotRecipe.Output output = recipe.getOutput(inventory);

                // Clear inputs
                inventory.tank.setFluid(FluidStack.EMPTY);
                for (int slot = SLOT_EXTRA_INPUT_START; slot <= SLOT_EXTRA_INPUT_END; slot++)
                {
                    // Consume items, but set container items if they exist
                    inventory.setStackInSlot(slot, inventory.getStackInSlot(slot).getContainerItem());
                }

                output.onFinish(inventory); // Let the output handle filling into the empty pot
                if (!output.isEmpty()) // Then, if we still have contents, save the output
                {
                    this.output = output;
                }

                // Reset recipe progress
                cachedRecipe = null;
                boilingTicks = 0;
                updateCachedRecipe();
                markForSync();
            }
        }
        else if (boilingTicks > 0) // catch accidentally not syncing when it dips below temperature
        {
            boilingTicks = 0;
            markForSync();
        }
    }

    @Override
    protected void coolInstantly()
    {
        boilingTicks = 0;
        markForSync();
    }

    @Override
    protected void updateCachedRecipe()
    {
        assert level != null;
        cachedRecipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.POT.get(), inventory, level).orElse(null);
    }

    public boolean isBoiling()
    {
        // if we have a recipe, there is no output, and we're hot enough, we boil
        return cachedRecipe != null && output == null && cachedRecipe.isHotEnough(temperature);
    }

    /**
     * The amount of info pots actually sync to clients is low. So checking output, cached recipe, etc. won't work.
     */
    public boolean shouldRenderAsBoiling()
    {
        return boilingTicks > 0;
    }

    public InteractionResult interactWithOutput(Player player, ItemStack stack)
    {
        if (output != null)
        {
            InteractionResult result = output.onInteract(this, player, stack);
            if (output.isEmpty())
            {
                output = null;
                markForSync();
            }
            return result;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    public PotRecipe.Output getOutput()
    {
        return output;
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player)
    {
        return PotContainer.create(this, playerInv, windowID);
    }

    public static class PotInventory implements EmptyInventory, DelegateItemHandler, DelegateFluidHandler, INBTSerializable<CompoundTag>
    {
        private final PotBlockEntity pot;
        private final ItemStackHandler inventory;
        private final FluidTank tank;

        public PotInventory(InventoryBlockEntity<PotInventory> entity)
        {
            this.pot = (PotBlockEntity) entity;
            this.inventory = new InventoryItemHandler(entity, 9);
            this.tank = new FluidTank(FluidHelpers.BUCKET_VOLUME, fluid -> Helpers.isFluid(fluid.getFluid(), TFCTags.Fluids.USABLE_IN_POT));
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return pot.isBoiling() && slot >= SLOT_EXTRA_INPUT_START ? ItemStack.EMPTY : inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            tank.readFromNBT(nbt.getCompound("tank"));
        }
    }
}
