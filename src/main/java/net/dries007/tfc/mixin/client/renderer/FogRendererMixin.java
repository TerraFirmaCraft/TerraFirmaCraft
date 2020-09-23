package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.client.TFCColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class FogRendererMixin
{
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"))
    private static int redirect$getFogColor(Biome biome, ActiveRenderInfo activeRenderInfoIn)
    {
        return TFCColors.getWaterFogColor(biome, new BlockPos(activeRenderInfoIn.getPosition()));
    }
}
