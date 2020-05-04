package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BarrelProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider, IBlockDisplayOverride
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_barrel_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        if (state.getBlock() instanceof BlockBarrel)
        {
            TEBarrel te = Helpers.getTE(world, iProbeHitData.getPos(), TEBarrel.class);
            if (te instanceof TEBarrel)
            {
                IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                FluidStack fluid = fluidHandler != null ? fluidHandler.drain(Integer.MAX_VALUE, false) : null;

                if (te.isSealed())
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.barrel.sealed", te.getSealedDate()).getFormattedText());
                    BarrelRecipe recipe = te.getRecipe();
                    if (recipe != null)
                    {

                        TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.barrel.recipe", recipe.getResultName()).getFormattedText());

                    }
                    else
                    {

                        TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.barrel.no_recipe").getFormattedText());
                    }
                }
                if (fluid != null && fluid.amount > 0)
                {
                    TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.barrel.contents", fluid.amount, fluid.getLocalizedName()).getFormattedText());
                }
            }

        }
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo iProbeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        TileEntity te = world.getTileEntity(iProbeHitData.getPos());
        if (te instanceof TEBarrel)
        {
            ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(stack)
                .vertical()
                .itemLabel(stack)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);

            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Void apply(ITheOneProbe iTheOneProbe)
    {
        probe = iTheOneProbe;
        probe.registerBlockDisplayOverride((mode, probeInfo, player, world, iBlockState, iProbeHitData) -> {
            TileEntity te = world.getTileEntity(iProbeHitData.getPos());
            if (te != null && te instanceof TEBarrel)
            {
                return overrideStandardInfo(mode, probeInfo, player, world, iBlockState, iProbeHitData);
            }
            return false;
        });

        iTheOneProbe.registerProvider(this);
        return null;
    }
}
