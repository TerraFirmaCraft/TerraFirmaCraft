package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.world.biome.TFCBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin
{
    /**
     * Replace the call to {@link Biome#getWaterFogColor()} with one that has a position context
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"))
    private static int redirect$getWaterFogColor(Biome biome, ActiveRenderInfo activeRenderInfoIn)
    {
        IWorld world = Minecraft.getInstance().level;
        if (world != null && TFCBiomes.getExtension(world, biome) != null)
        {
            return TFCColors.getWaterFogColor(activeRenderInfoIn.getBlockPosition());
        }
        return biome.getWaterFogColor();
    }
}
