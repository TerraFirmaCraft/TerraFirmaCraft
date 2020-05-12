/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.devices.BlockBloomery;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TEBloom;
import net.dries007.tfc.objects.te.TEBloomery;
import net.dries007.tfc.util.calendar.ICalendar;

public class BloomeryProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TEBloomery)
        {
            TEBloomery bloomery = (TEBloomery) tileEntity;
            if (state.getValue(ILightableBlock.LIT))
            {
                List<ItemStack> oreStacks = bloomery.getOreStacks();
                BloomeryRecipe recipe = oreStacks.size() > 0 ? BloomeryRecipe.get(oreStacks.get(0)) : null;
                long remainingTicks = bloomery.getRemainingTicks();
                switch (ConfigTFC.Client.TOOLTIP.timeTooltipMode)
                {
                    case NONE:
                        break;
                    case TICKS:
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.ticks_remaining", remainingTicks).getFormattedText());
                        break;
                    case MINECRAFT_HOURS:
                        long remainingHours = Math.round(remainingTicks / (float) ICalendar.TICKS_IN_HOUR);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.hours_remaining", remainingHours).getFormattedText());
                        break;
                    case REAL_MINUTES:
                        long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.minutes_remaining", remainingMinutes).getFormattedText());
                        break;
                }
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
                int max = BlockBloomery.getChimneyLevels(world, bloomery.getInternalBlock()) * 8;
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.ores", ores, max).getFormattedText());
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.fuel", fuel, max).getFormattedText());
            }
        }
        else if (tileEntity instanceof TEBloom)
        {
            TEBloom bloom = (TEBloom) tileEntity;
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
    public List<Class<?>> getLookupClass()
    {
        return ImmutableList.of(TEBloom.class, TEBloomery.class);
    }
}
