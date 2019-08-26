/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.util.IHeatConsumerBlock;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@ParametersAreNonnullByDefault
public class TECharcoalForge extends TEInventory implements ITickable, ITileFields
{
    public static final int SLOT_FUEL_MIN = 0;
    public static final int SLOT_FUEL_MAX = 4;
    public static final int SLOT_INPUT_MIN = 5;
    public static final int SLOT_INPUT_MAX = 9;
    public static final int SLOT_EXTRA_MIN = 10;
    public static final int SLOT_EXTRA_MAX = 13;

    public static final int FIELD_TEMPERATURE = 0;

    private HeatRecipe[] cachedRecipes = new HeatRecipe[5];
    private boolean requiresSlotUpdate = false;
    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int airTicks; // Ticks of air provided by bellows

    public TECharcoalForge()
    {
        // 0 - 4 are fuel slots: 0 being the lowest, 4 highest (in order of consumption)
        // 5 - 9 are the input slots. Same arrangement (0 lowest, then alternating L, R, L high, R high)
        // 10 - 13 are the extra slots for molds and stuff. 10 at the top, 14 at the bottom. Iterate in that order
        super(14);

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        airTicks = 0;

        for (int i = 0; i < cachedRecipes.length; i++)
        {
            cachedRecipes[i] = null;
        }
    }

    public void onAirIntake(int amount)
    {
        airTicks += amount;
        if (airTicks > 600)
        {
            airTicks = 600;
        }
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
                // Double fuel consumption if using bellows
                burnTicks -= airTicks > 0 ? 2 : 1;
            }
            if (burnTicks <= 0)
            {
                // Consume fuel
                ItemStack stack = inventory.getStackInSlot(SLOT_FUEL_MIN);
                if (stack.isEmpty())
                {
                    world.setBlockState(pos, state.withProperty(LIT, false));
                    burnTicks = 0;
                    burnTemperature = 0;
                }
                else
                {
                    inventory.setStackInSlot(SLOT_FUEL_MIN, ItemStack.EMPTY);
                    requiresSlotUpdate = true;
                    Fuel fuel = FuelManager.getFuel(stack);
                    burnTicks = fuel.getAmount();
                    burnTemperature = fuel.getTemperature();
                }
            }
        }
        else if (burnTemperature > 0)
        {
            // If not lit, stop burning
            burnTemperature = 0;
            burnTicks = 0;
        }

        // Update bellows air
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
            float targetTemperature = Math.min(MAX_TEMPERATURE, burnTemperature + airTicks);
            if (temperature < targetTemperature)
            {
                // Modifier for heating = 2x for bellows
                temperature += (airTicks > 0 ? 2 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
            }
            else if (temperature > targetTemperature)
            {
                // Modifier for cooling = 0.5x for bellows
                temperature -= (airTicks > 0 ? 0.5 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
            }

            // Provide heat to blocks that are one block above
            Block blockUp = world.getBlockState(pos.up()).getBlock();
            if (blockUp instanceof IHeatConsumerBlock)
            {
                ((IHeatConsumerBlock) blockUp).acceptHeat(world, pos.up(), temperature);
            }

            // Update items in slots
            // Loop through input + 2 output slots
            for (int i = SLOT_INPUT_MIN; i <= SLOT_INPUT_MAX; i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (cap != null)
                {
                    // Update temperature of item
                    float itemTemp = cap.getTemperature();
                    if (temperature > itemTemp)
                    {
                        CapabilityItemHeat.addTemp(cap);
                    }

                    // Handle possible melting, or conversion (if reach 1599 = pit kiln temperature)
                    handleInputMelting(stack, i);
                }
            }
        }

        // This is here to avoid duplication glitches
        if (requiresSlotUpdate)
        {
            cascadeFuelSlots();
        }
    }

    public void onCreate()
    {
        burnTicks = 200;
        burnTemperature = 500;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        this.markDirty();
        requiresSlotUpdate = true;
        updateCachedRecipes();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInteger("burnTicks");
        airTicks = nbt.getInteger("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        super.readFromNBT(nbt);

        updateCachedRecipes();
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("temperature", temperature);
        nbt.setInteger("burnTicks", burnTicks);
        nbt.setInteger("airTicks", airTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        return super.writeToNBT(nbt);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        // All slots have limit 1
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot <= SLOT_FUEL_MAX)
        {
            // Fuel slots - anything that is a valid TFC fuel
            return FuelManager.isItemForgeFuel(stack);
        }
        else if (slot <= SLOT_INPUT_MAX)
        {
            // Input slots - anything that can heat up
            return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        }
        else
        {
            // Extra slots - anything that can heat up and hold fluids
            return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        }
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
            TerraFirmaCraft.getLog().warn("Invalid field ID {} in TECharcoalForge#setField", index);
        }
    }

    @Override
    public int getField(int index)
    {
        if (index == FIELD_TEMPERATURE)
        {
            return (int) temperature;
        }
        TerraFirmaCraft.getLog().warn("Invalid field ID {} in TECharcoalForge#getField", index);
        return 0;
    }

    private void handleInputMelting(ItemStack stack, int startIndex)
    {
        HeatRecipe recipe = cachedRecipes[startIndex - SLOT_INPUT_MIN];
        IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);

        if (recipe != null && cap != null && recipe.isValidTemperature(cap.getTemperature()))
        {
            // Handle possible metal output
            FluidStack fluidStack = recipe.getOutputFluid(stack);
            float itemTemperature = cap.getTemperature();
            if (fluidStack != null)
            {
                // Loop through all input slots
                for (int i = SLOT_EXTRA_MIN; i <= SLOT_EXTRA_MAX; i++)
                {
                    // While the fluid is still waiting
                    if (fluidStack.amount <= 0)
                    {
                        break;
                    }
                    // Try an output slot
                    ItemStack output = inventory.getStackInSlot(i);
                    // Fill the fluid
                    IFluidHandler fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if (fluidHandler != null)
                    {
                        int amountFilled = fluidHandler.fill(fluidStack.copy(), true);
                        if (amountFilled > 0)
                        {
                            fluidStack.amount -= amountFilled;

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

            // Handle possible item output
            inventory.setStackInSlot(startIndex, recipe.getOutputStack(stack));
        }
    }

    private void cascadeFuelSlots()
    {
        // This will cascade all fuel down to the lowest available slot
        int lowestAvailSlot = 0;
        for (int i = 0; i <= SLOT_FUEL_MAX; i++)
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

    private void updateCachedRecipes()
    {
        // cache heat recipes for each input
        for (int i = SLOT_INPUT_MIN; i <= SLOT_INPUT_MAX; i++)
        {
            cachedRecipes[i - SLOT_INPUT_MIN] = null;
            ItemStack inputStack = inventory.getStackInSlot(i);
            if (!inputStack.isEmpty())
            {
                cachedRecipes[i - SLOT_INPUT_MIN] = HeatRecipe.get(inputStack);
            }
        }
    }
}