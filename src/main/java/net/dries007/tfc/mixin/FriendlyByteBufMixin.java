/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.dries007.tfc.common.capabilities.ItemStackCapabilitySync;

/**
 * Implement syncable item stack capabilities and fix issues with the creative menu
 *
 * @see ItemStackCapabilitySync
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin
{
    @Redirect(
        method = "writeItemStack",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/FriendlyByteBuf;"),
        remap = false
    )
    private FriendlyByteBuf writeSyncableCapabilityData(FriendlyByteBuf buffer, CompoundTag tag, ItemStack stack)
    {
        return buffer.writeNbt(ItemStackCapabilitySync.writeToNetwork(stack, tag));
    }

    @Redirect(
        method = "readItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;readShareTag(Lnet/minecraft/nbt/CompoundTag;)V", remap = false)
    )
    private void readSyncableCapabilityData(ItemStack stack, CompoundTag tag)
    {
        stack.readShareTag(ItemStackCapabilitySync.readFromNetwork(stack, tag));
    }
}
