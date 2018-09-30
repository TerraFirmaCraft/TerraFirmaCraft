/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.recipes.heat.HeatRecipe;
import net.dries007.tfc.objects.recipes.heat.HeatRecipeManager;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.FuelManager;

import static net.dries007.tfc.objects.blocks.BlockFirePit.LIT;

@ParametersAreNonnullByDefault
public class TEFirePit extends TESidedInventory implements ITickable
{
    // Slot 0 - 3 = fuel slots with 3 being input, 4 = normal input slot, 5 and 6 are output slots 1 + 2
    public static final int SLOT_FUEL_CONSUME = 0;
    public static final int SLOT_FUEL_INPUT = 3;
    public static final int SLOT_ITEM_INPUT = 4;
    public static final int SLOT_OUTPUT_1 = 5;
    public static final int SLOT_OUTPUT_2 = 6;

    private static boolean isStackFuel(ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "logWood");
    }

    private static boolean isStackCookable(ItemStack stack)
    {
        return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null); // todo
    }
    // todo: adjust this to change how fast firepit heats up items (item_heating_mod) or how fast it heats up (temperature_modifier)
    private static final float TEMPERATURE_MODIFIER = 1f;
    private static final float ITEM_HEATING_MODIFIER = 3f;

    private boolean requiresSlotUpdate = false;
    private float temperature; // Current Temperature
    private float burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int pickupTimer;

    public TEFirePit()
    {
        super(7);

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        pickupTimer = 0;
    }

    @Override
    public void update()
    {
        // do timer things
        if (world.isRemote) return;
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT))
        {
            // Update fuel
            if (burnTicks > 0)
            {
                burnTicks--;
            }
            if (burnTicks == 0)
            {
                // Consume fuel
                ItemStack stack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
                if (stack.isEmpty())
                {
                    world.setBlockState(pos, state.withProperty(LIT, false));
                    burnTicks = 0;
                    burnTemperature = 0;
                }
                else
                {
                    inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
                    Fuel fuel = FuelManager.getFuel(stack);
                    burnTicks += fuel.getAmount();
                    burnTemperature = fuel.getTemperature();
                }
            }
        }

        // Always update temperature / cooking, until the fire pit is not hot anymore
        if (temperature > 0 || burnTemperature > 0)
        {
            // Update temperature
            if (temperature < burnTemperature)
            {
                temperature += TEMPERATURE_MODIFIER;
            }
            else if (temperature > burnTemperature)
            {
                temperature -= TEMPERATURE_MODIFIER;
            }

            // Update items in slots
            // Loop through input + 2 output slots
            for (int i = SLOT_ITEM_INPUT; i < SLOT_ITEM_INPUT + 3; i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (cap != null)
                {
                    float itemTemp = cap.getTemperature();
                    if (temperature > itemTemp)
                    {
                        CapabilityItemHeat.addTemp(cap, ITEM_HEATING_MODIFIER);
                        stack.setTagCompound(cap.serializeNBT());
                    }

                    // This will melt + consume the input stack
                    // Output stacks are assumed to not melt (see the case of ceramic molds in the output)
                    if (cap.isMolten() && i == SLOT_ITEM_INPUT)
                    {
                        handleInputMelting(stack);
                    }
                }
            }
        }

        // This is here to avoid duplication glitches
        if (requiresSlotUpdate)
        {
            cascadeFuelSlots();
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        this.markDirty();
        requiresSlotUpdate = true;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return slot <= 4 ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FUEL_INPUT: // Valid fuel if it is registered correctly
                return FuelManager.isItemFuel(stack);
            case SLOT_ITEM_INPUT: // Valid input as long as it can be heated
                return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            case SLOT_OUTPUT_1:
            case SLOT_OUTPUT_2: // Valid insert into output as long as it can hold fluids and is heat-able
                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            default: // Other fuel slots + output slots
                return false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getFloat("burnTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("temperature", temperature);
        nbt.setFloat("burnTicks", burnTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        return super.writeToNBT(nbt);
    }

    public void onCreate(ItemStack log)
    {
        Fuel fuel = FuelManager.getFuel(log);
        burnTicks += fuel.getAmount();
        burnTemperature += fuel.getTemperature();
    }

    private void cascadeFuelSlots()
    {
        // This will cascade all fuel down to the lowest available slot
        int lowestAvailSlot = 0;
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Move to lowest avail slot
                if (i > lowestAvailSlot)
                {
                    inventory.setStackInSlot(lowestAvailSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestAvailSlot++;
            }
        }
        requiresSlotUpdate = false;
    }

    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Debugging Fire pit:");
        TerraFirmaCraft.getLog().debug("Temp {} | Burn Temp {} | Fuel Ticks {}", temperature, burnTemperature, burnTicks);
        TerraFirmaCraft.getLog().debug("Burning? {}", world.getBlockState(pos).getValue(LIT));
    }

    private void handleInputMelting(ItemStack stack)
    {
        HeatRecipe recipe = HeatRecipeManager.get(stack);
        if (recipe != null)
        {
            // Handle possible metal output
            FluidStack fluidStack = recipe.getOutputMetal(stack);
            if (fluidStack != null)
            {
                ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT_1);
                IFluidHandler fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler != null)
                {
                    int amountFilled = fluidHandler.fill(fluidStack.copy(), true);
                    fluidStack.amount -= amountFilled;
                }
                if (fluidStack.amount > 0)
                {
                    output = inventory.getStackInSlot(SLOT_OUTPUT_2);
                    fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                    if (fluidHandler != null)
                    {
                        fluidHandler.fill(fluidStack, true);
                    }
                }
            }

            // Handle possible item output
            ItemStack outputStack = recipe.getOutputStack();
            if (outputStack != null)
            {
                outputStack = inventory.insertItem(SLOT_OUTPUT_1, outputStack, false);
                if (!outputStack.isEmpty())
                {
                    inventory.insertItem(SLOT_OUTPUT_2, outputStack, false);
                }
            }

            // Handle removal of input
            ItemStack inputStack = recipe.consumeInput(stack);
            inventory.setStackInSlot(SLOT_ITEM_INPUT, inputStack);
        }
    }

}
