package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class TEFlowerPotTFC extends TEBase
{
    public IBlockState state;

    public TEFlowerPotTFC()
    {
        clear();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        state = NBTUtil.readBlockState(nbt.getCompoundTag("state"));
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (state != null)
            nbt.setTag("state", NBTUtil.writeBlockState(new NBTTagCompound(), state));
        return super.writeToNBT(nbt);
    }

    public void setState(IBlockState stateIn)
    {
        state = stateIn;
        markForSync();
    }

    public void dump()
    {
        if (hasWorld() && !world.isRemote)
        {
            Helpers.spawnItemStack(world, pos, new ItemStack(Item.getItemFromBlock(state.getBlock())));
            clear();
        }
    }

    public boolean isEmpty()
    {
        return state.getBlock() == Blocks.AIR;
    }

    private void clear()
    {
        state = Blocks.AIR.getDefaultState();
    }
}
