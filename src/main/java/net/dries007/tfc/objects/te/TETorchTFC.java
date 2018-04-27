/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.objects.blocks.BlockTorchTFC;

import static net.dries007.tfc.Constants.MOD_ID;

public class TETorchTFC extends TileEntity implements ITickable
{
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "torch");
    public static final long BURN_SECONDS = 600;
    private long secondsleft = BURN_SECONDS;

    @Override
    public void update()
    {
        if (this.world.getTotalWorldTime() % 20 == 0L)
        {
            this.updateTimer();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        secondsleft = compound.getLong("secondsleft");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setLong("secondsleft", secondsleft);
        return compound;
    }

    public void toggle()
    {
        IBlockState state = world.getBlockState(pos);
        boolean lit = state.getValue(BlockTorchTFC.LIT);
        if (lit)
        {
            world.setBlockState(pos, state.withProperty(BlockTorchTFC.LIT, false));
            secondsleft = 0;
        }
        else
        {
            world.setBlockState(pos, state.withProperty(BlockTorchTFC.LIT, true));
            secondsleft = BURN_SECONDS; // todo: adjust 600 seconds = 10 minutes
        }
        markDirty();
    }

    private void updateTimer()
    {
        if (secondsleft > 0)
        {
            secondsleft--;
            if (secondsleft == 0)
                toggle();
        }
    }
}
