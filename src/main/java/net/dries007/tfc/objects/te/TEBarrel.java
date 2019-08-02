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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.network.PacketBarrelUpdate;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.fluids.capability.FluidHandlerSided;
import net.dries007.tfc.objects.fluids.capability.FluidTankCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidHandlerSidedCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidTankCallback;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.util.FluidTransferHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarFormatted;

@ParametersAreNonnullByDefault
public class TEBarrel extends TEInventory implements ITickable, IItemHandlerSidedCallback, IFluidHandlerSidedCallback, IFluidTankCallback
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    public static final int TANK_CAPACITY = 10000;
    public static final int BARREL_MAX_FLUID_TEMPERATURE = 500;

    private FluidTank tank = new FluidTankCallback(this, 0, TANK_CAPACITY);
    private boolean sealed;
    private long sealedTick, sealedCalendarTick;
    private BarrelRecipe recipe;
    private int tickCounter;
    private boolean checkInstantRecipe = false;

    public TEBarrel()
    {
        super(3);
    }

    /**
     * Called when this TileEntity was created by placing a sealed Barrel Item.
     * Loads its data from the Item's NBTTagCompound without loading xyz coordinates.
     *
     * @param nbt The NBTTagCompound to load from.
     */
    public void readFromItemTag(NBTTagCompound nbt)
    {
        tank.readFromNBT(nbt.getCompoundTag("tank"));
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"));

        sealedTick = nbt.getLong("sealedTick");
        sealedCalendarTick = nbt.getLong("sealedCalendarTick");

        this.markDirty();
    }

    /**
     * Called to get the NBTTagCompound that is put on Barrel Items.
     * This happens when a sealed Barrel was broken.
     *
     * @return An NBTTagCompound containing inventory and tank data.
     */
    public NBTTagCompound getItemTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        nbt.setTag("inventory", inventory.serializeNBT());

        nbt.setLong("sealedTick", sealedTick);
        nbt.setLong("sealedCalendarTick", sealedCalendarTick);

        return nbt;
    }

    /**
     * Called once per side when the TileEntity has finished loading.
     * On servers, this is the earliest point in time to safely access the TE's World object.
     */
    @Override
    public void onLoad()
    {
        if (!world.isRemote)
        {
            updateLockStatus();
        }
    }

    public BarrelRecipe getRecipe()
    {
        return recipe;
    }

    public String getSealedDate()
    {
        return ICalendarFormatted.getTimeAndDate(sealedCalendarTick, CalendarTFC.INSTANCE.getDaysInMonth());
    }

    @Override
    public void setAndUpdateFluidTank(int fluidTankID)
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    /**
     * Retrieves the packet to send to clients whenever this TileEntity is updated via World.notifyBlockUpdate.
     * We are using this method to update the lock status on our ItemHandler and FluidHandler, since a Block update occurred.
     * This method is only called server-side.
     *
     * @return The Packet that will be sent to clients in range.
     */
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        updateLockStatus();
        return super.getUpdatePacket();
    }

    /**
     * Called on clients whenever this TileEntity received an update from the server.
     **/
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        updateLockStatus();
    }

    /**
     * Called on clients when this TileEntity received an update from the server on load.
     *
     * @param tag An NBTTagCompound containing the TE's data.
     */
    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
        updateLockStatus();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        return !sealed && (isItemValid(slot, stack) || side == null && slot == SLOT_FLUID_CONTAINER_OUT);
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return !sealed && (side == null || slot != SLOT_FLUID_CONTAINER_IN);
    }

    @Override
    public boolean canFill(FluidStack resource, EnumFacing side)
    {
        return !sealed && (resource.getFluid() == null || resource.getFluid().getTemperature(resource) < BARREL_MAX_FLUID_TEMPERATURE);
    }

    @Override
    public boolean canDrain(EnumFacing side)
    {
        return !sealed;
    }

    public void onSealed()
    {
        sealedTick = CalendarTFC.TOTAL_TIME.getTicks();
        sealedCalendarTick = CalendarTFC.CALENDAR_TIME.getTicks();
        recipe = BarrelRecipe.get(inventory.getStackInSlot(SLOT_ITEM), tank.getFluid());
        TerraFirmaCraft.getNetwork().sendToDimension(new PacketBarrelUpdate(this, recipe, sealedCalendarTick), world.provider.getDimension());
    }

    public void onReceivePacket(@Nullable BarrelRecipe recipe, long sealedCalendarTick)
    {
        this.recipe = recipe;
        this.sealedCalendarTick = sealedCalendarTick;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            tickCounter++;

            if (tickCounter == 10)
            {
                tickCounter = 0;

                ItemStack fluidContainerIn = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
                FluidActionResult result = FluidTransferHelper.emptyContainerIntoTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, TANK_CAPACITY, world, pos);

                if (!result.isSuccess())
                {
                    result = FluidTransferHelper.fillContainerFromTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, TANK_CAPACITY, world, pos);
                }

                if (result.isSuccess())
                {
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, result.getResult());
                }

                Fluid freshWater = FluidRegistry.getFluid("fresh_water");

                if (!sealed && world.isRainingAt(pos.up()) && (tank.getFluid() == null || tank.getFluid().getFluid() == freshWater))
                {
                    tank.fill(new FluidStack(freshWater, 10), true);
                }
            }

            // Check if recipe is complete
            if (recipe != null)
            {
                int durationSealed = (int) (CalendarTFC.TOTAL_TIME.getTicks() - sealedTick);
                if (durationSealed > recipe.getDuration())
                {
                    ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM);
                    FluidStack inputFluid = tank.getFluid();
                    if (recipe.isValidInput(inputFluid, inputStack))
                    {
                        tank.setFluid(recipe.getOutputFluid(inputFluid, inputStack));
                        inventory.setStackInSlot(SLOT_ITEM, recipe.getOutputItem(inputFluid, inputStack));

                        IBlockState state = world.getBlockState(pos);
                        world.notifyBlockUpdate(pos, state, state, 3);
                    }
                    recipe = null;
                }
            }

            if (checkInstantRecipe)
            {
                ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM);
                FluidStack inputFluid = tank.getFluid();
                BarrelRecipe instantRecipe = BarrelRecipe.getInstant(inputStack, inputFluid);
                if (instantRecipe != null)
                {
                    tank.setFluid(instantRecipe.getOutputFluid(inputFluid, inputStack));
                    inventory.setStackInSlot(SLOT_ITEM, instantRecipe.getOutputItem(inputFluid, inputStack));
                    instantRecipe.onRecipeComplete(world, pos);

                    IBlockState state = world.getBlockState(pos);
                    world.notifyBlockUpdate(pos, state, state, 3);
                }
                else checkInstantRecipe = false;
            }
        }
    }

    public boolean isSealed()
    {
        return sealed;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        checkInstantRecipe = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        tank.readFromNBT(nbt.getCompoundTag("tank"));
        sealedTick = nbt.getLong("sealedTick");
        sealedCalendarTick = nbt.getLong("sealedCalendarTick");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        nbt.setLong("sealedTick", sealedTick);
        nbt.setLong("sealedCalendarTick", sealedCalendarTick);

        return super.writeToNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) new ItemHandlerSidedWrapper(this, inventory, facing);
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) new FluidHandlerSided(this, tank, facing);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FLUID_CONTAINER_IN:
                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            case SLOT_ITEM:
                IItemSize sizeCap = CapabilityItemSize.getIItemSize(stack);
                if (sizeCap != null)
                {
                    return sizeCap.getSize(stack).isSmallerThan(Size.VERY_LARGE);
                }
                return true;
            default:
                return false;
        }
    }

    private void updateLockStatus()
    {
        sealed = world.getBlockState(pos).getValue(BlockBarrel.SEALED);
    }
}
