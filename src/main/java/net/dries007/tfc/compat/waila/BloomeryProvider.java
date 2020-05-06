/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.objects.blocks.devices.BlockBloomery;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TEBloom;
import net.dries007.tfc.objects.te.TEBloomery;

@WailaPlugin
public class BloomeryProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TEBloomery)
        {
            TEBloomery bloomery = (TEBloomery) accessor.getTileEntity();
            IBlockState state = accessor.getBlockState();
            if (state.getValue(ILightableBlock.LIT))
            {
                List<ItemStack> oreStacks = bloomery.getOreStacks();
                BloomeryRecipe recipe = oreStacks.size() > 0 ? BloomeryRecipe.get(oreStacks.get(0)) : null;
                long remainingMinutes = Math.round(bloomery.getReaminingTicks() / 1200.0f);
                currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.remaining", remainingMinutes).getFormattedText());
                if (recipe != null)
                {
                    ItemStack output = recipe.getOutput(oreStacks);
                    IForgeable cap = output.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                    if (cap instanceof IForgeableMeasurableMetal)
                    {
                        IForgeableMeasurableMetal forgeCap = ((IForgeableMeasurableMetal) cap);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.output", forgeCap.getMetalAmount(), new TextComponentTranslation(forgeCap.getMetal().getTranslationKey()).getFormattedText()).getFormattedText());
                    }
                }
            }
            else
            {
                int ores = bloomery.getOreStacks().size();
                int fuel = bloomery.getFuelStacks().size();
                int max = BlockBloomery.getChimneyLevels(accessor.getWorld(), bloomery.getInternalBlock()) * 8;
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.ores", ores, max).getFormattedText());
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.fuel", fuel, max).getFormattedText());
            }
        }
        else if (accessor.getTileEntity() instanceof TEBloom)
        {
            TEBloom bloom = (TEBloom) accessor.getTileEntity();
            IItemHandler cap = bloom.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (cap != null)
            {
                ItemStack bloomStack = cap.getStackInSlot(0);
                IForgeable forgeCap = bloomStack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                if (forgeCap instanceof IForgeableMeasurableMetal)
                {
                    IForgeableMeasurableMetal bloomCap = ((IForgeableMeasurableMetal) forgeCap);
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.metal.output", bloomCap.getMetalAmount(), new TextComponentTranslation(bloomCap.getMetal().getTranslationKey()).getFormattedText()).getFormattedText());
                }
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return te.writeToNBT(tag);
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(this, TEBloomery.class);
        registrar.registerBodyProvider(this, TEBloom.class);

        registrar.registerNBTProvider(this, TEBloomery.class);
        registrar.registerNBTProvider(this, TEBloom.class);
    }
}
