/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.dries007.tfc.common.blocks.wood.ITFCHangingSignBlock;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin
{
    @Mutable
    @Final
    @Shadow
    private ResourceLocation texture;

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZ)V", at = @At("TAIL"))
    public void inject$constructor(SignBlockEntity signBlockEntity, boolean isFrontText, boolean filter, CallbackInfo ci)
    {
        Block block = signBlockEntity.getBlockState().getBlock();
        if (block instanceof ITFCHangingSignBlock tfcSignBlock)
        {
            this.texture = tfcSignBlock.hangingSignGUITexture();
        }
    }
}
