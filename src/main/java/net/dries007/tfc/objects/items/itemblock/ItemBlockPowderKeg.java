/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.objects.blocks.BlockPowderKeg;

@ParametersAreNonnullByDefault
public class ItemBlockPowderKeg extends ItemBlockTFC implements IItemSize
{
    public ItemBlockPowderKeg(BlockPowderKeg block)
    {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null)
        {
            ItemStackHandler stackHandler = new ItemStackHandler();
            stackHandler.deserializeNBT(nbt.getCompoundTag("inventory"));
            int count = 0;
            int firstSlot = -1;
            for (int i = 0; i < stackHandler.getSlots(); i++)
            {
                if (firstSlot < 0 && !stackHandler.getStackInSlot(i).isEmpty())
                {
                    firstSlot = i;
                }
                count += stackHandler.getStackInSlot(i).getCount();
            }

            if (count == 0)
            {
                tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.powderkeg_empty"));
            }
            else
            {
                ItemStack itemStack = stackHandler.getStackInSlot(firstSlot);
                tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.powderkeg_amount", count, itemStack.getItem().getItemStackDisplayName(itemStack)));
            }
        }
    }
}
