/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.metal.ItemBloom;
import net.dries007.tfc.objects.items.metal.ItemIngot;

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
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output);
    }
}
