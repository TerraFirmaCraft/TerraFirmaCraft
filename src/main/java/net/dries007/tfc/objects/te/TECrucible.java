/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.network.PacketCrucibleUpdate;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.Helpers;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class TECrucible extends TEInventory implements ITickable, ITileFields, IItemHandlerSidedCallback
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    public static final int FIELD_TEMPERATURE = 0;

    public static final int CRUCIBLE_MAX_METAL_FLUID = 3000; // = 30 Ingots worth

    private final Alloy alloy;
    private final IItemHandler inventoryWrapperExtract;
    private final IItemHandler inventoryWrapperInsert;

    private HeatRecipe cachedRecipe;
    private Metal alloyResult;
    private float temperature;
    private float targetTemperature;
    private int lastFillTimer;

    public TECrucible()
    {
        super(2);

        this.alloy = new Alloy(CRUCIBLE_MAX_METAL_FLUID);
        this.inventoryWrapperExtract = new ItemHandlerSidedWrapper(this, inventory, EnumFacing.DOWN);
        this.inventoryWrapperInsert = new ItemHandlerSidedWrapper(this, inventory, EnumFacing.UP);

        this.temperature = 0;
        this.lastFillTimer = 0;
        this.cachedRecipe = null;
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
        int overflow = Math.max(0, alloy.getAmount() + amount - CRUCIBLE_MAX_METAL_FLUID); // Amount which cannot be inserted
        alloy.add(metal, amount);

        //Update crucible temperature to match
        temperature = metal.getMeltTemp();
        targetTemperature = metal.getMeltTemp();

        TerraFirmaCraft.getNetwork().sendToAllTracking(new PacketCrucibleUpdate(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        return overflow;
    }

    @Override
    public void update()
    {
        if (world.isRemote) return;
        if (temperature < targetTemperature)
        {
            temperature += (float) ConfigTFC.GENERAL.temperatureModifierHeating;
        }
        else if (temperature > targetTemperature)
        {
            temperature -= (float) ConfigTFC.GENERAL.temperatureModifierHeating;
        }

        // Update target temperature
        if (targetTemperature > 0)
        {
            // Crucible target temperature decays constantly, since it is set by outside providers
            targetTemperature -= (float) ConfigTFC.GENERAL.temperatureModifierHeating;
        }

        // Input draining
        ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);
        IItemHeat cap = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);

        boolean needsClientUpdate = false;
        if (cap instanceof IMoldHandler)
        {
            // Try and drain fluid
            IMoldHandler mold = (IMoldHandler) cap;
            if (lastFillTimer <= 0)
            {
                if (mold.isMolten())
                {
                    // Use mold.getMetal() to avoid off by one errors during draining
                    Metal metal = mold.getMetal();
                    FluidStack fluidStack = mold.drain(1, true);
                    if (fluidStack != null && fluidStack.amount > 0)
                    {
                        alloy.add(metal, fluidStack.amount);
                        needsClientUpdate = true;
                    }
                }
                lastFillTimer = 5;
            }
            else
            {
                lastFillTimer--;
            }
            // Always heat up the item regardless if it is melting or not
            if (cap.getTemperature() < temperature)
            {
                CapabilityItemHeat.addTemp(cap);
            }

        }
        else if (cap != null && cachedRecipe != null)
        {
            if (cachedRecipe.isValidTemperature(cap.getTemperature()))
            {
                alloy.add(inputStack, cachedRecipe);
                inventory.setStackInSlot(SLOT_INPUT, cachedRecipe.getOutputStack(inputStack));
                needsClientUpdate = true;
            }
            else if (cap.getTemperature() < temperature)
            {
                CapabilityItemHeat.addTemp(cap);
            }
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
                        needsClientUpdate = true;
                    }
                }
            }
        }

        if (needsClientUpdate)
        {
            TerraFirmaCraft.getNetwork().sendToAllTracking(new PacketCrucibleUpdate(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
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
        return slot == SLOT_INPUT || stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);

        cachedRecipe = HeatRecipe.get(inventory.getStackInSlot(SLOT_INPUT));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        alloy.deserializeNBT(nbt.getCompoundTag("alloy"));
        temperature = nbt.getFloat("temp");

        // Also set the cached alloyResult:
        alloyResult = alloy.getResult();

        super.readFromNBT(nbt);

        // Update the recipe cache
        cachedRecipe = HeatRecipe.get(inventory.getStackInSlot(SLOT_INPUT));
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
        //Only carry to itemstack the alloy fluid
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

        // Also set the cached alloyResult:
        alloyResult = alloy.getResult();
    }

    /**
     * Used on SERVER to get the alloy contents
     *
     * @return the alloy
     */
    @Nonnull
    public Alloy getAlloy()
    {
        return alloy;
    }

    /**
     * Used on CLIENT to update the alloy contents
     * Also updates cached alloy result
     *
     * @param nbt the nbt from the packet
     */
    public void setAlloy(@Nonnull NBTTagCompound nbt)
    {
        alloy.deserializeNBT(nbt);
        alloyResult = alloy.getResult();
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
        return side != EnumFacing.DOWN;
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return side == EnumFacing.DOWN;
    }
}
