package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.objects.blocks.devices.BlockBlastFurnace;
import net.dries007.tfc.objects.te.TEBlastFurnace;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BlastFurnaceProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_blastfurnace_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        TileEntity te = world.getTileEntity(iProbeHitData.getPos());
        if (state.getBlock() instanceof BlockBlastFurnace && te instanceof TEBlastFurnace)
        {
            TEBlastFurnace blastFurnace = Helpers.getTE(world, iProbeHitData.getPos(), TEBlastFurnace.class);
            int chimney = BlockBlastFurnace.getChimneyLevels(world, iProbeHitData.getPos());
            if (chimney > 0)
            {
                int maxItems = chimney * 4;
                int oreStacks = blastFurnace.getOreStacks().size();
                int fuelStacks = blastFurnace.getFuelStacks().size();
                int temperature = blastFurnace.getField(TEBlastFurnace.FIELD_TEMPERATURE);
                String heatTooltip = Heat.getTooltip(temperature);
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.bloomery.ores", oreStacks, maxItems).getFormattedText());
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.bloomery.fuel", fuelStacks, maxItems).getFormattedText());
                if (heatTooltip != null)
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, heatTooltip);
                }
            }
            else
            {
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.blast_furnace.not_formed").getFormattedText());
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
