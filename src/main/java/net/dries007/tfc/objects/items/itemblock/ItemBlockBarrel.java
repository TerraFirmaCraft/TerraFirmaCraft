/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.te.TEBarrel;

@ParametersAreNonnullByDefault
public class ItemBlockBarrel extends ItemBlockTFC
{
    public ItemBlockBarrel(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    @Nonnull
    public String getTranslationKey(@Nonnull ItemStack stack)
    {
        if (stack.getMetadata() == 1)
        {
            return super.getTranslationKey() + ".sealed";
        }

        return super.getTranslationKey();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getMetadata() == 1)
        {
            NBTTagCompound compound = stack.getTagCompound();

            if (compound != null)
            {
                FluidTank tank = new FluidTank(0).readFromNBT(compound.getCompoundTag("tank"));
                ItemStackHandler stackHandler = new ItemStackHandler();
                stackHandler.deserializeNBT(compound.getCompoundTag("inventory"));
                ItemStack inventory = stackHandler.getStackInSlot(TEBarrel.SLOT_ITEM);

                if (tank.getFluid() == null || tank.getFluidAmount() == 0)
                {
                    if (inventory.isEmpty())
                    {
                        tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_empty"));
                    }
                    else
                    {
                        tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_item", inventory.getCount(), inventory.getItem().getItemStackDisplayName(inventory)));
                    }
                }
                else
                {
                    tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_fluid", tank.getFluidAmount(), tank.getFluid().getLocalizedName()));

                    if (!inventory.isEmpty())
                    {
                        tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_item_in_fluid", inventory.getCount(), inventory.getItem().getItemStackDisplayName(inventory)));
                    }
                }
            }
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

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return stack.getMetadata() == 0;
    }
}
