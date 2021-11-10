/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class SyncableCapability
{
    public static final Capability<ISyncable> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static boolean hasSyncableCapability(ItemStack stack)
    {
        return stack.getCapability(CAPABILITY).isPresent();
    }

    public static void writeNetworkTag(ItemStack stack, FriendlyByteBuf buffer)
    {
        buffer.writeNbt(stack.getCapability(CAPABILITY)
            .resolve()
            .map(c -> c.writeNetworkTag(stack))
            .orElse(null));
    }

    public static void readNetworkTag(ItemStack stack, FriendlyByteBuf buffer)
    {
        final CompoundTag tag = buffer.readNbt();
        if (tag != null)
        {
            stack.getCapability(CAPABILITY).ifPresent(c -> c.readNetworkTag(stack, tag));
        }
    }
}
