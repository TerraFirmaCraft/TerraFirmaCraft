/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;

/**
 * This is a manager for capabilities that need to be synced externally, constantly.
 * Most capabilities (Vessel, Mold, Forging, etc.) directly interact with the stack tag, which is synced by vanilla and respects the creative inventory.
 * However, that relies on the capability to be able to detect changes and sync on change.
 * This is <strong>impossible</strong> with {@link HeatCapability} and {@link FoodCapability} as their serialization can change without intervention, so it cannot be saved on modification.
 * <p>
 * A note on why {@link ItemStack#getShareTag()} does not work for this purpose:
 * - It needs to be overriden for items (which means it works for none of our event attached capabilities)
 * - In the cases where we could use it, it's not needed (because we just stick to using the stack tag which should ALWAYS be synced.)
 * - It's still ineffective in the case of the creative inventory.
 *
 * So, in order to solve both problems (S -> C sync, and creative inventory C -> S sync):
 * - We patch the encode item stack to use this method, to sync non-stack-tag capabilities
 * - All other capabilities use the stack tag to avoid sync concerns.
 */
public final class ItemStackCapabilitySync
{
    public static boolean hasSyncableCapability(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).isPresent() || stack.getCapability(HeatCapability.CAPABILITY).isPresent();
    }

    public static void writeToNetwork(ItemStack stack, FriendlyByteBuf buffer)
    {
        writeToNetwork(FoodCapability.CAPABILITY, stack, buffer);
        writeToNetwork(HeatCapability.CAPABILITY, stack, buffer);
    }

    public static void readFromNetwork(ItemStack stack, FriendlyByteBuf buffer)
    {
        readFromNetwork(FoodCapability.CAPABILITY, stack, buffer);
        readFromNetwork(HeatCapability.CAPABILITY, stack, buffer);
    }

    @SuppressWarnings("NullableProblems")
    private static void writeToNetwork(Capability<? extends INBTSerializable<CompoundTag>> capability, ItemStack stack, FriendlyByteBuf buffer)
    {
        buffer.writeNbt(stack.getCapability(capability)
            .map(INBTSerializable::serializeNBT)
            .orElse(null));
    }

    private static void readFromNetwork(Capability<? extends INBTSerializable<CompoundTag>> capability, ItemStack stack, FriendlyByteBuf buffer)
    {
        final CompoundTag tag = buffer.readNbt();
        if (tag != null)
        {
            stack.getCapability(capability).ifPresent(cap -> cap.deserializeNBT(tag));
        }
    }
}
