package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.climate.ClimateTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class RockProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_rock_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        if (state.getBlock() instanceof BlockRockVariant)
        {
            int temperature = Math.round(ClimateTFC.getActualTemp(world, iProbeHitData.getPos(), 0));
            TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.temperature", temperature).getFormattedText());
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
