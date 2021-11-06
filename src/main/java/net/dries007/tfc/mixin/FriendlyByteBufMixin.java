package net.dries007.tfc.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.sync.SyncableCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implement syncable item stack capabilities and fix issues with the creative menu
 *
 * @see net.dries007.tfc.common.capabilities.sync.ISyncable
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin
{
    @Inject(method = "writeItemStack", at = @At("RETURN"), remap = false)
    private void writeSyncableCapabilityData(ItemStack stack, boolean limitedTag, CallbackInfoReturnable<FriendlyByteBuf> cir)
    {
        if (!stack.isEmpty())
        {
            SyncableCapability.writeNetworkTag(stack, (FriendlyByteBuf) (Object) this);
        }
    }

    @Inject(method = "readItem", at = @At("RETURN"))
    private void readSyncableCapabilityData(CallbackInfoReturnable<ItemStack> cir)
    {
        final ItemStack stack = cir.getReturnValue();
        if (!stack.isEmpty())
        {
            SyncableCapability.readNetworkTag(stack, (FriendlyByteBuf) (Object) this);
        }
    }
}
