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
 * <p>
 * So, in order to solve both problems (S -> C sync, and creative inventory C -> S sync):
 * - We patch the encode item stack to use this method, to sync non-stack-tag capabilities
 * - All other capabilities use the stack tag to avoid sync concerns.
 * <p>
 * Finally, in order to avoid issues caused by other mods due to incorrectly synced item stacks (see <a href="https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/2198">TerraFirmaCraft#2198</a>), we need to write and read this data in an as unconditional method as possible.
 * This means we cannot check for empty stacks, or those that do not have a capability. In the best case, we write an additional +1 bytes per item stack (a typical item stack has ~4-6 bytes default). This is about as least-cost that we can make it (in the worst case, we write 1 + two nbt tags).
 */
public final class ItemStackCapabilitySync
{
    public static boolean hasSyncableCapability(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).isPresent() || stack.getCapability(HeatCapability.CAPABILITY).isPresent();
    }

    public static void writeToNetwork(ItemStack stack, FriendlyByteBuf buffer)
    {
        // getCapability().resolve() might be called on an uninitialized stack here, which actually involves a mutation to the stack, as capabilities will be initialized for the first time
        // This initialization is not threadsafe, in general. In particular, during deserialization/serialization of food capabilities, food traits are mutated and iterated through a list.
        // While this is a weird construct, we should be able to synchronize on the item stack itself - this will prevent any stacks from having getCapability invoked and resolved by two threads at once. Different stacks should be fully safe to execute independently.
        synchronized (stack)
        {
            if (hasSyncableCapability(stack))
            {
                buffer.writeBoolean(true);
                writeToNetwork(FoodCapability.CAPABILITY, stack, buffer);
                writeToNetwork(HeatCapability.CAPABILITY, stack, buffer);
            }
            else
            {
                buffer.writeBoolean(false);
            }
        }
    }

    public static void readFromNetwork(ItemStack stack, FriendlyByteBuf buffer)
    {
        if (buffer.readBoolean())
        {
            readFromNetwork(FoodCapability.CAPABILITY, stack, buffer);
            readFromNetwork(HeatCapability.CAPABILITY, stack, buffer);
        }
    }

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
