package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.util.calendar.Month;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class FruitTreeProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_fruittree_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        if (state.getBlock() instanceof BlockFruitTreeLeaves)
        {
            TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.agriculture.harvesting_months").getFormattedText());
            BlockFruitTreeLeaves b = (BlockFruitTreeLeaves) state.getBlock();
            for (Month month : Month.values())
            {
                if (b.tree.isHarvestMonth(month))
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, TerraFirmaCraft.getProxy().getMonthName(month, true));
                }
            }
        }
    }


    @Nullable
    @Override
    public Void apply(ITheOneProbe iTheOneProbe)
    {
        iTheOneProbe.registerProvider(this);
        return null;
    }
}
