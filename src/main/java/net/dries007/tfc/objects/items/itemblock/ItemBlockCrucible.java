/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Alloy;

@ParametersAreNonnullByDefault
public class ItemBlockCrucible extends ItemBlockTFC
{
    public ItemBlockCrucible(Block block)
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
            Alloy alloy = new Alloy(ConfigTFC.Devices.CRUCIBLE.tank);
            alloy.deserializeNBT(nbt.getCompoundTag("alloy"));
            String metalName = (new TextComponentTranslation(alloy.getResult().getTranslationKey())).getFormattedText();
            tooltip.add(I18n.format(TerraFirmaCraft.MOD_ID + ".tooltip.crucible_alloy", alloy.getAmount(), metalName));
        }
    }
}
