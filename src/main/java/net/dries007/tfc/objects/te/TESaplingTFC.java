/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.world.classic.CalendarTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TESaplingTFC extends TileEntity
{
    private long timer;

    public TESaplingTFC()
    {
        super();
    }

    public long getHoursSincePlaced()
    {
        return (CalendarTFC.getTotalTime() - timer) / CalendarTFC.TICKS_IN_HOUR;
    }

    public void onPlaced()
    {
        timer = CalendarTFC.getTotalTime();
        this.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        timer = tag.getLong("timer");
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setLong("timer", timer);
        return super.writeToNBT(tag);
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        if (world != null)
        {
            return new SPacketUpdateTileEntity(this.getPos(), 0, this.writeToNBT(new NBTTagCompound()));
        }

        return null;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        this.handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }
}
