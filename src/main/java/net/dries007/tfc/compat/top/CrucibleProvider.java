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
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.te.TECrucible;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CrucibleProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_crucible_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        TileEntity te = world.getTileEntity(iProbeHitData.getPos());
        if (te instanceof TECrucible)
        {
            TECrucible crucible = Helpers.getTE(world, iProbeHitData.getPos(), TECrucible.class);
            if (crucible.getAlloy().getAmount() > 0)
            {
                Metal metal = crucible.getAlloyResult();
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.metal.output", crucible.getAlloy().getAmount(), new TextComponentTranslation(metal.getTranslationKey()).getFormattedText()).getFormattedText());
            }
            int temperature = crucible.getField(TECrucible.FIELD_TEMPERATURE);
            String heatTooltip = Heat.getTooltip(temperature);
            if (heatTooltip != null)
            {
                TOPPlugin.outputHorizontalText(iProbeInfo, heatTooltip);
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
