/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.client.BarSystem;
import net.dries007.tfc.common.component.ItemStackHooks;
import net.dries007.tfc.common.component.forge.ForgingBonusComponent;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    /**
     * Modify the components attached to a stack on constructing, for time-dependent components.
     * @see ItemStackHooks#onModifyItemStackComponents
     */
    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("TAIL"))
    private void modifyItemStackOnConstructing(CallbackInfo ci)
    {
        ItemStackHooks.onModifyItemStackComponents((ItemStack) (Object) this);
    }

    /**
     * When copying components, do a check to see if the default prototype has been updated - if so, then replace it on the copy,
     * to ensure stacks created before we update default components don't leak the original prototype.
     * @see ItemStackHooks#onCopyItemStackComponents
     */
    @Redirect(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/PatchedDataComponentMap;copy()Lnet/minecraft/core/component/PatchedDataComponentMap;"))
    private PatchedDataComponentMap modifyItemCopyToUpdateComponents(PatchedDataComponentMap map)
    {
        return ItemStackHooks.onCopyItemStackComponents((ItemStack) (Object) this, map);
    }

    /**
     * Inject into the same spot where unbreaking enchantment is processed, in order to additionally apply forging bonus in the same respect
     */
    @WrapOperation(
        method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I")
    )
    private int applyForgingBonusToPreventItemDamage(ServerLevel level, ItemStack stack, int damage, Operation<Integer> original)
    {
        return ForgingBonusComponent.applyLikeUnbreaking(stack, level.random, original.call(level, stack, damage));
    }

    @Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
    private void inject$isBarVisible(CallbackInfoReturnable<Boolean> cir)
    {
        final ItemStack stack = ((ItemStack) (Object) this);
        final BarSystem.Bar bar = BarSystem.getCustomBar(stack);
        if (bar != null)
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBarColor", at = @At("TAIL"), cancellable = true)
    private void inject$getBarColor(CallbackInfoReturnable<Integer> cir)
    {
        final ItemStack stack = ((ItemStack) (Object) this);
        final BarSystem.Bar bar = BarSystem.getCustomBar(stack);
        if (bar != null)
        {
            cir.setReturnValue(bar.getBarColor(stack));
        }
    }

    @Inject(method = "getBarWidth", at = @At("TAIL"), cancellable = true)
    private void inject$getBarWidth(CallbackInfoReturnable<Integer> cir)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        final BarSystem.Bar bar = BarSystem.getCustomBar(stack);
        if (bar != null)
        {
            cir.setReturnValue(bar.getBarWidth(stack));
        }
    }
}
