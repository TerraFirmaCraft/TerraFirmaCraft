/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.inventory.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Change the default container sync behavior to sync *all* changes, including those in capabilities.
 * This is the most performant and simple way this can be done without a forge PR, although there have been many.
 *
 * todo: is there a forge PR at long last?
 * todo: only sync heat cap changes w/ a dirty flag? or specific caps? because forcing everything to sync is not the best solution
 */
@Mixin(Container.class)
public abstract class ContainerMixin
{
    @Redirect(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;equals(Lnet/minecraft/item/ItemStack;Z)Z", remap = false))
    private boolean redirect$broadcastChanges$equals(ItemStack itemStack, ItemStack other, boolean limitTags)
    {
        return itemStack.equals(other, false);
    }
}
