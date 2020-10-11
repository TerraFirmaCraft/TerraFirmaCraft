package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow
    private ClientWorld level;

    @Unique
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

    /**
     * Redirect the call to {@link Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$renderSnowAndRain$getTemperature(Biome biome, BlockPos pos)
    {
        return Climate.toVanillaTemperature(Climate.getTemperature(level, pos));
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.RainType redirect$renderSnowAndRain$getPrecipitation(Biome biome, LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn)
    {
        mutablePos.set(xIn, yIn, zIn);
        return Climate.getPrecipitation(level, mutablePos);
    }

    /**
     * Redirect the call to {@link Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$tickRain$getTemperature(Biome biome, BlockPos pos)
    {
        return Climate.toVanillaTemperature(Climate.getTemperature(level, pos));
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.RainType redirect$tickRain$getPrecipitation(Biome biome, ActiveRenderInfo activeRenderInfo)
    {
        return Climate.getPrecipitation(level, activeRenderInfo.getBlockPosition());
    }
}
