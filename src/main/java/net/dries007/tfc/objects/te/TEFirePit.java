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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.objects.blocks.devices.BlockFirePit.LIT;

@ParametersAreNonnullByDefault
public class TEFirePit extends TEInventory implements ITickable, ITileFields
{
    // Slot 0 - 3 = fuel slots with 3 being input, 4 = normal input slot, 5 and 6 are output slots 1 + 2
    public static final int SLOT_FUEL_CONSUME = 0;
    public static final int SLOT_FUEL_INPUT = 3;
    public static final int SLOT_ITEM_INPUT = 4;
    public static final int SLOT_OUTPUT_1 = 5;
    public static final int SLOT_OUTPUT_2 = 6;

    public static final int FIELD_TEMPERATURE = 0;

    private HeatRecipe cachedRecipe;
    private boolean requiresSlotUpdate = false;
    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private int airTicks; // Ticks of bellows provided air remaining
    private float burnTemperature; // Temperature provided from the current item of fuel

    public TEFirePit()
    {
        super(7);

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        cachedRecipe = null;
    }

    @Override
    public void update()
    {
        if (world.isRemote) return;
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT))
        {
            // Update fuel
            if (burnTicks > 0)
            {
                burnTicks -= airTicks > 0 ? 2 : 1;
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
                    requiresSlotUpdate = true;
                    Fuel fuel = FuelManager.getFuel(stack);
                    burnTicks += fuel.getAmount();
                    burnTemperature = fuel.getTemperature();
                }
            }
        }

        // Update air ticks
        if (airTicks > 0)
        {
            airTicks--;
        }
        else
        {
            airTicks = 0;
        }

        // Always update temperature / cooking, until the fire pit is not hot anymore
        if (temperature > 0 || burnTemperature > 0)
        {
            // Update temperature
            float targetTemp = Math.min(MAX_TEMPERATURE, burnTemperature + airTicks);
            if (temperature < targetTemp)
            {
                temperature += (airTicks > 0 ? 2 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
            }
            else if (temperature > targetTemp)
            {
                temperature -= (airTicks > 0 ? 0.5 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
            }

            // The fire pit is nice: it will automatically move input to output for you, saving the trouble of losing the input due to melting / burning
            ItemStack stack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (cap != null)
            {
                float itemTemp = cap.getTemperature();
                if (temperature > itemTemp)
                {
                    CapabilityItemHeat.addTemp(cap);
                }

                handleInputMelting(stack);
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

        // Update cached recipe
        cachedRecipe = HeatRecipe.get(inventory.getStackInSlot(SLOT_ITEM_INPUT));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInteger("burnTicks");
        airTicks = nbt.getInteger("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        super.readFromNBT(nbt);

        // Update recipe cache
        cachedRecipe = HeatRecipe.get(inventory.getStackInSlot(SLOT_ITEM_INPUT));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("temperature", temperature);
        nbt.setInteger("burnTicks", burnTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        return super.writeToNBT(nbt);
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
                return FuelManager.isItemFuel(stack) && !FuelManager.isItemForgeFuel(stack);
            case SLOT_ITEM_INPUT: // Valid input as long as it can be heated
                return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            case SLOT_OUTPUT_1:
            case SLOT_OUTPUT_2: // Valid insert into output as long as it can hold fluids and is heat-able
                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            default: // Other fuel slots + output slots
                return false;
        }
    }

    public void onCreate(ItemStack log)
    {
        Fuel fuel = FuelManager.getFuel(log);
        burnTicks = fuel.getAmount();
        burnTemperature = fuel.getTemperature();
    }

    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Debugging Fire pit:");
        TerraFirmaCraft.getLog().debug("Temp {} | Burn Temp {} | Fuel Ticks {}", temperature, burnTemperature, burnTicks);
        TerraFirmaCraft.getLog().debug("Burning? {}", world.getBlockState(pos).getValue(LIT));
    }

    @Override
    public int getFieldCount()
    {
        return 1;
    }

    @Override
    public void setField(int index, int value)
    {
        if (index == FIELD_TEMPERATURE)
        {
            this.temperature = (float) value;
        }
        else
        {
            TerraFirmaCraft.getLog().warn("Invalid Field ID {} in TEFirePit#setField", index);
        }
    }

    @Override
    public int getField(int index)
    {
        if (index == FIELD_TEMPERATURE)
        {
            return (int) temperature;
        }
        TerraFirmaCraft.getLog().warn("Invalid Field ID {} in TEFirePit#getField", index);
        return 0;
    }

    public void onAirIntake(int amount)
    {
        airTicks += amount;
        if (airTicks > 600)
        {
            airTicks = 600;
        }
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

    private void handleInputMelting(ItemStack stack)
    {
        IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);

        if (cachedRecipe != null && cap != null && cachedRecipe.isValidTemperature(cap.getTemperature()))
        {
            // Handle possible metal output
            FluidStack fluidStack = cachedRecipe.getOutputFluid(stack);
            float itemTemperature = cap.getTemperature();
            if (fluidStack != null)
            {
                ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT_1);
                IFluidHandler fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler != null)
                {
                    int amountFilled = fluidHandler.fill(fluidStack.copy(), true);
                    fluidStack.amount -= amountFilled;

                    // If the fluid was filled, make sure to make it the same temperature
                    IItemHeat heatHandler = output.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                    if (heatHandler != null)
                    {
                        heatHandler.setTemperature(itemTemperature);
                    }
                }
                if (fluidStack.amount > 0)
                {
                    output = inventory.getStackInSlot(SLOT_OUTPUT_2);
                    fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                    if (fluidHandler != null)
                    {
                        int amountFilled = fluidHandler.fill(fluidStack, true);

                        if (amountFilled > 0)
                        {
                            // If the fluid was filled, make sure to make it the same temperature
                            IItemHeat heatHandler = output.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                            if (heatHandler != null)
                            {
                                heatHandler.setTemperature(itemTemperature);
                            }
                        }
                    }
                }
            }

            // Handle removal of input
            ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            ItemStack outputStack = cachedRecipe.getOutputStack(inputStack);

            inputStack.shrink(1);
            if (!outputStack.isEmpty())
            {
                outputStack = inventory.insertItem(SLOT_OUTPUT_1, outputStack, false);
                if (!outputStack.isEmpty())
                {
                    outputStack = inventory.insertItem(SLOT_OUTPUT_2, outputStack, false);
                }
                if (!outputStack.isEmpty()) // Couldn't merge directly
                {
                    // If both the output and input is the same food, try merging-updating the creation date to the earliest one
                    IFood foodCap = outputStack.getCapability(CapabilityFood.CAPABILITY, null);
                    if (foodCap != null)
                    {
                        // First slot merging if possible
                        ItemStack slot1Stack = inventory.getStackInSlot(SLOT_OUTPUT_1);
                        if (outputStack.isItemEqual(slot1Stack) && slot1Stack.getCount() < slot1Stack.getMaxStackSize())
                        {
                            // If both are the same food and are mergeable, ensure both have the same earliest creation date
                            IFood output1Cap = slot1Stack.getCapability(CapabilityFood.CAPABILITY, null);
                            if (output1Cap != null)
                            {
                                long earliest = Math.max(output1Cap.getCreationDate(), foodCap.getCreationDate());
                                output1Cap.setCreationDate(earliest);
                                int merge = Math.min(slot1Stack.getMaxStackSize(), outputStack.getCount() + slot1Stack.getCount());
                                // Why not inventory#insertItem you ask?
                                // 1 - we would need to update the creation date of the outputStack first before inserting, which would undesirable propagate to the second slot or even further when spit out to the world
                                // 2 - if one of then have a trait while the other don't (ie: salted), they will not merge even after updating the creation date
                                // 3 - It is possible to fix all of the above, but you will need to handle all possible scenarios which would make you want to just spit out the thing to the world.
                                outputStack.shrink(merge - slot1Stack.getCount());
                                slot1Stack.setCount(merge);
                            }
                        }

                        //Second slot merging (remaining) if possible
                        ItemStack slot2Stack = inventory.getStackInSlot(SLOT_OUTPUT_2);
                        if (!outputStack.isEmpty() && outputStack.isItemEqual(slot2Stack) && slot2Stack.getCount() < slot2Stack.getMaxStackSize())
                        {
                            // If both are the same food and are mergeable, ensure both have the same earliest creation date
                            IFood output2Cap = slot2Stack.getCapability(CapabilityFood.CAPABILITY, null);
                            if (output2Cap != null)
                            {
                                long earliest = Math.max(output2Cap.getCreationDate(), foodCap.getCreationDate());
                                output2Cap.setCreationDate(earliest);
                                int merge = Math.min(slot2Stack.getMaxStackSize(), outputStack.getCount() + slot2Stack.getCount());
                                outputStack.shrink(merge - slot2Stack.getCount());
                                slot2Stack.setCount(merge);
                            }
                        }
                    }
                    if (!outputStack.isEmpty())
                    {
                        // If we got here, we failed merging foods, or this wasn't a food at all
                        // Spit out the item in a random position. This is gonna avoid the food catching fire in the fire pit.
                        // Unless this fire pit is placed near walls.
                        Helpers.spawnItemStack(world, pos.offset(EnumFacing.byHorizontalIndex(Constants.RNG.nextInt(4))), outputStack);
                    }
                }
            }
        }
    }
}