package net.dries007.tfc.mixin.client;

import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * https://github.com/MinecraftForge/MinecraftForge/pull/8168
 */
@Mixin(FishingHookRenderer.class)
public abstract class FishingHookRendererMixin
{
    @Redirect(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean isAnyFishingRod(ItemStack stack, Item item)
    {
        return stack.getItem() instanceof FishingRodItem;
    }
}
