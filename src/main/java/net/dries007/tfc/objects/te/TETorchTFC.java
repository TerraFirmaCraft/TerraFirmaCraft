/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

public class TETorchTFC extends TileEntity
{
    private long lastLitTimestamp;

    public TETorchTFC()
    {
        lastLitTimestamp = CalendarTFC.PLAYER_TIME.getTicks();
    }

    public void onRandomTick()
    {
        if (CalendarTFC.PLAYER_TIME.getTicks() - lastLitTimestamp > (long) ConfigTFC.GENERAL.torchTime && ConfigTFC.GENERAL.torchTime > 0)
        {
            extinguish();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        lastLitTimestamp = compound.getLong("ticks");
        super.readFromNBT(compound);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setLong("ticks", lastLitTimestamp);
        return super.writeToNBT(compound);
    }

    public void extinguish()
    {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(LIT, false));
        this.markDirty();
    }

    public void light()
    {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(LIT, true));
        lastLitTimestamp = CalendarTFC.PLAYER_TIME.getTicks();
        this.markDirty();
    }
}
