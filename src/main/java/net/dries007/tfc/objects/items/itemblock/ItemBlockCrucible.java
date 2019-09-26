/*
 *  Work under Copyright. Licensed under the EUPL.
 *  See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import java.util.List;
import javax.annotation.Nonnull;
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

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.te.TECrucible;
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
            Alloy alloy = new Alloy(TECrucible.CRUCIBLE_MAX_METAL_FLUID);
            alloy.deserializeNBT(nbt.getCompoundTag("alloy"));
            String metalName = (new TextComponentTranslation(alloy.getResult().getTranslationKey())).getFormattedText();
            tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.crucible_alloy", alloy.getAmount(), metalName));
        }
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.HUGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
