/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.ItemStackCapabilitySync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin
{
    @Redirect(method = "triggerSlotListeners", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;equals(Lnet/minecraft/world/item/ItemStack;Z)Z", remap = false))
    private boolean hasClientStackChangedIncludingSyncableCapabilities(ItemStack current, ItemStack previous, boolean limitTags)
    {
        // This mimics the behavior of ItemStack#equals, and so a 'false' return value means 'we need to sync this anyway'
        // We add an additional check here in order for these two item stacks to be equal enough to not sync to client.
        return !ItemStackCapabilitySync.hasSyncableCapability(current) && current.equals(previous, limitTags);
    }
}
