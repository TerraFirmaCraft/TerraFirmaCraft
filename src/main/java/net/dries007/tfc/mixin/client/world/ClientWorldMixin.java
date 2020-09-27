package net.dries007.tfc.mixin.client.world;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.world.biome.TFCBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientWorld.class)
public class ClientWorldMixin
{
    /**
     * Replace a call to {@link Biome#getSkyColor()} with one that has a position and world context
     */
    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getSkyColor()I"))
    private int getSkyColor(Biome biome, BlockPos pos)
    {
        IWorld world = (ClientWorld) (Object) this;
        if (TFCBiomes.getExtension(world, biome) != null)
        {
            return TFCColors.getSkyColor(pos);
        }
        return biome.getSkyColor();
    }
}
