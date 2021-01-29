/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.server.management;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import net.dries007.tfc.util.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This is a fake event injection as Forge's RightClickBlock does not fire in the correct place
 *
 * @see InteractionManager
 */
@Mixin(PlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin
{
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOn(Lnet/minecraft/item/ItemUseContext;)Lnet/minecraft/util/ActionResultType;"), cancellable = true, require = 2)
    private void inject$useItemOn(ServerPlayerEntity playerIn, World worldIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult blockRaytraceResultIn, CallbackInfoReturnable<ActionResultType> cir)
    {
        final int startCount = stackIn.getCount();
        final ItemUseContext itemContext = new ItemUseContext(playerIn, handIn, blockRaytraceResultIn);
        InteractionManager.onItemUse(stackIn, itemContext).ifPresent(result -> {
            if (playerIn.isCreative())
            {
                stackIn.setCount(startCount);
            }
            if (result.consumesAction())
            {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(playerIn, blockRaytraceResultIn.getBlockPos(), stackIn);
            }
            cir.setReturnValue(result);
        });
    }
}
