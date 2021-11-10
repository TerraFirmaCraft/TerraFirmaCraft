/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.sync;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * An interface that marks capabilities that need to write data to the network.
 *
 * Share tag is fundamentally broken and/or useless:
 * - It needs to be overriden for items (which means it works for none of our event attached capabilities)
 * - In the cases where we could use it, it's not needed (because we just stick to using the stack tag which should ALWAYS be synced.)
 * - It's still ineffective in the case of the creative inventory.
 *
 * So: we ignore share tag completely. For items we control, we never override it or {@link Item#shouldOverrideMultiplayerNbt()} and the default behavior in vanilla (with Forge's patches) syncs the entire tag.
 * For external capabilities, we implement {@link ISyncable} on the capability, and add an injection that will always sync the content of network caps, from both S -> C, and in the case of the creative inventory, from C -> S.
 * Note that specific implementations, which rely on the stack nbt, instead will chose to no-op the write and read methods here because they rely on the stack tag itself.
 *
 * Finally, there is the issue of the creative inventory, which syncs C -> S. Forge has actually patched the call sites s.t. we can know when the creative inventory is trying to sync.
 *
 * @see FriendlyByteBuf#writeItem(ItemStack)
 * @see FriendlyByteBuf#readItem()
 */
public interface ISyncable
{
    @Nullable
    CompoundTag writeNetworkTag(ItemStack stack);

    void readNetworkTag(ItemStack stack, CompoundTag tag);

    /**
     * For serializable capabilities that should write their entire state to nbt.
     */
    interface Serializable extends ISyncable, INBTSerializable<CompoundTag>
    {
        @Nullable
        @Override
        default CompoundTag writeNetworkTag(ItemStack stack)
        {
            return serializeNBT();
        }

        @Override
        default void readNetworkTag(ItemStack stack, CompoundTag tag)
        {
            deserializeNBT(tag);
        }
    }
}
