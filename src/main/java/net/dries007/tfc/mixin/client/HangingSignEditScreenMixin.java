package net.dries007.tfc.mixin.client;

import net.dries007.tfc.common.blocks.wood.ITFCHangingSignBlock;
import net.dries007.tfc.util.Metal;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin {
    @Mutable
    @Final
    @Shadow
    private ResourceLocation texture;
    @Inject(method="<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZ)V", at=@At("TAIL"))
    public void mixin(SignBlockEntity signBlockEntity, boolean isFrontText, boolean filter, CallbackInfo ci) {
        Block block = signBlockEntity.getBlockState().getBlock();
        if (block instanceof ITFCHangingSignBlock tfcSignBlock) {
            this.texture = new ResourceLocation(SignBlock.getWoodType(block).name() + ".png").withPrefix("textures/gui/hanging_signs/" + tfcSignBlock.metal().getPath() + "/");
        }
    }
}
