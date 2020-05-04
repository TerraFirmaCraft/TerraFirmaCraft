package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlacedItemProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_placeditem_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {

        if (world.getTileEntity(iProbeHitData.getPos()) instanceof TEPlacedItemFlat)
        {
            TEPlacedItemFlat te = Helpers.getTE(world, iProbeHitData.getPos(), TEPlacedItemFlat.class);
            ItemStack stack = te.getStack();
            if (stack.getItem() instanceof ItemSmallOre)
            {
                ItemSmallOre nugget = (ItemSmallOre) stack.getItem();
                Ore ore = nugget.getOre();
                Metal metal = ore.getMetal();
                if (metal != null)
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.ore_drop", new TextComponentTranslation(metal.getTranslationKey()).getFormattedText()).getFormattedText());
                }
            }
            if (stack.getItem() instanceof ItemRock)
            {
                ItemRock pebble = (ItemRock) stack.getItem();
                Rock rock = pebble.getRock(stack);
                if (rock.isFluxStone())
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.flux_stone").getFormattedText());
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
