package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class OreProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_ore_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        if (state.getBlock() instanceof BlockOreTFC)
        {
            BlockOreTFC b = (BlockOreTFC) state.getBlock();
            Ore.Grade grade = Ore.Grade.valueOf(b.getMetaFromState(state));
            ItemStack stack = ItemOreTFC.get(b.ore, grade, 1);
            TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.ore_drop", new TextComponentTranslation(stack.getTranslationKey() + ".name").getFormattedText()).getFormattedText());
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
