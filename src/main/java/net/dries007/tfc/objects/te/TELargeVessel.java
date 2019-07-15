/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.network.PacketLargeVesselUpdate;
import net.dries007.tfc.objects.blocks.BlockLargeVessel;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarFormatted;

@ParametersAreNonnullByDefault
public class TELargeVessel extends TEInventory implements IItemHandlerSidedCallback
{
    public static final float LARGE_VESSEL_PRESERVATION_FACTOR = 2.0f;

    private boolean sealed;
    private long sealedTick, sealedCalendarTick;

    public TELargeVessel()
    {
        super(9);
    }

    /**
     * Called when this TileEntity was created by placing a sealed Barrel Item.
     * Loads its data from the Item's NBTTagCompound without loading xyz coordinates.
     *
     * @param nbt The NBTTagCompound to load from.
     */
    public void readFromItemTag(NBTTagCompound nbt)
    {
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

    public String getSealedDate()
    {
        return ICalendarFormatted.getTimeAndDate(sealedCalendarTick, CalendarTFC.INSTANCE.getDaysInMonth());
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

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        return !sealed && isItemValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return !sealed;
    }

    public void onSealed()
    {
        sealedTick = CalendarTFC.TOTAL_TIME.getTicks();
        sealedCalendarTick = CalendarTFC.CALENDAR_TIME.getTicks();
        TerraFirmaCraft.getNetwork().sendToDimension(new PacketLargeVesselUpdate(this, sealedCalendarTick), world.provider.getDimension());
    }

    public void onSolidSeal()
    {
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
                if (cap != null)
                {
                    if (!cap.isRotten())
                    {
                        cap.setPreservationFactor(LARGE_VESSEL_PRESERVATION_FACTOR);
                    }
                }
            }
        }
    }

    public void onSolidUnseal()
    {
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
                if (cap != null)
                {
                    if (!cap.isRotten())
                    {
                        cap.setPreservationFactor(1.0f);
                    }
                }
            }
        }
    }

    public void onReceivePacket(long sealedCalendarTick)
    {
        this.sealedCalendarTick = sealedCalendarTick;
    }

    public boolean isSealed()
    {
        return sealed;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {

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
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        sealedTick = nbt.getLong("sealedTick");
        sealedCalendarTick = nbt.getLong("sealedCalendarTick");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
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

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        IItemSize sizeCap = CapabilityItemSize.getIItemSize(stack);
        if (sizeCap != null)
        {
            if (sizeCap.getSize(stack) != Size.HUGE)
            {
                return true;
            }
            return false;
        }
        return true;
    }

    private void updateLockStatus()
    {
        sealed = world.getBlockState(pos).getValue(BlockLargeVessel.SEALED);
    }
}
