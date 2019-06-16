/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.dries007.tfc.util.Helpers;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ItemBloom;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TEBloom extends TEBase
{
    private int count;

    public TEBloom()
    {
        count = 100;
    }

    public void setCount(int count){ this.count = count; }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        count = tag.getInteger("count");
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("count", count);
        return super.writeToNBT(tag);
    }

    public void onBreakBlock()
    {
        ItemStack output = new ItemStack(ItemsTFC.UNREFINED_BLOOM, 1);
        ItemBloom.setSmeltAmount(output, count);
        TEBloomery te = null;
        BlockPos bloomeryPos = pos;
        for (int i = 0; i < 4 && te == null; i++)
            te = Helpers.getTE(world, pos.offset(EnumFacing.HORIZONTALS[i]), TEBloomery.class);
        //This statement must always be true
        if(te!=null)
            bloomeryPos = te.getPos();
        InventoryHelper.spawnItemStack(world, bloomeryPos.getX(), bloomeryPos.getY(), bloomeryPos.getZ(), output);
    }
}
