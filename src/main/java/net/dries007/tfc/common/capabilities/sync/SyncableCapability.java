/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.sync;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;

public final class SyncableCapability
{
    public static final Capability<ISyncable> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private static final List<Capability<?>> SYNCING_CAPS = new ArrayList<>();

    static
    {
        SYNCING_CAPS.add(FoodCapability.CAPABILITY);
        SYNCING_CAPS.add(HeatCapability.CAPABILITY);
        SYNCING_CAPS.add(EggCapability.CAPABILITY);
    }

    public static boolean hasSyncableCapability(ItemStack stack)
    {
        return stack.getCapability(CAPABILITY).isPresent();
    }

    public static void writeNetworkTag(ItemStack stack, FriendlyByteBuf buffer)
    {
        CompoundTag tag = null;
        for (Capability<?> cap : SYNCING_CAPS)
        {
            CompoundTag newTag = stack.getCapability(cap)
                .resolve()
                .filter(c -> c instanceof ISyncable)
                .map(c -> ((ISyncable) c).writeNetworkTag(stack))
                .orElse(null);
            if (newTag != null)
            {
                if (tag == null)
                {
                    tag = new CompoundTag();
                }
                tag.put(cap.getName(), newTag);
            }
        }
        buffer.writeNbt(tag);
    }

    public static void readNetworkTag(ItemStack stack, FriendlyByteBuf buffer)
    {
        final CompoundTag tag = buffer.readNbt();
        if (tag != null)
        {
            for (Capability<?> cap : SYNCING_CAPS)
            {
                stack.getCapability(cap).ifPresent(c -> {
                    if (c instanceof ISyncable syncable && tag.contains(cap.getName()))
                    {
                        syncable.readNetworkTag(stack, tag.getCompound(cap.getName()));
                    }
                });
            }
        }
    }
}
