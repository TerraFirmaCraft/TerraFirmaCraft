/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.container.ItemStackHandlerCallback;
import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.recipes.IInventoryNoop;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.types.FuelManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PotTileEntity extends AbstractFirepitTileEntity<PotTileEntity.PotInventory>
{
    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.pot");
    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;
    private PotRecipe.Output output;
    private PotRecipe cachedRecipe;
    private int boilingTicks;

    public PotTileEntity()
    {
        super(TFCTileEntities.POT.get(), PotInventory::new, NAME);

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
    public void load(BlockState state, CompoundNBT nbt)
    {
        if (nbt.contains("output"))
        {
            output.deserializeNBT(nbt);
        }
        boilingTicks = nbt.getInt("boilingTicks");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        if (output != null)
        {
            nbt.put("output", output.serializeNBT());
        }
        nbt.putInt("boilingTicks", boilingTicks);
        return super.save(nbt);
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
            return FuelManager.get(stack) != null;
        }
        return slot >= SLOT_EXTRA_INPUT_START && slot <= SLOT_EXTRA_INPUT_END;
    }

    @Override
    protected void handleCooking()
    {
        if (isBoiling())
        {
            if (boilingTicks < cachedRecipe.getDuration())
            {
                boilingTicks++;
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
            }
        }
        else
        {
            boilingTicks = 0;
        }
    }

    @Override
    protected void coolInstantly()
    {
        boilingTicks = 0;
    }

    @Override
    protected void updateCachedRecipe()
    {
        assert level != null;
        cachedRecipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.POT, inventory, level).orElse(null);
    }

    public boolean isBoiling()
    {
        // if we have a recipe, there is no output, and we're hot enough, we boil
        return cachedRecipe != null && output == null && cachedRecipe.isHotEnough(temperature);
    }

    public ActionResultType interactWithOutput(PlayerEntity player, ItemStack stack)
    {
        if (output != null)
        {
            ActionResultType result = output.onInteract(this, player, stack);
            if (output.isEmpty())
            {
                output = null;
                markForSync();
            }
            return result;
        }
        return ActionResultType.PASS;
    }

    @Nullable
    public PotRecipe.Output getOutput()
    {
        return output;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new PotContainer(this, playerInv, windowID);
    }

    public static class PotInventory implements IInventoryNoop, DelegateItemHandler, DelegateFluidHandler, INBTSerializable<CompoundNBT>
    {
        private final ItemStackHandler inventory;
        private final FluidTank tank;

        public PotInventory(InventoryTileEntity<PotInventory> entity)
        {
            this.inventory = new ItemStackHandlerCallback(entity, 9);
            this.tank = new FluidTank(FluidAttributes.BUCKET_VOLUME, fluid -> TFCTags.Fluids.USABLE_IN_POT.contains(fluid.getFluid()));
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
        public CompoundNBT serializeNBT()
        {
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundNBT()));
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            tank.readFromNBT(nbt.getCompound("tank"));
        }
    }
}
