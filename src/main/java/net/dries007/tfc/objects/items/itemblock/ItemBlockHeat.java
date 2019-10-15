/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.heat.ItemHeatHandler;

public class ItemBlockHeat extends ItemBlockTFC
{
    private final float heatCapacity;
    private final float meltingPoint;

    public ItemBlockHeat(Block block, float heatCapacity, float meltingPoint)
    {
        super(block);

        this.heatCapacity = heatCapacity;
        this.meltingPoint = meltingPoint;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ItemHeatHandler(nbt, heatCapacity, meltingPoint);
    }
}
