/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.world.item.crafting.Recipe;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.NetworkRecipeParityCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundUpdateRecipesPacket.class)
public abstract class ClientboundUpdateRecipesPacketMixin
{
    @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
    private static void beforeFromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Recipe<?>> cir)
    {
        if (TFCConfig.COMMON.enableNetworkDebugging())
        {
            cir.setReturnValue(NetworkRecipeParityCheck.decodeRecipe(buffer));
        }
    }

    @Inject(method = "toNetwork", at = @At("HEAD"))
    private static <T extends Recipe<?>> void beforeToNetwork(FriendlyByteBuf buffer, T recipe, CallbackInfo ci)
    {
        if (TFCConfig.COMMON.enableNetworkDebugging())
        {
            NetworkRecipeParityCheck.encodeRecipePrefix(buffer, recipe);
        }
    }
}
