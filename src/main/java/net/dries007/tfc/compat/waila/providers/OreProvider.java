/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

public class OreProvider implements IWailaBlock
{

    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockOreTFC)
        {
            BlockOreTFC b = (BlockOreTFC) state.getBlock();
            Ore.Grade grade = state.getValue(BlockOreTFC.GRADE);
            ItemStack stack = ItemOreTFC.get(b.ore, grade, 1);
            currentTooltip.add(new TextComponentTranslation("waila.tfc.ore_drop", new TextComponentTranslation(stack.getTranslationKey() + ".name").getFormattedText()).getFormattedText());
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public ItemStack getIcon(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockOreTFC)
        {
            BlockOreTFC b = (BlockOreTFC) state.getBlock();
            return ItemOreTFC.get(b.ore, 1);
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(BlockOreTFC.class);
    }
}
