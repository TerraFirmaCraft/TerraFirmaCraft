/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.ItemStackCapabilitySync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implement syncable item stack capabilities and fix issues with the creative menu
 *
 * @see ItemStackCapabilitySync
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin
{
    @Inject(method = "writeItemStack", at = @At("RETURN"), remap = false)
    private void writeSyncableCapabilityData(ItemStack stack, boolean limitedTag, CallbackInfoReturnable<FriendlyByteBuf> cir)
    {
        if (!stack.isEmpty())
        {
            ItemStackCapabilitySync.writeToNetwork(stack, (FriendlyByteBuf) (Object) this);
        }
    }

    @Inject(method = "readItem", at = @At("RETURN"))
    private void readSyncableCapabilityData(CallbackInfoReturnable<ItemStack> cir)
    {
        final ItemStack stack = cir.getReturnValue();
        if (!stack.isEmpty())
        {
            ItemStackCapabilitySync.readFromNetwork(stack, (FriendlyByteBuf) (Object) this);
        }
    }
}
