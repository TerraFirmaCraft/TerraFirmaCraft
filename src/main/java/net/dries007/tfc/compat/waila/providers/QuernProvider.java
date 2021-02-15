package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.devices.BlockQuern;
import net.dries007.tfc.objects.te.TEQuern;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.te.TEQuern.SLOT_HANDSTONE;

public class QuernProvider implements IWailaBlock
{

    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>(1);
        TEQuern quern = Helpers.getTE(world, pos, TEQuern.class);
        IItemHandler handler;
        ItemStack handstone;
        if (quern != null && quern.hasHandstone() && (handler = quern.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) != null && !(handstone = handler.getStackInSlot(SLOT_HANDSTONE)).isEmpty())
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.quern.handstone_durability", handstone.getItemDamage(), handstone.getMaxDamage()).getFormattedText());
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(BlockQuern.class);
    }

}
