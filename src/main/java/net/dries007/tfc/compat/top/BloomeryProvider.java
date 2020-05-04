package net.dries007.tfc.compat.top;


import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.objects.blocks.devices.BlockBloomery;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TEBloom;
import net.dries007.tfc.objects.te.TEBloomery;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_bloomery_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        TileEntity te = world.getTileEntity(iProbeHitData.getPos());
        if (state.getBlock() instanceof BlockBloomery)
        {
            TEBloomery bloomery = Helpers.getTE(world, iProbeHitData.getPos(), TEBloomery.class);
            if (state.getValue(ILightableBlock.LIT))
            {
                List<ItemStack> oreStacks = bloomery.getOreStacks();
                BloomeryRecipe recipe = oreStacks.size() > 0 ? BloomeryRecipe.get(oreStacks.get(0)) : null;
                long remainingMinutes = Math.round(bloomery.getBurnTicksLeft() / 1200.0f);
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.devices.remaining", remainingMinutes).getFormattedText());
                if (recipe != null)
                {
                    ItemStack output = recipe.getOutput(oreStacks);
                    IForgeable cap = output.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                    if (cap instanceof IForgeableMeasurableMetal)
                    {
                        IForgeableMeasurableMetal forgeCap = ((IForgeableMeasurableMetal) cap);
                        TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.bloomery.output", forgeCap.getMetalAmount(), new TextComponentTranslation(forgeCap.getMetal().getTranslationKey()).getFormattedText()).getFormattedText());
                    }
                }
            }
            else
            {
                int ores = bloomery.getOreStacks().size();
                int fuel = bloomery.getFuelStacks().size();
                int max = BlockBloomery.getChimneyLevels(world, bloomery.getInternalBlock()) * 8;
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.bloomery.ores", ores, max).getFormattedText());
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.bloomery.fuel", fuel, max).getFormattedText());
            }
        }
        else if (te instanceof TEBloom)
        {
            TEBloom bloom = Helpers.getTE(world, iProbeHitData.getPos(), TEBloom.class);
            IItemHandler cap = bloom.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (cap != null)
            {
                ItemStack bloomStack = cap.getStackInSlot(0);
                IForgeable forgeCap = bloomStack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                if (forgeCap instanceof IForgeableMeasurableMetal)
                {
                    IForgeableMeasurableMetal bloomCap = ((IForgeableMeasurableMetal) forgeCap);
                    TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.metal.output", bloomCap.getMetalAmount(), new TextComponentTranslation(bloomCap.getMetal().getTranslationKey()).getFormattedText()).getFormattedText());
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
