/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;


@ParametersAreNonnullByDefault
public class TEPlacedHide extends TEBase
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
        positions |= 1 << (xPos + zPos * 4);
        markForBlockUpdate();
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
}
