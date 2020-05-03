/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

@WailaPlugin
public class OreProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockOreTFC)
        {
            BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
            return ItemOreTFC.get(b.ore, 1);
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockOreTFC)
        {
            BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
            Ore.Grade grade = Ore.Grade.valueOf(accessor.getMetadata());
            ItemStack stack = ItemOreTFC.get(b.ore, grade, 1);
            currentTooltip.add(new TextComponentTranslation("waila.tfc.ore_drop", new TextComponentTranslation(stack.getTranslationKey() + ".name").getFormattedText()).getFormattedText());
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerStackProvider(this, BlockOreTFC.class);
        //registrar.registerHeadProvider(this, BlockOreTFC.class);
        registrar.registerBodyProvider(this, BlockOreTFC.class);
    }
}
