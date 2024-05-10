package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakingItemParticle.class)
public abstract class BreakingItemParticleMixin extends TextureSheetParticle {
    protected BreakingItemParticleMixin(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void inject$constructor(ClientLevel level, double x, double y, double z, ItemStack item, CallbackInfo ci) {
        int i = Minecraft.getInstance().getItemColors().getColor(item, 0);
        this.rCol *= (float)(i >> 16 & 255) / 255.0F;
        this.gCol *= (float)(i >> 8 & 255) / 255.0F;
        this.bCol *= (float)(i & 255) / 255.0F;
    }
}
