/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import net.dries007.tfc.TerraFirmaCraft;

/**
 * todo: when blast furnace gets merged, make this subclass TEBase
 */
@ParametersAreNonnullByDefault
public class TEPlacedHide extends TileEntity
{
    private short positions; // essentially a boolean[16]

    public TEPlacedHide()
    {
        positions = 0;
    }

    public boolean isComplete()
    {
        return positions == -1;
    }

    public short getScrapedPositions()
    {
        return positions;
    }

    public void onClicked(float hitX, float hitZ)
    {
        // This needs to change on both client and server
        int xPos = (int) (hitX * 4);
        int zPos = (int) (hitZ * 4);
        TerraFirmaCraft.getLog().info("Positions: {} {} {} {}", hitX, hitZ, xPos, zPos);
        positions |= 1 << (xPos + zPos * 4);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        positions = nbt.getShort("positions");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setShort("positions", positions);
        return super.writeToNBT(nbt);
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        // Needed to sync TE data on block update
        // It only needs the TE-specific data
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 1, nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag()
    {
        // Needed to sync TE data on chunk load
        // This needs to write the TE data (i.e. x, y, z), as well as whatever TE-specific NBT is required
        NBTTagCompound nbt = super.getUpdateTag();
        return writeToNBT(nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        // Needed to sync TE data on chunk load
        NBTTagCompound nbt = pkt.getNbtCompound();
        readFromNBT(nbt);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt)
    {
        // Needed to sync TE data on chunk load
        readFromNBT(nbt);
    }
}
