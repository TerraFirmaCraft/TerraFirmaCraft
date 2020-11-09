package net.dries007.tfc.mixin.client.multiplayer;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;

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
@Mixin(PlayerController.class)
public class PlayerControllerMixin
{
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOn(Lnet/minecraft/item/ItemUseContext;)Lnet/minecraft/util/ActionResultType;"), require = 2, cancellable = true)
    private void inject$useItemOn(ClientPlayerEntity player, ClientWorld worldIn, Hand handIn, BlockRayTraceResult resultIn, CallbackInfoReturnable<ActionResultType> cir)
    {
        final ItemStack stack = player.getItemInHand(handIn);
        final int startCount = stack.getCount();
        final ItemUseContext itemContext = new ItemUseContext(player, handIn, resultIn);
        InteractionManager.onItemUse(stack, itemContext).ifPresent(result -> {
            if (player.isCreative())
            {
                stack.setCount(startCount);
            }
            cir.setReturnValue(result);
        });
    }
}
