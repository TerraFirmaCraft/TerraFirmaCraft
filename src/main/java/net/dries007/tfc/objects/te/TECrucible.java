/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.ISmallVesselHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.Helpers;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class TECrucible extends TETickableInventory implements ITickable, ITileFields, IItemHandlerSidedCallback
{
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 8;
    public static final int SLOT_OUTPUT = 9;

    public static final int FIELD_TEMPERATURE = 0;

    private final Alloy alloy;
    private final IItemHandler inventoryWrapperExtract;
    private final IItemHandler inventoryWrapperInsert;

    private final HeatRecipe[] cachedRecipes;
    private Metal alloyResult;
    private float temperature;
    private float targetTemperature;
    private int lastFillTimer;

    public TECrucible()
    {
        super(10);

        this.alloy = new Alloy(ConfigTFC.Devices.CRUCIBLE.tank); // Side effect: Maximum amount only matches config if not loading from disk
        this.inventoryWrapperExtract = new ItemHandlerSidedWrapper(this, inventory, EnumFacing.DOWN);
        this.inventoryWrapperInsert = new ItemHandlerSidedWrapper(this, inventory, EnumFacing.UP);

        this.temperature = 0;
        this.lastFillTimer = 0;
        this.cachedRecipes = new HeatRecipe[9];
        Arrays.fill(this.cachedRecipes, null);
    }

    public void acceptHeat(float temperature)
    {
        if (temperature > targetTemperature)
        {
            targetTemperature = temperature;
        }
    }

    public int addMetal(Metal metal, int amount)
    {
        int overflow = Math.max(0, alloy.getAmount() + amount - alloy.getMaxAmount()); // Amount which cannot be inserted
        alloy.add(metal, amount);

        // Update crucible temperature to match
        temperature = metal.getMeltTemp();
        targetTemperature = metal.getMeltTemp();

        // Alloy changed, so sync to client
        markForSync();
        return overflow;
    }

    @Override
    public void update()
    {
        super.update();
        if (!world.isRemote)
        {
            temperature = CapabilityItemHeat.adjustTempTowards(temperature, targetTemperature, (float) ConfigTFC.Devices.TEMPERATURE.heatingModifier);
            if (targetTemperature > 0)
            {
                // Crucible target temperature decays constantly, since it is set by outside providers
                targetTemperature -= (float) ConfigTFC.Devices.TEMPERATURE.heatingModifier;
            }

            // Input draining
            boolean canFill = lastFillTimer <= 0;
            for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
            {
                ItemStack inputStack = inventory.getStackInSlot(i);
                IItemHeat cap = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (cap != null)
                {
                    // Always heat up the item regardless if it is melting or not
                    if (cap.getTemperature() < temperature)
                    {
                        CapabilityItemHeat.addTemp(cap);
                    }
                    if (cachedRecipes[i] != null)
                    {
                        if (cachedRecipes[i].isValidTemperature(cap.getTemperature()))
                        {
                            alloy.add(inputStack, cachedRecipes[i]);
                            inventory.setStackInSlot(i, cachedRecipes[i].getOutputStack(inputStack));
                            // Update reference since it may have changed from recipe output
                            inputStack = inventory.getStackInSlot(i);
                            cap = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                            markForSync();
                        }
                    }
                }
                // Try and drain fluid
                if (cap instanceof IMoldHandler)
                {
                    IMoldHandler mold = (IMoldHandler) cap;
                    if (canFill)
                    {
                        if (mold.isMolten())
                        {
                            // Use mold.getMetal() to avoid off by one errors during draining
                            Metal metal = mold.getMetal();
                            FluidStack fluidStack = mold.drain(ConfigTFC.Devices.CRUCIBLE.pouringSpeed, true);
                            if (fluidStack != null && fluidStack.amount > 0)
                            {
                                lastFillTimer = 5;
                                if (!ConfigTFC.Devices.CRUCIBLE.enableAllSlots)
                                {
                                    canFill = false;
                                }
                                alloy.add(metal, fluidStack.amount);
                                markForSync();
                            }
                        }
                    }
                }
            }
            if (lastFillTimer > 0)
            {
                lastFillTimer--;
            }

            // Output filling
            ItemStack outputStack = inventory.getStackInSlot(SLOT_OUTPUT);
            IItemHeat capOut = outputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (capOut instanceof IMoldHandler)
            {
                IMoldHandler mold = (IMoldHandler) capOut;

                // Check that the crucible metal is molten
                Metal alloyMetal = alloy.getResult();
                if (temperature > alloyMetal.getMeltTemp())
                {
                    // Fill from the current alloy
                    int amountToFill = alloy.removeAlloy(1, true);
                    if (amountToFill > 0)
                    {
                        // Do fill of the mold
                        Fluid metalFluid = FluidsTFC.getFluidFromMetal(alloyMetal);
                        FluidStack fluidStack = new FluidStack(metalFluid, amountToFill);
                        int amountFilled = mold.fill(fluidStack, true);

                        if (amountFilled > 0)
                        {
                            // Actually remove fluid from the alloy
                            alloy.removeAlloy(amountFilled, false);

                            // Set the output item to high temperature
                            capOut.setTemperature(temperature);
                            markForSync();
                        }
                    }
                }
            }
            if (needsClientUpdate)
            {
                // Update cached alloy result, since TOP is executed server side.
                alloyResult = alloy.getResult();
            }
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (!stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null))
        {
            return false;
        }
        if (slot != SLOT_OUTPUT)
        {
            IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (cap instanceof IMoldHandler)
            {
                if (cap instanceof ISmallVesselHandler)
                {
                    if (((ISmallVesselHandler) cap).getMetal() != null)
                    {
                        return true;
                    }
                    else
                    {
                        for (int i = 0; i < ((ISmallVesselHandler) cap).getSlots(); i++)
                        {
                            if (!((ISmallVesselHandler) cap).getStackInSlot(i).isEmpty())
                            {
                                return true;
                            }
                        }
                        return false; // This will make empty small vessels go to the output slot (same as below)
                    }
                }
                else
                {
                    return ((IMoldHandler) cap).getAmount() > 0; // This will make empty molds go to the output slot / prevent empty molds go to the input (no sense in heating them here anyway)
                }
            }
        }
        return slot != SLOT_OUTPUT || stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (slot != SLOT_OUTPUT)
        {
            cachedRecipes[slot] = HeatRecipe.get(inventory.getStackInSlot(slot));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        alloy.deserializeNBT(nbt.getCompoundTag("alloy"));
        temperature = nbt.getFloat("temp");

        // Voids surplus and set the maximum amount if config was changed
        alloy.setMaxAmount(ConfigTFC.Devices.CRUCIBLE.tank);

        // Also set the cached alloyResult:
        alloyResult = alloy.getResult();

        super.readFromNBT(nbt);

        // Update the recipe cache
        for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
        {
            cachedRecipes[i] = HeatRecipe.get(inventory.getStackInSlot(i));
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("alloy", alloy.serializeNBT());
        nbt.setFloat("temp", temperature);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
        {
            if (facing == EnumFacing.DOWN)
            {
                return (T) inventoryWrapperExtract;
            }
            else
            {
                return (T) inventoryWrapperInsert;
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        // Only carry to itemstack the alloy fluid
        super.onBreakBlock(world, pos, state);
        ItemStack stack = new ItemStack(BlocksTFC.CRUCIBLE);
        if (alloy.getAmount() > 0)
        {
            stack.setTagCompound(this.writeToItemTag());
        }
        Helpers.spawnItemStack(world, pos, stack);
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
            this.temperature = value;
            return;
        }
        TerraFirmaCraft.getLog().warn("Illegal field id {} in TECrucible#setField", index);
    }

    @Override
    public int getField(int index)
    {
        if (index == FIELD_TEMPERATURE)
        {
            return (int) temperature;
        }
        TerraFirmaCraft.getLog().warn("Illegal field id {} in TECrucible#getField", index);
        return 0;
    }

    public NBTTagCompound writeToItemTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("alloy", alloy.serializeNBT());
        return nbt;
    }

    public void readFromItemTag(NBTTagCompound nbt)
    {
        alloy.deserializeNBT(nbt.getCompoundTag("alloy"));

        // Voids surplus and set the maximum amount if config was changed
        alloy.setMaxAmount(ConfigTFC.Devices.CRUCIBLE.tank);

        // Also set the cached alloyResult:
        alloyResult = alloy.getResult();
    }

    /**
     * Used on SERVER to get the alloy contents
     *
     * @return the alloyForgeableHeatableHandler
     */
    @Nonnull
    public Alloy getAlloy()
    {
        return alloy;
    }

    /**
     * Used on CLIENT for quicker rendering - doesn't have to calculate the alloy every render tick
     *
     * @return the current result of getAlloy().getResult()
     */
    @Nonnull
    public Metal getAlloyResult()
    {
        return alloyResult;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (cap instanceof IMoldHandler)
        {
            // Molds will go into the output slot (automating filling molds should be possible)
            return side != EnumFacing.DOWN && slot == SLOT_OUTPUT;
        }
        return side != EnumFacing.DOWN && slot != SLOT_OUTPUT;
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        if (side == EnumFacing.DOWN && slot == SLOT_OUTPUT)
        {
            ItemStack stack = inventory.getStackInSlot(SLOT_OUTPUT);
            IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (cap != null)
            {
                // Only output if cap is full (so, only full molds will output from slot)
                return cap.drain(1, false) != null && cap.fill(cap.drain(1, false), false) <= 0;
            }
            return true;
        }
        return false;
    }
}
